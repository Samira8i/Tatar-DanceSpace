package com.tatardancespace.service;

import com.tatardancespace.dto.request.HallRequest;
import com.tatardancespace.entity.DanceHall;
import com.tatardancespace.entity.Status;
import com.tatardancespace.entity.User;
import com.tatardancespace.exception.AccessDeniedException;
import com.tatardancespace.exception.HallNotFoundException;
import com.tatardancespace.repository.DanceHallRepository;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DanceHallService {

    private static final Logger log = LoggerFactory.getLogger(DanceHallService.class);

    private final DanceHallRepository danceHallRepository;

    public DanceHallService(DanceHallRepository danceHallRepository) {
        this.danceHallRepository = danceHallRepository;
    }
    @Cacheable(value = "halls", key = "'approved'")
    public List<DanceHall> getApprovedHalls() {
        log.debug("идем в базу данных за одобренными залами");
        return danceHallRepository.findByStatus(Status.APPROVED);
    }

    public List<DanceHall> getPendingHalls() {
        log.debug("Fetching pending halls");
        return danceHallRepository.findByStatus(Status.PENDING);
    }

    public DanceHall getHallById(Long id) {
        log.debug("Fetching hall by id: {}", id);
        return danceHallRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hall not found with id: {}", id);
                    return new HallNotFoundException(id);
                });
    }

    @Transactional
    public DanceHall createHall(HallRequest request, User owner) {
        log.info("=== CREATING NEW HALL ===");
        log.info("Owner: {} (id: {})", owner.getEmail(), owner.getId());
        log.info("Hall data: name={}, address={}, price={}", request.getName(), request.getAddress(), request.getPrice());

        DanceHall hall = new DanceHall();
        hall.setName(request.getName());
        hall.setAddress(request.getAddress());
        hall.setPrice(request.getPrice());
        hall.setDescription(request.getDescription());
        hall.setImageUrl(request.getImageUrl());
        hall.setOwner(owner);
        hall.setStatus(Status.PENDING);

        log.info("Saving hall to database...");
        DanceHall saved = danceHallRepository.save(hall);
        log.info("Hall saved successfully with id: {}", saved.getId());

        return saved;
    }

    @CacheEvict(value = "halls", allEntries = true)
    @Transactional
    public DanceHall updateHall(Long id, HallRequest request, User user) {
        log.info("Updating hall {} by user {}", id, user.getEmail());

        DanceHall hall = getHallById(id);

        if (!hall.getOwner().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            log.warn("Access denied for user {} to update hall {}", user.getEmail(), id);
            throw new AccessDeniedException("редактирования зала");
        }

        hall.setName(request.getName());
        hall.setAddress(request.getAddress());
        hall.setPrice(request.getPrice());
        hall.setDescription(request.getDescription());
        hall.setImageUrl(request.getImageUrl());

        return danceHallRepository.save(hall);
    }

    @CacheEvict(value = "halls", allEntries = true)
    @Transactional
    public void deleteHall(Long id, User user) {
        log.info("Deleting hall {} by user {}", id, user.getEmail());

        DanceHall hall = getHallById(id);

        if (!hall.getOwner().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            log.warn("Access denied for user {} to delete hall {}", user.getEmail(), id);
            throw new AccessDeniedException("удаления зала");
        }

        danceHallRepository.delete(hall);
        log.info("Hall {} deleted", id);
    }

    @CacheEvict(value = "halls", allEntries = true)
    @Transactional
    public DanceHall approveHall(Long id) {
        log.info("Approving hall {}", id);
        DanceHall hall = getHallById(id);
        hall.setStatus(Status.APPROVED);
        return danceHallRepository.save(hall);
    }

    public void rejectHall(Long id) {
        log.info("Rejecting hall {}", id);
        danceHallRepository.deleteById(id);
    }
    public List<DanceHall> getHallsByOwner(Long ownerId) {
        log.debug("Fetching halls for owner: {}", ownerId);
        return danceHallRepository.findByOwnerId(ownerId);
    }
    public List<DanceHall> getHallsByStatus(Status status) {
        log.debug("Fetching halls for status: {}", status);
        return danceHallRepository.findByStatus(status);
    }
    public boolean isOwner(Long hallId, Long userId) {
        DanceHall hall = getHallById(hallId);
        return hall.getOwner().getId().equals(userId);
    }

    public boolean canEdit(Long hallId, User user) {
        DanceHall hall = getHallById(hallId);
        return hall.getOwner().getId().equals(user.getId()) || user.getRole().name().equals("ADMIN");
    }
}