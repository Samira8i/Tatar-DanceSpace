package com.tatardancespace.service;

import com.tatardancespace.dto.request.ReviewRequest;
import com.tatardancespace.entity.DanceHall;
import com.tatardancespace.entity.Review;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.AccessDeniedException;
import com.tatardancespace.exception.AlreadyReviewedException;
import com.tatardancespace.exception.HallNotFoundException;
import com.tatardancespace.exception.ReviewNotFoundException;
import com.tatardancespace.repository.DanceHallRepository;
import com.tatardancespace.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final DanceHallRepository danceHallRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         DanceHallRepository danceHallRepository) {
        this.reviewRepository = reviewRepository;
        this.danceHallRepository = danceHallRepository;
    }

    public List<Review> getReviewsByHallId(Long hallId) {
        log.debug("Fetching reviews for hall: {}", hallId);
        return reviewRepository.findByHallIdOrderByCreatedAtDesc(hallId);
    }

    public Double getAverageRating(Long hallId) {
        log.debug("Calculating average rating for hall: {}", hallId);
        Double avg = reviewRepository.getAverageRatingByHallId(hallId);
        return avg != null ? Math.round(avg * 10) / 10.0 : 0.0;
    }

    public boolean hasUserReviewed(Long hallId, Long userId) {
        log.debug("Checking if user {} reviewed hall {}", userId, hallId);
        return reviewRepository.findByHallIdOrderByCreatedAtDesc(hallId)
                .stream()
                .anyMatch(r -> r.getUser().getId().equals(userId));
    }

    @Transactional
    public Review addReview(Long hallId, ReviewRequest request, User user) {
        log.info("Adding review for hall {} by user {}", hallId, user.getEmail());

        DanceHall hall = danceHallRepository.findById(hallId)
                .orElseThrow(() -> new HallNotFoundException(hallId));

        boolean alreadyReviewed = hasUserReviewed(hallId, user.getId());

        if (alreadyReviewed) {
            throw new AlreadyReviewedException();
        }

        Review review = new Review();
        review.setRating(request.getRating());
        review.setText(request.getText());
        review.setUser(user);
        review.setHall(hall);

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        log.info("Deleting review {} by user {}", reviewId, user.getEmail());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("удаления отзыва");
        }

        reviewRepository.delete(review);
        log.info("Review {} deleted", reviewId);
    }

    public boolean isHallOwner(Long hallId, Long userId) {
        DanceHall hall = danceHallRepository.findById(hallId).orElse(null);
        if (hall == null) return false;
        return hall.getOwner().getId().equals(userId);
    }
    public List<Review> getReviewsByUserId(Long userId) {
        log.debug("Fetching reviews for user: {}", userId);
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}