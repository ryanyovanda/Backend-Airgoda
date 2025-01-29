package com.ryanyovanda.airgodabackend.usecase.events.impl;

import com.ryanyovanda.airgodabackend.entity.Event;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.events.dto.CreateEventRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.events.dto.CreateEventResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.events.repository.CategoryRepository;
import com.ryanyovanda.airgodabackend.infrastructure.events.repository.EventRepository;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.events.CreateEventUsecase;
import org.springframework.stereotype.Service;

@Service
public class CreateEventUsecaseImpl implements CreateEventUsecase {
  private final EventRepository eventRepository;
  private final UsersRepository usersRepository;
  private final CategoryRepository categoryRepository;

  public CreateEventUsecaseImpl(EventRepository eventRepository, UsersRepository usersRepository, CategoryRepository categoryRepository) {
    this.eventRepository = eventRepository;
    this.usersRepository = usersRepository;
    this.categoryRepository = categoryRepository;
  }

  @Override
  public CreateEventResponseDTO create(CreateEventRequestDTO req) {
    Event event = req.toEntity();
    User assignedOrganizer = usersRepository.findById(req.getOrganizerId()).orElseThrow(() -> new RuntimeException("Organizer not found"));
    if (!assignedOrganizer.isOrganizer()) {
      throw new RuntimeException("User is not an organizer");
    }
    event.setOrganizer(assignedOrganizer);
    event.setCategory(categoryRepository.findById(req.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found")));
    Event savedEvent = eventRepository.save(event);
    return new CreateEventResponseDTO(
            savedEvent.getId(),
            savedEvent.getOrganizer(),
            savedEvent.getCategory(),
            savedEvent.getTitle(),
            savedEvent.getDescription(),
            savedEvent.getLocation(),
            savedEvent.getEventDate(),
            savedEvent.getIsFree(),
            savedEvent.getPrice(),
            savedEvent.getAllocatedSeats(),
            savedEvent.getAvailableSeats()
    );
  }
}
