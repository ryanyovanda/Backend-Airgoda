package com.ryanyovanda.airgodabackend.usecase.events;

import com.ryanyovanda.airgodabackend.infrastructure.events.dto.CreateEventRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.events.dto.CreateEventResponseDTO;

public interface CreateEventUsecase {
  CreateEventResponseDTO create(CreateEventRequestDTO req);
}
