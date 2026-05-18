package com.tatardancespace.controller.web;


import com.tatardancespace.service.DanceHallService;
import com.tatardancespace.service.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final EventService eventService;
    private final DanceHallService danceHallService;

    public AdminController(EventService eventService, DanceHallService danceHallService) {
        this.eventService = eventService;
        this.danceHallService = danceHallService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        model.addAttribute("pendingHallsCount", danceHallService.getPendingHalls().size());
        model.addAttribute("pendingEventsCount", eventService.getPendingEvents().size());
        return "admin/dashboard";
    }

    @GetMapping("/halls/pending")
    public String pendingHalls(Model model) {
        model.addAttribute("halls", danceHallService.getPendingHalls());
        return "admin/pending-halls";
    }

    @GetMapping("/events/pending")
    public String pendingEvents(Model model) {
        model.addAttribute("events", eventService.getPendingEvents());
        return "admin/pending-events";
    }

    @PostMapping("/halls/{id}/approve")
    public String approveHall(@PathVariable Long id) {
        System.out.println("=== APPROVE HALL CALLED, ID: " + id);
        danceHallService.approveHall(id);
        System.out.println("=== HALL APPROVED");
        return "redirect:/admin/halls/pending";
    }

    @PostMapping("/halls/{id}/reject")
    public String rejectHall(@PathVariable Long id) {
        System.out.println("=== REJECT HALL CALLED, ID: " + id);
        danceHallService.rejectHall(id);
        System.out.println("=== HALL REJECTED");
        return "redirect:/admin/halls/pending";
    }

    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Long id) {
        eventService.approveEvent(id);
        return "redirect:/admin/events/pending";
    }

    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id) {
        eventService.rejectEvent(id);
        return "redirect:/admin/events/pending";
    }

}




