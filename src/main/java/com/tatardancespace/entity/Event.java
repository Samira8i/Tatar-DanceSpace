package com.tatardancespace.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime dateTime;
    private String style;
    private String place;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    public Event() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getOrganizer() { return organizer; }
    public void setOrganizer(User organizer) { this.organizer = organizer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }
}