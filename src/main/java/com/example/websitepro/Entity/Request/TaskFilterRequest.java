package com.example.websitepro.Entity.Request;

import lombok.*;

import java.util.Date;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskFilterRequest {

    private String order = "asc"; //desc, asc
    private String orderItem;
    private Date searchDate;
    private String typeCode;
    private String author;
    private Boolean completed = false;
}
