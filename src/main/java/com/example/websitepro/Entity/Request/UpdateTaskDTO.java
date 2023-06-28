package com.example.websitepro.Entity.Request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskDTO {

    private Long id;

    private String name;

    private String description;

    private Date startDate;

    private Date endDate;

    private LocalDateTime loopTime;

    private Long priority = 1L;

    private Double taskListOrder;

    private String typeCode;

    private Long typeGroup = 1L;

    private Long totalCredit = 0L;

    private Long isCredited = 0L;

    private Long routineDate = 1L;

    private String routineType;

    private Boolean pinned = false;

    private Boolean isCompleted = false;

    private Boolean isDeleted = false;

    private Long projectParentId;

}
