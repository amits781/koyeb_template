package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.EmailExtraction;

/**
 * Repository is an interface that provides access to data in a database
 */
public interface EmailExtractionRepo extends JpaRepository<EmailExtraction, Long> {

}