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
import com.example.websitepro.Service.TaskCheckListService;
import lombok.RequiredArgsConstructor;
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

        List<TaskCheckList> newList = detail.getChildren().stream().map(item -> {
            if(item.getId() != null && item.getTypeGroup() == 2L && listChildrenRes.stream().anyMatch(resItem -> resItem.getId().equals(item.getId()))){
                item.setChildren(listChildrenRes.stream().filter(resItem -> resItem.getId().equals(item.getId())).findFirst().get().getChildren());
            }
            return item;
        }).collect(Collectors.toList());

        detail.setChildren(newList);

        taskRepo.save(detail);

        return TaskCheckListDTO.builder()
                .id(res.getId())
                .build();
    }

    @Override
    public TaskDetailResponse detail(Long id) {
        TaskCheckList result = taskRepo.findById(id)
                .orElseThrow(() -> new WebException(Constant.MESSAGE.ID_NOT_FOUND));
        return taskMapper.toDetailDTO(result);
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

        // Take note to do later
        // Write a task action log aboute DELETE/UNDO_DELETE/COMPLETE/UNDO_COMPLETE/SCORING

        if (task.getAction().equals(Constant.ACTION.DELETE)) {

            // Remove all EXP gained from the task or subtask
            // run query set IS_DELETED all id || projectParentId = task.getId
            // if response.getProjectParentId = null change status all children

            response.setIsDeleted(true);

        } else if (task.getAction().equals(Constant.ACTION.UNDO_DELETE)){

            // Restore all EXP gained from the task or subtask
            // run query set IS_DELETED false all id || projectParentId = task.getId
            // if response.getProjectParentId = null change status all children

            response.setIsDeleted(false);

        } else if (task.getAction().equals(Constant.ACTION.COMPLETE)) {

            // GAINED 1 SCORE
            // Perform action similar to STREAKS ?????
            // if DAILY and 1 day not finish, lose streak and SCORE
            // if DAILY and finish all DAILY task (task no parent_id) gain more EXP
            response.setIsCompleted(true);
            response.setIsCredited(1L);

        } else if (task.getAction().equals(Constant.ACTION.UNDO_COMPLETE)) {

            // -1 SCORE
            // LOSE STREAK
            // LOSE PRIZE ALL DAILY TASK DONE
            response.setIsCompleted(false);

        } else if (task.getAction().equals(Constant.ACTION.CHANGE_POSITION)) {

            response.setTaskListOrder(task.getPosition());

        } else if (task.getAction().equals(Constant.ACTION.ADD_SCORE)) {

            // Add EXP if + 1
            // In ADD Streak, add more EXP
            response.setTotalCredit(response.getTotalCredit() + 1);

        } else if (task.getAction().equals(Constant.ACTION.SUBTRACT_SCORE)) {

            // DESTROY STREAK
            // If in 1 day no action, - 1
            response.setTotalCredit(response.getTotalCredit() - 1);
        } else {
            throw new WebException(Constant.MESSAGE.ACTION_NOT_EXIST);
        }
        taskRepo.save(response);
        return taskMapper.toDTO(response);
    }

    @Override
    public List<TaskCheckListDTO> filterTaskList(TaskFilterRequest filter) {
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
        return taskMapper.toListDTOWithoutChildren(taskRepo.filter(filter, sort));
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
    public TaskCheckListDTO modifySubTask(Long id, UpdateTaskDTO task) {
        //not use
        TaskCheckList response = taskRepo.findById(id)
                .orElseThrow(() -> new WebException(Constant.MESSAGE.ID_NOT_FOUND));
        if (task.getEndDate() != null){
            response.setEndDate(task.getEndDate());
        }
        if (task.getLoopTime() != null){
            response.setLoopTime(task.getLoopTime());
        }
        if (StringUtils.hasLength(task.getName())){
            response.setName(task.getName());
        }
        taskRepo.save(response);

        return TaskCheckListDTO.builder()
                .id(response.getId())
                .build();
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

}
