
package com.aja.mediaevents.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aja.mediaevents.Entity.GalleryEntity;
import com.aja.mediaevents.Exceptions.InvalidRequestException;
import com.aja.mediaevents.Exceptions.ResourceNotFoundException;
import com.aja.mediaevents.Repository.GalleryRepo;
import com.aja.mediaevents.dto.GalleryRequestDTO;
import com.aja.mediaevents.dto.GalleryResponseDTO;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class GalleryService {

    private static final Logger logger =
            LoggerFactory.getLogger(GalleryService.class);

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png", "image/webp");

    private final GalleryRepo galleryRepo;
    private final Cloudinary cloudinary;

    public GalleryService(
            GalleryRepo galleryRepo,
            Cloudinary cloudinary) {

        this.galleryRepo = galleryRepo;
        this.cloudinary = cloudinary;
    }

    public GalleryResponseDTO uploadImage(
            GalleryRequestDTO request) throws IOException {

        validateImage(request.getImage());

        if (request.getTitle() == null
                || request.getTitle().trim().isEmpty()) {
            throw new InvalidRequestException("Title cannot be empty");
        }

        if (request.getCategory() == null
                || request.getCategory().trim().isEmpty()) {
            throw new InvalidRequestException("Category cannot be empty");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                request.getImage().getBytes(),
                ObjectUtils.asMap("folder", "aja-gallery")
        );

        Object secureUrl = uploadResult.get("secure_url");
        Object publicId = uploadResult.get("public_id");

        if (secureUrl == null || publicId == null) {
            throw new IllegalStateException(
                    "Cloudinary did not return the uploaded image details"
            );
        }

        GalleryEntity gallery = new GalleryEntity();

        gallery.setTitle(request.getTitle().trim());
        gallery.setCategory(request.getCategory().trim());
        gallery.setImageUrl(secureUrl.toString());
        gallery.setPublicId(publicId.toString());

        try {
            GalleryEntity savedGallery = galleryRepo.save(gallery);
            return mapToResponse(savedGallery);

        } catch (RuntimeException ex) {

            // Database save failed after the Cloudinary upload.
            // Try to remove the uploaded image to avoid an orphan asset.
            try {
                cloudinary.uploader().destroy(
                        publicId.toString(),
                        ObjectUtils.emptyMap()
                );
            } catch (Exception cleanupException) {
                logger.error(
                        "Could not clean up Cloudinary image {} after database save failed",
                        publicId,
                        cleanupException
                );
            }

            throw ex;
        }
    }

    public List<GalleryResponseDTO> getAllImages() {

        List<GalleryEntity> images = galleryRepo.findAll();
        List<GalleryResponseDTO> response = new ArrayList<>();

        for (GalleryEntity image : images) {
            response.add(mapToResponse(image));
        }

        return response;
    }

    private GalleryResponseDTO mapToResponse(GalleryEntity image) {

        GalleryResponseDTO dto = new GalleryResponseDTO();

        dto.setId(image.getId());
        dto.setTitle(image.getTitle());
        dto.setImageUrl(image.getImageUrl());
        dto.setCategory(image.getCategory());
        dto.setCreatedAt(image.getCreatedAt());

        return dto;
    }

    public GalleryResponseDTO updateImage(
            Long id,
            String title,
            String category) {

        GalleryEntity image = galleryRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Gallery image not found"
                        ));

        if (title == null || title.trim().isEmpty()) {
            throw new InvalidRequestException("Title cannot be empty");
        }

        if (category == null || category.trim().isEmpty()) {
            throw new InvalidRequestException("Category cannot be empty");
        }

        image.setTitle(title.trim());
        image.setCategory(category.trim());

        GalleryEntity updatedImage = galleryRepo.save(image);

        return mapToResponse(updatedImage);
    }

    public void deleteImage(Long id) {

        GalleryEntity image = galleryRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Gallery image not found"
                        ));

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    image.getPublicId(),
                    ObjectUtils.emptyMap()
            );

            Object deletionStatus = result.get("result");

            // "not found" means the asset is already absent from Cloudinary.
            // It is safe to remove the stale database record.
            if (!"ok".equals(deletionStatus)
                    && !"not found".equals(deletionStatus)) {

                throw new IllegalStateException(
                        "Cloudinary image deletion was not successful"
                );
            }

            galleryRepo.delete(image);

        } catch (Exception ex) {

            logger.error(
                    "Failed to delete gallery image with ID: {}",
                    id,
                    ex
            );

            throw new RuntimeException(
                    "Failed to delete gallery image",
                    ex
            );
        }
    }

    private void validateImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Please select an image");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRequestException(
                    "Image size must not exceed 10 MB"
            );
        }

        Tika tika = new Tika();

        try (var inputStream = file.getInputStream()) {

            String detectedType = tika.detect(inputStream);

            if (!ALLOWED_TYPES.contains(detectedType)) {
                throw new InvalidRequestException(
                        "Only JPEG, PNG and WebP images are allowed"
                );
            }
        }
    }
}
