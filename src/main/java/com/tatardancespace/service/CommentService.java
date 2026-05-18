package com.tatardancespace.service;

import com.tatardancespace.entity.Comment;
import com.tatardancespace.entity.Event;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.EventNotFoundException;
import com.tatardancespace.repository.CommentRepository;
import com.tatardancespace.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    public CommentService(CommentRepository commentRepository, EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Comment addComment(Long eventId, String text, User user) {
        log.info("Adding comment to event {} by user {}", eventId, user.getEmail());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setEvent(event);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByEventId(Long eventId) {
        log.debug("Fetching comments for event {}", eventId);
        return commentRepository.findByEventIdOrderByCreatedAtDesc(eventId);
    }

    public long getCommentsCount(Long eventId) {
        return commentRepository.findByEventIdOrderByCreatedAtDesc(eventId).size();
    }
}