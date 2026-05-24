package com.tatardancespace.controller.api;

import com.tatardancespace.dto.response.HallStatsResponse;
import com.tatardancespace.dto.response.ReviewResponse;
import com.tatardancespace.entity.Review;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.AccessDeniedException;
import com.tatardancespace.exception.AlreadyReviewedException;
import com.tatardancespace.service.ReviewService;
import com.tatardancespace.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/halls")
public class ReviewApiController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewApiController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ReviewResponse> addHallReview(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String text,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Review review = reviewService.addReview(id, rating, text, user);

            ReviewResponse response = new ReviewResponse(
                    review.getId(),
                    user.getUsername(),
                    review.getRating(),
                    review.getText(),
                    review.getCreatedAt()
            );
            return ResponseEntity.ok(response);

        } catch (AlreadyReviewedException e) {
            ReviewResponse errorResponse = new ReviewResponse(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<HallStatsResponse> getHallStats(@PathVariable Long id) {
        double averageRating = reviewService.getAverageRating(id);
        int reviewsCount = reviewService.getReviewsByHallId(id).size();

        HallStatsResponse response = new HallStatsResponse(averageRating, reviewsCount);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        Review review = reviewService.getReviewById(reviewId);

        if (!review.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new AccessDeniedException("удаления этого отзыва");
        }

        reviewService.deleteReview(reviewId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Отзыв удалён");
        return ResponseEntity.ok(response);
    }
}