package com.tatardancespace.service;

import com.tatardancespace.entity.Event;
import com.tatardancespace.entity.Like;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.EventNotFoundException;
import com.tatardancespace.repository.EventRepository;
import com.tatardancespace.repository.LikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private static final Logger log = LoggerFactory.getLogger(LikeService.class);

    private final LikeRepository likeRepository;
    private final EventRepository eventRepository;

    public LikeService(LikeRepository likeRepository, EventRepository eventRepository) {
        this.likeRepository = likeRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public boolean toggleLike(Long eventId, User user) {
        log.info("Toggling like for event {} by user {}", eventId, user.getEmail());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        boolean isLiked = likeRepository.existsByUserIdAndEventId(user.getId(), eventId);

        if (isLiked) {
            likeRepository.deleteByUserIdAndEventId(user.getId(), eventId);
            log.info("Like removed for event {}", eventId);
            return false;
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setEvent(event);
            likeRepository.save(like);
            log.info("Like added for event {}", eventId);
            return true;
        }
    }

    public long getLikesCount(Long eventId) {
        return likeRepository.countByEventId(eventId);
    }

    public boolean isLikedByUser(Long eventId, Long userId) {
        return likeRepository.existsByUserIdAndEventId(userId, eventId);
    }
}