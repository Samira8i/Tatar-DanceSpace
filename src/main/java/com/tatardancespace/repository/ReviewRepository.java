package com.tatardancespace.repository;

import com.tatardancespace.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHallIdOrderByCreatedAtDesc(Long hallId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hall.id = :hallId")
    Double getAverageRatingByHallId(@Param("hallId") Long hallId);
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
}