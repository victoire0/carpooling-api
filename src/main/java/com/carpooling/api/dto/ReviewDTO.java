package com.carpooling.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Long rideId;
    private Long reviewerId;
    private Long reviewedId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
} 