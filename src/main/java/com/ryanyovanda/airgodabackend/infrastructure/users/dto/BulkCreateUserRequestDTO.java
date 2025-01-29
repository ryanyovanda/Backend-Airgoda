package com.ryanyovanda.airgodabackend.infrastructure.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkCreateUserRequestDTO {
  private List<CreateUserRequestDTO> users;
}
