package com.example.websitepro.Entity.DTO;

import com.example.websitepro.Entity.TaskCheckList;

import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCheckListDTO {

    private Long id;

    private String name;

    private String description;

    private Date startDate;

    private Date endDate;

//    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalDateTime loopTime;

    private Date createdDate;

    private String createdBy;

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

    private List<TaskCheckListDTO> children;

    private List<String> routine;
//    private List<Tag> tags;

}
