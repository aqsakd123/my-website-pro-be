package com.example.websitepro.Entity.Request;

import lombok.*;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskChangeStatusRequest {
    private Long id;
    private String action;
    private Double position;
}
