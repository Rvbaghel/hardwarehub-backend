package com.project.hardwarehub.controller;

import com.project.hardwarehub.dto.BlogResponseDTO;
import com.project.hardwarehub.dto.CreateBlogRequestDTO;
import com.project.hardwarehub.service.HardwareBlogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
public class HardwareBlogController {

    @Autowired
    private HardwareBlogService blogService;

    // 1. Create a blog post
    @PostMapping
    public ResponseEntity<?> createBlog(@Valid @RequestBody CreateBlogRequestDTO request) {
        try {
            BlogResponseDTO response = blogService.createBlog(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Fetch all blogs (feed)
    @GetMapping
    public ResponseEntity<List<BlogResponseDTO>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    // 3. Filter blogs by category (e.g., /api/blogs/category/MEMORY)
    @GetMapping("/category/{name}")
    public ResponseEntity<List<BlogResponseDTO>> getBlogsByCategory(@PathVariable("name") String name) {
        return ResponseEntity.ok(blogService.getBlogsByCategory(name));
    }

    // 4. Get single blog detail
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(blogService.getBlogById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}