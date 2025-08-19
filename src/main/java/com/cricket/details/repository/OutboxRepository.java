package com.cricket.details.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cricket.details.model.OutboxEvent;

import jakarta.persistence.LockModeType;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OutboxEvent o where o.status = 'PENDING' order by o.createdAt")
    public List<OutboxEvent> fetchPendingEvents(Pageable pageable);
}
