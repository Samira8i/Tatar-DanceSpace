package com.tatardancespace.controller.web;

import com.tatardancespace.dto.request.ReviewRequest;
import com.tatardancespace.entity.User;
import com.tatardancespace.service.ReviewService;
import com.tatardancespace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/hall/{hallId}/add")
    public String addReview(@PathVariable Long hallId,
                            @Valid @ModelAttribute("reviewRequest") ReviewRequest request,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            return "redirect:/halls/" + hallId + "?error=validation";
        }

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            reviewService.addReview(hallId, request, user);
            return "redirect:/halls/" + hallId + "?review_added=true";
        } catch (RuntimeException e) {
            return "redirect:/halls/" + hallId + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId,
                               @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            reviewService.deleteReview(reviewId, user);
            return "redirect:/halls/" + reviewId + "?review_deleted=true";
        } catch (RuntimeException e) {
            return "redirect:/halls/?error=" + e.getMessage();
        }
    }
}