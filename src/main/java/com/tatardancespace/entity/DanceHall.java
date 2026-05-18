package com.tatardancespace.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dance_halls")
public class DanceHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private Double price;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public DanceHall() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}