
package com.aja.mediaevents.Services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tika.Tika;
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

    private final GalleryRepo galleryRepo;
    private final Cloudinary cloudinary;
    
    private static final Logger logger =
            LoggerFactory.getLogger(GalleryService.class);

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png", "image/webp");

    public GalleryService(GalleryRepo galleryRepo, Cloudinary cloudinary) {
        this.galleryRepo = galleryRepo;
        this.cloudinary = cloudinary;
    }

    public GalleryResponseDTO uploadImage(GalleryRequestDTO request) throws IOException {

        validateImage(request.getImage());

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new InvalidRequestException("Title cannot be empty");
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new InvalidRequestException("Category cannot be empty");
        }

        Map uploadResult = cloudinary.uploader().upload(
                request.getImage().getBytes(),
                ObjectUtils.asMap("folder", "aja-gallery")
        );

        GalleryEntity gallery = new GalleryEntity();

        gallery.setTitle(request.getTitle().trim());
        gallery.setCategory(request.getCategory().trim());
        gallery.setImageUrl(uploadResult.get("secure_url").toString());
        gallery.setPublicId(uploadResult.get("public_id").toString());

        GalleryEntity savedGallery = galleryRepo.save(gallery);

        return mapToResponse(savedGallery);
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

    public GalleryResponseDTO updateImage(Long id, String title, String category) {

        GalleryEntity image = galleryRepo.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Gallery image not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gallery image not found"
                ));

        try {
            @SuppressWarnings("unchecked")
			Map<String, Object> result = cloudinary.uploader().destroy(
                    image.getPublicId(),
                    ObjectUtils.emptyMap()
            );

            if (!"ok".equals(result.get("result"))) {
                throw new RuntimeException("Cloudinary image deletion failed");
            }

            galleryRepo.delete(image);

        } 
        catch (Exception e) {
            logger.error("Failed to delete gallery image with ID: {}", id, e);
            throw new RuntimeException("Failed to delete gallery image", e);
        }
    }

    private void validateImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("Please select an image");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRequestException("Image size must not exceed 10 MB");
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
