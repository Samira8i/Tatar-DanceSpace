package com.tatardancespace.controller.web;

import com.tatardancespace.dto.request.HallRequest;
import com.tatardancespace.dto.request.ReviewRequest;
import com.tatardancespace.entity.DanceHall;
import com.tatardancespace.entity.Status;
import com.tatardancespace.service.CustomUserDetails;
import com.tatardancespace.service.DanceHallService;
import com.tatardancespace.service.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/halls")
public class HallController {

    private static final Logger log = LoggerFactory.getLogger(HallController.class);
    private final DanceHallService danceHallService;
    private final ReviewService reviewService;

    public HallController(DanceHallService danceHallService, ReviewService reviewService) {
        this.danceHallService = danceHallService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String listHalls(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean isAdmin = userDetails != null && userDetails.getUser().getRole().name().equals("ADMIN");

        if (isAdmin) {
            model.addAttribute("halls", danceHallService.getAllHalls());
            model.addAttribute("showAdminFilters", true);
        } else {
            model.addAttribute("halls", danceHallService.getApprovedHalls());
            model.addAttribute("showAdminFilters", false);
        }

        model.addAttribute("isAdmin", isAdmin);
        return "halls/list";
    }

    @GetMapping("/{id}")
    public String hallDetails(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        model.addAttribute("hall", danceHallService.getHallById(id));
        model.addAttribute("reviews", reviewService.getReviewsByHallId(id));
        model.addAttribute("averageRating", reviewService.getAverageRating(id));
        model.addAttribute("reviewRequest", new ReviewRequest());

        if (userDetails != null) {
            model.addAttribute("hasReviewed", reviewService.hasUserReviewed(id, userDetails.getId()));
            model.addAttribute("isOwner", danceHallService.isOwner(id, userDetails.getId()));
            model.addAttribute("isAdmin", userDetails.getUser().getRole().name().equals("ADMIN"));
        } else {
            model.addAttribute("hasReviewed", false);
            model.addAttribute("isOwner", false);
            model.addAttribute("isAdmin", false);
        }

        return "halls/details";
    }

    @GetMapping("/add")
    public String addHallForm(Model model) {
        model.addAttribute("hallRequest", new HallRequest());
        return "halls/add";
    }

    @PostMapping("/add")
    public String addHall(@Valid @ModelAttribute("hallRequest") HallRequest request,
                          BindingResult bindingResult,
                          @AuthenticationPrincipal CustomUserDetails userDetails,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Пожалуйста, исправьте ошибки в форме");
            return "halls/add";
        }

        try {
            danceHallService.createHall(request, userDetails.getUser());
            return "redirect:/halls?success=true";
        } catch (Exception e) {
            log.error("Error creating hall: ", e);
            model.addAttribute("error", "Ошибка при создании зала: " + e.getMessage());
            return "halls/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editHallForm(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        if (!danceHallService.canEdit(id, userDetails.getUser())) {
            return "redirect:/halls?error=access_denied";
        }

        DanceHall hall = danceHallService.getHallById(id);
        model.addAttribute("hall", hall);

        HallRequest request = new HallRequest();
        request.setName(hall.getName());
        request.setAddress(hall.getAddress());
        request.setPrice(hall.getPrice());
        request.setDescription(hall.getDescription());
        request.setImageUrl(hall.getImageUrl());

        model.addAttribute("hallRequest", request);
        return "halls/edit";
    }

    @PostMapping("/{id}/edit")
    public String editHall(@PathVariable Long id,
                           @Valid @ModelAttribute("hallRequest") HallRequest request,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "halls/edit";
        }

        try {
            danceHallService.updateHall(id, request, userDetails.getUser());
            return "redirect:/halls/" + id + "?updated=true";
        } catch (Exception e) {
            return "redirect:/halls/" + id + "?error=update_failed";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteHall(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            danceHallService.deleteHall(id, userDetails.getUser());
            return "redirect:/halls?deleted=true";
        } catch (Exception e) {
            return "redirect:/halls?error=delete_failed";
        }
    }

    @GetMapping("/status/{status}")
    public String filterByStatus(@PathVariable Status status,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {
        if (userDetails == null || !userDetails.getUser().getRole().name().equals("ADMIN")) {
            return "redirect:/halls?error=access_denied";
        }

        model.addAttribute("halls", danceHallService.getHallsByStatus(status));
        model.addAttribute("currentStatus", status);
        model.addAttribute("title", "Залы - " + status);
        model.addAttribute("isAdmin", true);
        model.addAttribute("showAdminFilters", true);
        return "halls/list";
    }
}