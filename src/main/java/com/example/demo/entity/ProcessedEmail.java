package com.example.demo.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_emails")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedEmail {

    @Id
    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "sender", nullable = false)
    private String from;

    private String tnxSource;
    private String tnxAmount;
    private String tnxId;
    private String tnxDate;
    private String tnxDetails;
    private String tnxCategory;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        this.processedAt = LocalDateTime.now();
    }
}