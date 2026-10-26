package com.project.hardwarehub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // 6-digit numeric password

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Integer age;

    @Column(name = "mobile_number")
    private String mobileNumber;

    public User() {}

    public User(String username, String email, String password, String bio, Integer age, String mobileNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.age = age;
        this.mobileNumber = mobileNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
}