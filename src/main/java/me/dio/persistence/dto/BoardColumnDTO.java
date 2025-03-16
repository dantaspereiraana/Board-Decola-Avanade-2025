package me.dio.persistence.dto;

import me.dio.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind, int cardsAmount) {
}
