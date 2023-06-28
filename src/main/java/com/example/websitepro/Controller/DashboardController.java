package com.example.websitepro.Controller;

import com.example.websitepro.Entity.Request.TaskFilterRequest;
import com.example.websitepro.Entity.Response.TaskDetailResponse;
import com.example.websitepro.Service.TaskCheckListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/public/dashboard")
public class DashboardController {

    @Autowired
    TaskCheckListService taskService;

    @PostMapping("/task")
    public ResponseEntity<?> getDashboardTaskList(@RequestBody TaskFilterRequest filterRequest){
        return ResponseEntity.ok(taskService.getTaskDashboard(filterRequest));
    }

    @GetMapping("/get-time-line-stream")
    public Flux<ServerSentEvent<List<TaskDetailResponse>>> streamLastMessage(@RequestParam("email") String email) {
        TaskFilterRequest request = new TaskFilterRequest();
        request.setAuthor(email);
        return taskService.getTaskDashboardSSE(request);
    }

}
