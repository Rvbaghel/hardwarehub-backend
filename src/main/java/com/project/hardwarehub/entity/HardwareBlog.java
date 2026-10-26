package com.project.hardwarehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hardware_blogs")
public class HardwareBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 60)
    private String manufacturer;

    @Column(name = "primary_use_cases", nullable = false, length = 250)
    private String primaryUseCases;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "purchase_link", length = 500)
    private String purchaseLink; // Optional buyer/distributor link

    // Many blogs belong to one Category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Many blogs belong to one Author/User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Up to 2 image links
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "hardware_blog_images", joinColumns = @JoinColumn(name = "blog_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public HardwareBlog() {}

    public HardwareBlog(String title, String manufacturer, String primaryUseCases,
                        String content, String purchaseLink, Category category,
                        User author, List<String> imageUrls) {
        this.title = title;
        this.manufacturer = manufacturer;
        this.primaryUseCases = primaryUseCases;
        this.content = content;
        this.purchaseLink = purchaseLink;
        this.category = category;
        this.author = author;
        this.imageUrls = imageUrls;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}