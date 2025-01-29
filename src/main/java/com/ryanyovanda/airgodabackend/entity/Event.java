package com.ryanyovanda.airgodabackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_id_gen")
  @SequenceGenerator(name = "events_id_gen", sequenceName = "events_events_id_seq", allocationSize = 1)
  @Column(name = "events_id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organizer_id", nullable = false)
  private User organizer;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Size(max = 100)
  @NotNull
  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @NotNull
  @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
  private String description;

  @Size(max = 100)
  @NotNull
  @Column(name = "location", nullable = false, length = 100)
  private String location;

  @NotNull
  @Column(name = "event_date", nullable = false)
  private OffsetDateTime eventDate;

  @NotNull
  @ColumnDefault("false")
  @Column(name = "is_free", nullable = false)
  private Boolean isFree = false;

  @Column(name = "price", precision = 15, scale = 2)
  private BigDecimal price;

  @NotNull
  @Column(name = "allocated_seats", nullable = false)
  private Integer allocatedSeats;

  @NotNull
  @Column(name = "available_seats", nullable = false)
  private Integer availableSeats;

  @NotNull
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @NotNull
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @Column(name = "deleted_at")
  private OffsetDateTime deletedAt;

}