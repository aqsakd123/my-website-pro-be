package com.example.websitepro.Controller;

import com.example.websitepro.Config.Constant;
import com.example.websitepro.Service.Impl.TaskCheckListServiceImpl;
import com.example.websitepro.Service.TaskCheckListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    TaskCheckListServiceImpl taskService;

    @GetMapping("/api/test")
    public ResponseEntity<?> test(){
        taskService.restartDaily();
        return ResponseEntity.ok("OK");
    }

}
