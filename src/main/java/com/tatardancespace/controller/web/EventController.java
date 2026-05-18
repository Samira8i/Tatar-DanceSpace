package com.tatardancespace.controller.web;

import com.tatardancespace.dto.request.EventRequest;
import com.tatardancespace.entity.Status;
import com.tatardancespace.service.CustomUserDetails;
import com.tatardancespace.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getApprovedEvents());
        return "events/list";
    }

    @GetMapping("/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getEventById(id));
        return "events/details";
    }

    @GetMapping("/add")
    public String addEventForm(Model model) {
        model.addAttribute("eventRequest", new EventRequest());
        return "events/add";
    }

    @PostMapping("/add")
    public String addEvent(@Valid @ModelAttribute("eventRequest") EventRequest request,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "events/add";
        }

        try {
            eventService.createEvent(request, userDetails.getUser());
            return "redirect:/events?success=true";
        } catch (Exception e) {
            log.error("Error creating event: ", e);
            model.addAttribute("error", e.getMessage());
            return "events/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editEventForm(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        if (!eventService.canEdit(id, userDetails.getUser())) {
            return "redirect:/events?error=access_denied";
        }

        model.addAttribute("event", eventService.getEventById(id));
        model.addAttribute("eventRequest", new EventRequest());
        return "events/edit";
    }

    @PostMapping("/{id}/edit")
    public String editEvent(@PathVariable Long id,
                            @Valid @ModelAttribute("eventRequest") EventRequest request,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "events/edit";
        }

        try {
            eventService.updateEvent(id, request, userDetails.getUser());
            return "redirect:/events/" + id + "?updated=true";
        } catch (Exception e) {
            return "redirect:/events/" + id + "/edit?error=true";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            eventService.deleteEvent(id, userDetails.getUser());
            return "redirect:/events?deleted=true";
        } catch (Exception e) {
            return "redirect:/events?error=delete_failed";
        }
    }

    @GetMapping("/status/{status}")
    public String filterByStatus(@PathVariable Status status, Model model) {
        model.addAttribute("events", eventService.getEventsByStatus(status));
        model.addAttribute("currentStatus", status);
        model.addAttribute("title", "События - " + status);
        return "events/list";
    }
}