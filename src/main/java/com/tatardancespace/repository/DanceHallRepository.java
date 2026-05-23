package com.tatardancespace.repository;

import com.tatardancespace.entity.DanceHall;
import com.tatardancespace.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanceHallRepository extends JpaRepository<DanceHall, Long> {

    List<DanceHall> findByOwnerId(Long ownerId);

    List<DanceHall> findByStatus(Status status);

    List<DanceHall> findAllByOrderByIdDesc();

    @Query("SELECT h FROM DanceHall h WHERE h.status = 'APPROVED' AND " +
            "(SELECT AVG(r.rating) FROM Review r WHERE r.hall = h) >= 4.0")
    List<DanceHall> findTopRatedHalls();
}