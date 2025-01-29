package com.ryanyovanda.airgodabackend.usecase.events.impl;

import com.ryanyovanda.airgodabackend.entity.Category;
import com.ryanyovanda.airgodabackend.entity.Event;
import com.ryanyovanda.airgodabackend.entity.Role;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.events.dto.CreateEventRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.events.dto.CreateEventResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.events.repository.CategoryRepository;
import com.ryanyovanda.airgodabackend.infrastructure.events.repository.EventRepository;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreateEventUsecaseImplTests {
  @Mock
  private EventRepository eventRepository;

  @Mock
  private UsersRepository usersRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CreateEventUsecaseImpl createEventUsecase;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  //  Case 1: Test create event successfully
  @Test
  void testCreateEventSuccessfully() {
    //  Arrange
    CreateEventRequestDTO req = new CreateEventRequestDTO();
    req.setOrganizerId(1L);
    req.setCategoryId(1L);
    req.setTitle("Event Title");
    req.setDescription("Event Description");
    req.setLocation("Event Location");
    req.setEventDate("2024-12-03T10:15:30+07:00");
    req.setIsFree(false);
    req.setPrice(new BigDecimal(100000));
    req.setAllocatedSeats(1000);
    req.setAvailableSeats(1000);

    Category existingCategory = new Category();
    existingCategory.setId(1L);

    Role role = new Role();
    role.setName("ORGANIZER");

    User organizer = new User();
    organizer.setId(1L);
    organizer.getRoles().add(role);

    Event savedEvent = req.toEntity();
    savedEvent.setId(1L);
    savedEvent.setOrganizer(organizer);
    savedEvent.setCategory(existingCategory);

    when(usersRepository.findById(1L)).thenReturn(Optional.of(organizer));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
    when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

    //  Act
    CreateEventResponseDTO res = createEventUsecase.create(req);

    //  Assert
    assertEquals(1L, res.getId());
    assertEquals(organizer.getId(), res.getOrganizer().getId());
    assertEquals(existingCategory.getId(), res.getCategory().getId());
    assertEquals(req.getTitle(), res.getTitle());
    assertEquals(req.getDescription(), res.getDescription());
    assertEquals(req.getLocation(), res.getLocation());
    assertEquals(req.getIsFree(), res.getIsFree());
    assertEquals(req.getPrice(), res.getPrice());
  }

  //  Case 2: Test create event with invalid organizer
  //  Case 3: Test create event with invalid category
  //  Case 4: Test create event with invalid title
  //  Case 5: Test create event with free event and price
}
