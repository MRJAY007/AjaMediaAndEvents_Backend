
package com.aja.mediaevents.Services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aja.mediaevents.Entity.GalleryEntity;
import com.aja.mediaevents.Repository.GalleryRepo;
import com.aja.mediaevents.dto.GalleryRequestDTO;
import com.aja.mediaevents.dto.GalleryResponseDTO;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class GalleryService {

    private final GalleryRepo galleryRepo;
    private final Cloudinary cloudinary;

    public GalleryService(GalleryRepo galleryRepo, Cloudinary cloudinary) {
        this.galleryRepo = galleryRepo;
        this.cloudinary = cloudinary;
    }

    public GalleryResponseDTO uploadImage(GalleryRequestDTO request) throws IOException {

        if (request.getImage() == null || request.getImage().isEmpty()) {
            throw new RuntimeException("Please select an image");
        }

        if (request.getImage().getContentType() == null ||
            !request.getImage().getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        Map uploadResult = cloudinary.uploader().upload(
            request.getImage().getBytes(),
            ObjectUtils.asMap("folder", "aja-gallery")
        );

        GalleryEntity gallery = new GalleryEntity();

        gallery.setTitle(request.getTitle());
        gallery.setCategory(request.getCategory());
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
            .orElseThrow(() -> new RuntimeException("Gallery image not found"));

    if (title == null || title.trim().isEmpty()) {
        throw new RuntimeException("Title cannot be empty");
    }

    image.setTitle(title.trim());
    image.setCategory(category);

    GalleryEntity updatedImage = galleryRepo.save(image);

    return mapToResponse(updatedImage);
}

public void deleteImage(Long id) {

    GalleryEntity image = galleryRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Gallery image not found"));

    try {
        Map result = cloudinary.uploader().destroy(
                image.getPublicId(),
                ObjectUtils.emptyMap()
        );

        if (!"ok".equals(result.get("result"))) {
            throw new RuntimeException("Cloudinary image deletion failed");
        }

        galleryRepo.delete(image);

    } catch (Exception e) {
        throw new RuntimeException("Failed to delete gallery image", e);
    }
}

}
