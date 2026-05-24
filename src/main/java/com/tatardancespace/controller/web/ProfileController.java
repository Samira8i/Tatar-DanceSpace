package com.tatardancespace.controller.web;

import com.tatardancespace.entity.DanceHall;
import com.tatardancespace.entity.Event;
import com.tatardancespace.entity.Review;
import com.tatardancespace.entity.User;
import com.tatardancespace.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final EventService eventService;
    private final DanceHallService danceHallService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    private static final List<String> AVATARS = List.of(
            "🕺", "💃", "🩰", "🎭", "🎵", "🎶", "⭐", "🌟",
            "💫", "✨", "🦋", "🌸", "🌺", "🔥", "💪", "🎯",
            "🏆", "🎉", "🥳", "💖", "❤️", "💜", "💙", "💚"
    );

    public ProfileController(UserService userService,
                             EventService eventService,
                             DanceHallService danceHallService,
                             ReviewService reviewService,
                             FavoriteService favoriteService) {
        this.userService = userService;
        this.eventService = eventService;
        this.danceHallService = danceHallService;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());

        List<Event> myEvents = eventService.getEventsByOrganizer(user.getId());
        List<DanceHall> myHalls = danceHallService.getHallsByOwner(user.getId());
        List<Review> myReviews = reviewService.getReviewsByUserId(user.getId());
        List<Event> favoriteEvents = favoriteService.getUserFavoriteEvents(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("myEvents", myEvents != null ? myEvents : List.of());
        model.addAttribute("myHalls", myHalls != null ? myHalls : List.of());
        model.addAttribute("myReviews", myReviews != null ? myReviews : List.of());
        model.addAttribute("favoriteEvents", favoriteEvents != null ? favoriteEvents : List.of());

        model.addAttribute("eventsCount", myEvents != null ? myEvents.size() : 0);
        model.addAttribute("hallsCount", myHalls != null ? myHalls.size() : 0);
        model.addAttribute("reviewsCount", myReviews != null ? myReviews.size() : 0);
        model.addAttribute("favoritesCount", favoriteEvents != null ? favoriteEvents.size() : 0);

        return "profile/index";
    }

    @GetMapping("/edit")
    public String editProfileForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("avatars", AVATARS);

        return "profile/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String avatarEmoji,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        userService.updateUsername(user.getId(), username);
        userService.updateAvatarEmoji(user.getId(), avatarEmoji);

        return "redirect:/profile?updated=true";
    }
}