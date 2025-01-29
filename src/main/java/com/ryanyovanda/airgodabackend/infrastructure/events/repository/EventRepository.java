package com.ryanyovanda.airgodabackend.infrastructure.events.repository;

import com.ryanyovanda.airgodabackend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
