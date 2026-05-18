package com.tatardancespace.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    @Column(length = 1000)
    private String text;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private DanceHall hall;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Review() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public DanceHall getHall() { return hall; }
    public void setHall(DanceHall hall) { this.hall = hall; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}