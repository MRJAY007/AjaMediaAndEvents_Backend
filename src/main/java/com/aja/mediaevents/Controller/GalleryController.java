
package com.aja.mediaevents.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.aja.mediaevents.Services.GalleryService;
import com.aja.mediaevents.dto.GalleryRequestDTO;
import com.aja.mediaevents.dto.GalleryResponseDTO;

@RestController
@RequestMapping("/api")
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    // Admin: Upload image
    @PostMapping(value = "/admin/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GalleryResponseDTO> uploadImage(
            @ModelAttribute GalleryRequestDTO request) throws IOException {

        GalleryResponseDTO response = galleryService.uploadImage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Public: Get all gallery images
    @GetMapping("/gallery")
    public ResponseEntity<List<GalleryResponseDTO>> getAllImages() {

        List<GalleryResponseDTO> images = galleryService.getAllImages();

        return ResponseEntity.ok(images);
    }

    // Admin: Update image title and category
    @PutMapping("/admin/gallery/{id}")
    public ResponseEntity<GalleryResponseDTO> updateImage(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String category) {

        GalleryResponseDTO updatedImage =
                galleryService.updateImage(id, title, category);

        return ResponseEntity.ok(updatedImage);
    }

    // Admin: Delete image
    @DeleteMapping("/admin/gallery/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {

        galleryService.deleteImage(id);

        return ResponseEntity.ok("Gallery image deleted successfully");
    }

    
}
