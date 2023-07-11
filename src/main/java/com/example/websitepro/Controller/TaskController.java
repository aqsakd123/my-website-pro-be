package com.example.websitepro.Controller;

import com.example.websitepro.Entity.DTO.TaskCheckListDTO;
import com.example.websitepro.Entity.Request.TaskChangeStatusRequest;
import com.example.websitepro.Entity.Request.TaskFilterRequest;
import com.example.websitepro.Entity.Request.UpdateTaskDTO;
import com.example.websitepro.Entity.Request.UpdateTaskRequest;
import com.example.websitepro.Entity.Response.TaskDetailResponse;
import com.example.websitepro.Entity.TaskCheckList;
import com.example.websitepro.Service.TaskCheckListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    TaskCheckListService taskService;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody TaskCheckListDTO task){
        return ResponseEntity.ok(taskService.save(task));
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("authentication.name == #task.createdBy")
    public ResponseEntity<?> update(@RequestBody UpdateTaskRequest task, @PathVariable("id") Long id){
        return ResponseEntity.ok(taskService.update(task));
    }

    @QueryMapping
    @PreAuthorize("authentication.name.length() > 0")
    public List<?> filter(@Argument TaskFilterRequest filterRequest){
        return taskService.filterTaskList(filterRequest);
    }

    @QueryMapping
    @PostAuthorize("authentication.name == returnObject.createdBy")
    public TaskCheckList findTaskById(@Argument Long id){
        return taskService.detail(id);
    }

    @PostMapping("/change-status")
    public ResponseEntity<?> changeStatus(@RequestBody TaskChangeStatusRequest request){
        return ResponseEntity.ok(taskService.changeStatus(request));
    }

    @PostMapping("/insert-sub-task/{parentId}")
    public ResponseEntity<?> insertSubTask(@PathVariable("parentId") Long parentId , @RequestBody TaskCheckListDTO task){
        return ResponseEntity.ok(taskService.saveSubGroupTask(parentId, task));
    }

    @GetMapping("/project-detail/{id}")
    public ResponseEntity<?> projectDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(taskService.detailProject(id));
    }

}
