package com.example.websitepro.Service.Impl;

import com.example.websitepro.Config.Constant;
import com.example.websitepro.Config.WebException;
import com.example.websitepro.Entity.DTO.TaskCheckListDTO;
import com.example.websitepro.Entity.Mapper.TaskCheckListMapper;
import com.example.websitepro.Entity.Request.TaskChangeStatusRequest;
import com.example.websitepro.Entity.Request.TaskFilterRequest;
import com.example.websitepro.Entity.Request.UpdateTaskDTO;
import com.example.websitepro.Entity.Request.UpdateTaskRequest;
import com.example.websitepro.Entity.Response.TaskDetailResponse;
import com.example.websitepro.Entity.Response.TaskProjection;
import com.example.websitepro.Entity.TaskCheckList;
import com.example.websitepro.Repository.TaskRepository;
import com.example.websitepro.Service.CalculatingEXPPoint;
import com.example.websitepro.Service.TaskCheckListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskCheckListServiceImpl implements TaskCheckListService {

    private final TaskRepository taskRepo;

    private TaskCheckListMapper taskMapper = Mappers.getMapper(TaskCheckListMapper.class);

    @Override
    public TaskCheckListDTO save(TaskCheckListDTO task) {

        if (task.getTypeCode().equals(Constant.TASK_TYPE_CODE.DAILY)){
            task.setRoutineType("DAILY");
        }
        TaskCheckList response = taskRepo.save(taskMapper.toEntity(task));

        return TaskCheckListDTO.builder()
                .id(response.getId())
                .build();
    }

    @Override
    public TaskCheckListDTO update(UpdateTaskRequest task) {

        TaskCheckList res = taskRepo.findById(task.getId())
                .orElseThrow(() -> new WebException(Constant.MESSAGE.ID_NOT_FOUND));

        List<TaskCheckList> listChildrenRes = res.getChildren();

        TaskCheckList detail = taskMapper.ToEntityFromUpdateRequest(task);

        // TODO: 3. If in res, isComplete != task.isComplete || isDelete != task.isDeleted, calculate EXP to add to user

        if(!detail.getTypeCode().equals("HOBBY")){
            List<TaskCheckList> newList = detail.getChildren().stream().map(item -> {
                if(item.getId() != null && item.getTypeGroup() == 2L && listChildrenRes.stream().anyMatch(resItem -> resItem.getId().equals(item.getId()))){
                    // if item is group check list, update children as in res get from DB (as UpdateTaskRequest shouldn't have this value, this is to prevent children from turning empty
                    item.setChildren(listChildrenRes.stream().filter(resItem -> resItem.getId().equals(item.getId())).findFirst().get().getChildren());
                }
                return item;
            }).collect(Collectors.toList());

            detail.setChildren(newList);
        }
        taskRepo.save(detail);

        return TaskCheckListDTO.builder()
                .id(res.getId())
                .build();
    }

    @Override
    public TaskCheckList detail(Long id) {
        TaskCheckList result = taskRepo.findById(id)
                .orElseThrow(() -> new WebException(Constant.MESSAGE.ID_NOT_FOUND));
        return result;
    }

    @Override
    public List<TaskProjection> detailProject(Long id) {
        List<TaskProjection> res = taskRepo.getByProjectId(id);
        return res;
    }

    @Override
    public TaskCheckListDTO changeStatus(TaskChangeStatusRequest task) {
        TaskCheckList response = taskRepo.findById(task.getId())
                .orElseThrow(() -> new WebException(Constant.MESSAGE.ID_NOT_FOUND));
        changeStatusByAction(response, task);
        calculateEXPAndSave(task.getAction(), response);
        taskRepo.save(response);
        return taskMapper.toDTO(response);
    }

    @Override
    public List<TaskCheckList> filterTaskList(TaskFilterRequest filter) {
        if (!StringUtils.hasLength(filter.getOrderItem())) {
            filter.setOrderItem("taskListOrder");
        }

        if (!(StringUtils.hasLength(filter.getOrder()))
                || !(filter.getOrder().equals("asc") || filter.getOrder().equals("desc"))) {
            filter.setOrder("asc");
        }

        if (filter.getSearchDate() == null) {
            filter.setSearchDate(Calendar.getInstance().getTime());
        }

        if (!StringUtils.hasLength(filter.getAuthor())) {
            Authentication authen = SecurityContextHolder.getContext().getAuthentication();
            filter.setAuthor(authen.getName());
        }

        Sort sort = Sort.by(filter.getOrder().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, filter.getOrderItem());
        return taskRepo.filter(filter, sort);
    }

    @Override
    public List<TaskDetailResponse> getTaskDashboard(TaskFilterRequest filter) {
        if (filter.getSearchDate() == null) {
            filter.setSearchDate(Calendar.getInstance().getTime());
        }
        if (!StringUtils.hasLength(filter.getAuthor())) {
            Authentication authen = SecurityContextHolder.getContext().getAuthentication();
            filter.setAuthor(authen.getName());
        }
        List<TaskCheckList> res = taskRepo.getTaskBySearchDate(filter);
        return taskMapper.toListNoLv2Child(res);
    }

    @Override
    public TaskCheckListDTO saveSubGroupTask(Long parentId, TaskCheckListDTO task) {
        TaskCheckList parentItem = taskRepo.findById(parentId)
                .orElseThrow(() -> new WebException(Constant.MESSAGE.ID_NOT_FOUND));

        TaskCheckList res = taskMapper.toEntity(task);
        res.setParent(parentItem);
        taskRepo.save(res);

        return taskMapper.toDTO(res);
    }

    @Override
    public Flux<ServerSentEvent<List<TaskDetailResponse>>> getTaskDashboardSSE(TaskFilterRequest filter) {
        try {
            return Flux.interval(Duration.ofSeconds(1))
                    .publishOn(Schedulers.boundedElastic())
                    .map(sequence -> ServerSentEvent.<List<TaskDetailResponse>>builder()
                            .id(String.valueOf(sequence))
                            .event("user-list-event")
                            .data(getTaskDashboard(filter))
                            .build());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new WebException(Constant.MESSAGE.ERROR_OCCURRED);
        }
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void restartDaily(){
        taskRepo.restartHobby();
    }

    public CalculatingEXPPoint chooseStrategy(String typeCode){
        CalculatingEXPPoint strategy = null;

        if (typeCode.equals("HOBBY")){
            strategy = new TypeHobbyCalculateEXPStrategy();
        } else if (typeCode.equals("TODO")) {
            strategy = new TypeToDoCalculateEXPStrategy();
        } else if (typeCode.equals("PROJECT")) {
            strategy = new TypeProjectCalculateEXPStrategy();
        } else if (typeCode.equals("DAILY")) {
            strategy = new TypeDailyCalculateEXPStrategy();
        }

        return strategy;
    }

    public void calculateEXPAndSave(String action, TaskCheckList taskCheckList){
        CalculatingEXPPoint strategy = chooseStrategy(taskCheckList.getTypeCode());
        Integer exp = strategy.calculateEXPTaskByAction(action, taskCheckList);
        if(exp != 0) {
            log.info("SCORE: " + exp);
            // ToDo: 1. Design formula calculate exp score of each action depend on each typeCode (based on: children size, due date, priority, etc)
            // ToDo: 2. Save in to DB
        }
    }

    public void changeStatusByAction(TaskCheckList response, TaskChangeStatusRequest task){
        String action = task.getAction();
        if (action.equals(Constant.ACTION.DELETE)) {
            response.setIsDeleted(true);
        } else if (action.equals(Constant.ACTION.UNDO_DELETE)){
            response.setIsDeleted(false);
        } else if (action.equals(Constant.ACTION.COMPLETE)) {
            response.setIsCompleted(true);
            response.setIsCredited(1L);
        } else if (action.equals(Constant.ACTION.UNDO_COMPLETE)) {
            response.setIsCompleted(false);
        } else if (action.equals(Constant.ACTION.CHANGE_POSITION)) {
            response.setTaskListOrder(task.getPosition());
        } else if (action.equals(Constant.ACTION.ADD_SCORE)) {
            response.setTotalCredit(response.getTotalCredit() + 1);
        } else if (action.equals(Constant.ACTION.SUBTRACT_SCORE)) {
            response.setTotalCredit(response.getTotalCredit() - 1);
        } else {
            throw new WebException(Constant.MESSAGE.ACTION_NOT_EXIST);
        }

    }
}
