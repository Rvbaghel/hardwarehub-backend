package com.project.hardwarehub.repository;

import com.project.hardwarehub.entity.HardwareBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HardwareBlogRepository extends JpaRepository<HardwareBlog, Long> {
    // Newest blogs first
    List<HardwareBlog> findAllByOrderByCreatedAtDesc();

    // Filter blogs by category name
    List<HardwareBlog> findByCategory_NameIgnoreCaseOrderByCreatedAtDesc(String categoryName);
}