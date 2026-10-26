package com.project.hardwarehub.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BlogResponseDTO {
    private Long id;
    private String title;
    private String manufacturer;
    private String primaryUseCases;
    private String content;
    private String purchaseLink;
    private String categoryName;
    private Long categoryId;
    private String authorUsername;
    private Long authorId;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    public BlogResponseDTO(Long id, String title, String manufacturer, String primaryUseCases,
                           String content, String purchaseLink, String categoryName,
                           Long categoryId, String authorUsername, Long authorId,
                           List<String> imageUrls, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.manufacturer = manufacturer;
        this.primaryUseCases = primaryUseCases;
        this.content = content;
        this.purchaseLink = purchaseLink;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.authorUsername = authorUsername;
        this.authorId = authorId;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getManufacturer() { return manufacturer; }
    public String getPrimaryUseCases() { return primaryUseCases; }
    public String getContent() { return content; }
    public String getPurchaseLink() { return purchaseLink; }
    public String getCategoryName() { return categoryName; }
    public Long getCategoryId() { return categoryId; }
    public String getAuthorUsername() { return authorUsername; }
    public Long getAuthorId() { return authorId; }
    public List<String> getImageUrls() { return imageUrls; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}