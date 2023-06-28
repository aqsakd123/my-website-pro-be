package com.example.websitepro.Entity.Response;

import java.util.Date;

public interface TaskProjection {

    Long getId();
    String getName();
    String getDescription();
    Date getStartDate();
    Date getEndDate();
    Long getPriority();
    Double getTaskListOrder();
    Boolean getPinned();
    Boolean getIsCompleted();
    Long getParentId();
}
