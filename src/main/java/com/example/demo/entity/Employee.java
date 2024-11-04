package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An entity class represents a table in a relational database
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "employee")
public class Employee {

  @Id
  private Integer id;
  private String firstName;
  private String lastName;
  private Integer age;
  private String designation;
  private String phoneNumber;
  private LocalDate joinedOn;
  private String address;
  private LocalDate dateOfBirth;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}