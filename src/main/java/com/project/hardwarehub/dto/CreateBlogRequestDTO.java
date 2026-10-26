package com.project.hardwarehub.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class CreateBlogRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 120, message = "Title must be between 10 and 120 characters")
    private String title;

    @NotBlank(message = "Manufacturer is required")
    @Size(min = 2, max = 60, message = "Manufacturer must be between 2 and 60 characters")
    private String manufacturer;

    @NotBlank(message = "Primary use cases are required")
    @Size(min = 10, max = 250, message = "Primary use cases must be between 10 and 250 characters")
    private String primaryUseCases;

    @NotBlank(message = "Content is required")
    @Size(min = 100, max = 10000, message = "Content must be between 100 and 10,000 characters")
    private String content;

    @Size(max = 500, message = "Buyer link cannot exceed 500 characters")
    private String purchaseLink; // Optional

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @Size(max = 2, message = "You can attach a maximum of 2 image links")
    private List<String> imageUrls; // List of URLs

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getPrimaryUseCases() { return primaryUseCases; }
    public void setPrimaryUseCases(String primaryUseCases) { this.primaryUseCases = primaryUseCases; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPurchaseLink() { return purchaseLink; }
    public void setPurchaseLink(String purchaseLink) { this.purchaseLink = purchaseLink; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}