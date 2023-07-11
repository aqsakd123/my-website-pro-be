package com.example.websitepro.Service;

import com.example.websitepro.Entity.DTO.TaskCheckListDTO;
import com.example.websitepro.Entity.Request.TaskChangeStatusRequest;
import com.example.websitepro.Entity.Request.TaskFilterRequest;
import com.example.websitepro.Entity.Request.UpdateTaskDTO;
import com.example.websitepro.Entity.Request.UpdateTaskRequest;
import com.example.websitepro.Entity.Response.TaskDetailResponse;
import com.example.websitepro.Entity.Response.TaskProjection;
import com.example.websitepro.Entity.TaskCheckList;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TaskCheckListService {

    TaskCheckListDTO save(TaskCheckListDTO task);

    TaskCheckListDTO update(UpdateTaskRequest task);

    TaskCheckList detail(Long id);

    List<TaskProjection> detailProject(Long id);

    TaskCheckListDTO changeStatus(TaskChangeStatusRequest task);

    List<TaskCheckList> filterTaskList(TaskFilterRequest filter);

    List<TaskDetailResponse> getTaskDashboard(TaskFilterRequest filter);

    Flux<ServerSentEvent<List<TaskDetailResponse>>> getTaskDashboardSSE(TaskFilterRequest filter);

    TaskCheckListDTO saveSubGroupTask(Long parentId, TaskCheckListDTO task);

}
