package com.tatardancespace.service;

import com.tatardancespace.entity.Event;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.UserNotFoundException;
import com.tatardancespace.repository.EventRepository;
import com.tatardancespace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteService.class);

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public FavoriteService(UserRepository userRepository,
                           EventRepository eventRepository,
                           EventService eventService) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    public boolean isFavorite(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        return user.getFavoriteEvents().stream()
                .anyMatch(event -> event.getId().equals(eventId));
    }

    public long getFavoritesCount(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return 0;
        return event.getFavoritedByUsers().size();
    }


    public List<Event> getUserFavoriteEvents(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return user.getFavoriteEvents();
    }


    @Transactional
    @CacheEvict(value = {"events", "eventStats"}, key = "#eventId")
    public void addFavorite(Long userId, Long eventId) {
        log.info("User {} adding favorite event {}", userId, eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Event event = eventService.getEventById(eventId);

        if (user.getFavoriteEvents().contains(event)) {
            log.warn("User {} already has event {} in favorites", userId, eventId);
            return;
        }

        user.getFavoriteEvents().add(event);
        event.getFavoritedByUsers().add(user);

        userRepository.save(user);
        eventRepository.save(event);

        log.info("Event {} added to favorites for user {}", eventId, userId);
    }


    @Transactional
    @CacheEvict(value = {"events", "eventStats"}, key = "#eventId")
    public void removeFavorite(Long userId, Long eventId) {
        log.info("User {} removing favorite event {}", userId, eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Event event = eventService.getEventById(eventId);

        user.getFavoriteEvents().remove(event);
        event.getFavoritedByUsers().remove(user);

        userRepository.save(user);
        eventRepository.save(event);

        log.info("Event {} removed from favorites for user {}", eventId, userId);
    }

    @Transactional
    public boolean toggleFavorite(Long userId, Long eventId) {
        if (isFavorite(userId, eventId)) {
            removeFavorite(userId, eventId);
            return false;
        } else {
            addFavorite(userId, eventId);
            return true;
        }
    }
}