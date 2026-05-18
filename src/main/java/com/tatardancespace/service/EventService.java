package com.tatardancespace.service;

import com.tatardancespace.dto.request.EventRequest;
import com.tatardancespace.entity.Event;
import com.tatardancespace.entity.Status;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.AccessDeniedException;
import com.tatardancespace.exception.EventNotFoundException;
import com.tatardancespace.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;

import java.util.List;
import org.slf4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Cacheable(value = "events", key = "'approved'")
    public List<Event> getApprovedEvents() {
        log.debug("Fetching approved events");
        return eventRepository.findByStatus(Status.APPROVED);
    }

    public List<Event> getPendingEvents() {
        log.debug("Fetching pending events");
        return eventRepository.findByStatus(Status.PENDING);
    }

    public List<Event> getEventsByOrganizer(Long organizerId) {
        log.debug("Fetching events for organizer: {}", organizerId);
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Event getEventById(Long id) {
        log.debug("Fetching event by id: {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public Event createEvent(EventRequest request, User organizer) {
        log.info("Creating new event for organizer: {}", organizer.getEmail());

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDateTime(request.getDateTime());
        event.setStyle(request.getStyle());
        event.setPlace(request.getPlace());
        event.setDescription(request.getDescription());
        event.setImageUrl(request.getImageUrl());
        event.setOrganizer(organizer);
        event.setStatus(Status.PENDING);

        Event saved = eventRepository.save(event);
        log.info("Event saved with id: {}", saved.getId());
        return saved;
    }

    @CacheEvict(value = "events", allEntries = true)
    @Transactional
    public Event updateEvent(Long id, EventRequest request, User user) {
        log.info("Updating event {} by user {}", id, user.getEmail());

        Event event = getEventById(id);

        if (!event.getOrganizer().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("редактирования события");
        }

        event.setTitle(request.getTitle());
        event.setDateTime(request.getDateTime());
        event.setStyle(request.getStyle());
        event.setPlace(request.getPlace());
        event.setDescription(request.getDescription());
        event.setImageUrl(request.getImageUrl());

        return eventRepository.save(event);
    }

    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(Long id, User user) {
        log.info("Deleting event {} by user {}", id, user.getEmail());

        Event event = getEventById(id);

        if (!event.getOrganizer().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("удаления зала");
        }

        eventRepository.delete(event);
        log.info("Event {} deleted", id);
    }

    @CacheEvict(value = "events", allEntries = true)
    @Transactional
    public Event approveEvent(Long id) {
        Event event = getEventById(id);
        event.setStatus(Status.APPROVED);
        return eventRepository.save(event);
    }

    @CacheEvict(value = "events", allEntries = true)
    public void rejectEvent(Long id) {
        eventRepository.deleteById(id);
    }


    public List<Event> getEventsByStatus(Status status) {
        log.debug("Fetching events by status: {}", status);
        return eventRepository.findByStatus(status);
    }
    public boolean canEdit(Long eventId, User user) {
        Event event = getEventById(eventId);
        return event.getOrganizer().getId().equals(user.getId()) || user.getRole().name().equals("ADMIN");
    }
}
