package com.example.websitepro.Service;

import com.example.websitepro.Entity.DTO.TaskCheckListDTO;
import com.example.websitepro.Entity.TaskCheckList;

public interface CalculatingEXPPoint {

    Integer calculateEXPTaskByAction(String action, TaskCheckList task);

}
