package me.dio.persistence.dao;

import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id,
                             String title,
                             String description,
                             boolean blocked,
                             OffsetDateTime blockedAt,
                             String blockReason,
                             int blockedAmount,
                             Long columnId,
                             String columnName) {

}
