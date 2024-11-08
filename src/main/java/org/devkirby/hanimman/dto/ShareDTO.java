package org.devkirby.hanimman.dto;

import java.time.Instant;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareDTO {
    private Integer id;
    private String title;
    private String content;
    private Integer views;
    private Instant createdAt;
    private Instant modifiedAt;
    private Instant deletedAt;
    private Integer regionId; // Region의 ID만 전달
    private String location;
    private Instant locationDate;
    private String item;
    private Integer quantity;
    private Boolean isEnd;
    private Integer userId; // User의 ID만 전달
}
