package com.ryanyovanda.airgodabackend.infrastructure.events.dto;

import com.ryanyovanda.airgodabackend.entity.Event;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequestDTO {
  @NotNull
  private Long organizerId;

  @NotNull
  private Long categoryId;

  @Size(max = 100)
  @NotNull
  private String title;

  @NotNull
  private String description;

  @Size(max = 100)
  @NotNull
  private String location;

  @NotNull
  private String eventDate;

  @NotNull
  private Boolean isFree = false;

  private BigDecimal price;

  @NotNull
  private Integer allocatedSeats;

  @NotNull
  private Integer availableSeats;

  public Event toEntity() {
    Event event = new Event();
    event.setTitle(title);
    event.setDescription(description);
    event.setLocation(location);
    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    OffsetDateTime eventDate = OffsetDateTime.parse(this.eventDate, formatter);
    event.setEventDate(eventDate);
    event.setIsFree(isFree);
    event.setPrice(price);
    event.setAllocatedSeats(allocatedSeats);
    event.setAvailableSeats(availableSeats);
    return event;
  }
}
