package com.project.hardwarehub.service;

import com.project.hardwarehub.dto.BlogResponseDTO;
import com.project.hardwarehub.dto.CreateBlogRequestDTO;
import com.project.hardwarehub.entity.Category;
import com.project.hardwarehub.entity.HardwareBlog;
import com.project.hardwarehub.entity.User;
import com.project.hardwarehub.repository.CategoryRepository;
import com.project.hardwarehub.repository.HardwareBlogRepository;
import com.project.hardwarehub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HardwareBlogService {

    @Autowired
    private HardwareBlogRepository blogRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public BlogResponseDTO createBlog(CreateBlogRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.getCategoryId()));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getAuthorId()));

        HardwareBlog blog = new HardwareBlog(
                request.getTitle(),
                request.getManufacturer(),
                request.getPrimaryUseCases(),
                request.getContent(),
                request.getPurchaseLink(),
                category,
                author,
                request.getImageUrls() != null ? request.getImageUrls() : List.of()
        );

        HardwareBlog saved = blogRepository.save(blog);
        return mapToDTO(saved);
    }

    public List<BlogResponseDTO> getAllBlogs() {
        return blogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BlogResponseDTO> getBlogsByCategory(String categoryName) {
        return blogRepository.findByCategory_NameIgnoreCaseOrderByCreatedAtDesc(categoryName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BlogResponseDTO getBlogById(Long id) {
        HardwareBlog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog post not found with ID: " + id));
        return mapToDTO(blog);
    }

    private BlogResponseDTO mapToDTO(HardwareBlog blog) {
        return new BlogResponseDTO(
                blog.getId(),
                blog.getTitle(),
                blog.getManufacturer(),
                blog.getPrimaryUseCases(),
                blog.getContent(),
                blog.getPurchaseLink(),
                blog.getCategory().getName(),
                blog.getCategory().getId(),
                blog.getAuthor().getUsername(),
                blog.getAuthor().getId(),
                blog.getImageUrls(),
                blog.getCreatedAt()
        );
    }
}