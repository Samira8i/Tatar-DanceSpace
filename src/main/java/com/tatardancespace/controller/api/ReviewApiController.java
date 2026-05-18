package com.tatardancespace.controller.api;

import com.tatardancespace.dto.request.ReviewRequest;
import com.tatardancespace.dto.response.HallStatsResponse;
import com.tatardancespace.dto.response.ReviewResponse;
import com.tatardancespace.entity.Review;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.AlreadyReviewedException;
import com.tatardancespace.service.ReviewService;
import com.tatardancespace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/halls")
@Tag(name = "Reviews API", description = "Управление отзывами на залы")
public class ReviewApiController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewApiController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/{id}/review")
    @Operation(summary = "Добавить отзыв на зал")
    public ResponseEntity<?> addHallReview(
            @PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam(required = false) String text,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());

            ReviewRequest request = new ReviewRequest();
            request.setRating(rating);
            request.setText(text);

            Review review = reviewService.addReview(id, request, user);

            ReviewResponse response = new ReviewResponse(
                    review.getId(),
                    user.getUsername(),
                    review.getRating(),
                    review.getText(),
                    review.getCreatedAt()
            );

            return ResponseEntity.ok(response);

        } catch (AlreadyReviewedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Получить статистику зала")
    public ResponseEntity<HallStatsResponse> getHallStats(@PathVariable Long id) {
        double averageRating = reviewService.getAverageRating(id);
        int reviewsCount = reviewService.getReviewsByHallId(id).size();

        HallStatsResponse response = new HallStatsResponse(averageRating, reviewsCount);
        return ResponseEntity.ok(response);
    }
}