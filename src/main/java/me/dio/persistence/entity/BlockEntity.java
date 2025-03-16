package me.dio.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

import static me.dio.persistence.entity.BoardColumnKindEnum.INITIAL;

@Data
public class BlockEntity {

    private Long id;
    private OffsetDateTime blockedAt;
    private String blockReason;
    private OffsetDateTime unblockedAt;
    private String unblockReason;


}