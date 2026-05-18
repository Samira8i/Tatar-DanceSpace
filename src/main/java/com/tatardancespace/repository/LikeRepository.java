package com.tatardancespace.repository;

import com.tatardancespace.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.event.id = :eventId")
    void deleteByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    long countByEventId(Long eventId);
}