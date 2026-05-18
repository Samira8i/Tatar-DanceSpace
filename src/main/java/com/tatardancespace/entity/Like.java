package com.tatardancespace.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Like() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}