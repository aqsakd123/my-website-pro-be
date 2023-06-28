package com.example.websitepro.TaskTest;

import com.example.websitepro.Entity.DTO.TaskCheckListDTO;
import com.example.websitepro.Entity.Mapper.TaskCheckListMapper;
import com.example.websitepro.Entity.Request.TaskFilterRequest;
import com.example.websitepro.Entity.Response.TaskDetailResponse;
import com.example.websitepro.Entity.TaskCheckList;
import com.example.websitepro.Repository.TaskRepository;
import com.example.websitepro.Service.Impl.TaskCheckListServiceImpl;
import com.example.websitepro.Service.TaskCheckListService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class TaskCheckListTest {

    @Autowired
    TaskRepository taskRepository;

    @InjectMocks
    private TaskCheckListServiceImpl taskCheckListService;

    private TaskCheckListMapper taskMapper = Mappers.getMapper(TaskCheckListMapper.class);

    @Before
    public void setUp() {
        taskCheckListService = new TaskCheckListServiceImpl(taskRepository);
    }

    @Test
    public void testFilter() {
        // data provided
        List<TaskCheckList> taskCheckList = new ArrayList<>();

        taskCheckList.add(TaskCheckList.builder().name("Task 1").createdBy("user1").isDeleted(false).typeCode("DAILY").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 2").createdBy("user1").isDeleted(false).typeCode("TODO").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 3").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 4").createdBy("user1").isDeleted(false).typeCode("HOBBY").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 5").createdBy("user1").isDeleted(false).typeCode("DAILY").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 6").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 7").createdBy("user1").isDeleted(false).typeCode("TODO").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 8").createdBy("user1").isDeleted(false).typeCode("HOBBY").isCompleted(false).build());

        //other's user tasks
        taskCheckList.add(TaskCheckList.builder().name("Task 9").createdBy("user2").isDeleted(false).typeCode("DAILY").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 10").createdBy("user2").isDeleted(false).typeCode("TODO").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 11").createdBy("user2").isDeleted(false).typeCode("HOBBY").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 12").createdBy("user2").isDeleted(false).typeCode("PROJECT").isCompleted(false).build());

        //completed tasks
        taskCheckList.add(TaskCheckList.builder().name("Task 13").createdBy("user1").isDeleted(false).typeCode("TODO").isCompleted(true).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 14").createdBy("user1").isDeleted(false).typeCode("DAILY").isCompleted(true).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 15").createdBy("user1").isDeleted(false).typeCode("TODO").isCompleted(true).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 16").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(true).build());

        // deleted tasks
        taskCheckList.add(TaskCheckList.builder().name("Task 17").createdBy("user1").isDeleted(true).typeCode("TODO").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 18").createdBy("user1").isDeleted(true).typeCode("DAILY").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 19").createdBy("user1").isDeleted(true).typeCode("TODO").isCompleted(false).build());
        taskCheckList.add(TaskCheckList.builder().name("Task 20").createdBy("user1").isDeleted(true).typeCode("PROJECT").isCompleted(false).build());

        taskRepository.saveAll(taskCheckList);
        // filter data

        TaskFilterRequest filter1 = TaskFilterRequest.builder().typeCode("DAILY").author("user1").completed(false).build();
        TaskFilterRequest filter2 = TaskFilterRequest.builder().typeCode("TODO").author("user1").completed(false).build();
        TaskFilterRequest filter3 = TaskFilterRequest.builder().typeCode("HOBBY").author("user1").completed(false).build();
        TaskFilterRequest filter4 = TaskFilterRequest.builder().typeCode("PROJECT").author("user1").completed(false).build();
        TaskFilterRequest filter5 = TaskFilterRequest.builder().typeCode("TODO").author("user1").completed(true).build();

        // actual
        List<TaskCheckListDTO> test1 = taskCheckListService.filterTaskList(filter1);
        List<TaskCheckListDTO> test2 = taskCheckListService.filterTaskList(filter2);
        List<TaskCheckListDTO> test3 = taskCheckListService.filterTaskList(filter3);
        List<TaskCheckListDTO> test4 = taskCheckListService.filterTaskList(filter4);
        List<TaskCheckListDTO> test5 = taskCheckListService.filterTaskList(filter5);

        // test
        Assert.assertEquals(2, test1.size());
        Assert.assertEquals("Task 1", test1.get(0).getName());
        Assert.assertEquals("Task 5", test1.get(1).getName());
        Assert.assertEquals(2, test2.size());
        Assert.assertEquals("Task 2", test2.get(0).getName());
        Assert.assertEquals("Task 7", test2.get(1).getName());
        Assert.assertEquals(2, test3.size());
        Assert.assertEquals("Task 4", test3.get(0).getName());
        Assert.assertEquals("Task 8", test3.get(1).getName());
        Assert.assertEquals(2, test4.size());
        Assert.assertEquals("Task 3", test4.get(0).getName());
        Assert.assertEquals("Task 6", test4.get(1).getName());
        Assert.assertEquals(2, test5.size());
        Assert.assertEquals("Task 13", test5.get(0).getName());
        Assert.assertEquals("Task 15", test5.get(1).getName());
    }


    @Test
    public void testTimeLineDashboard() throws ParseException {

        TaskCheckList subtask1 = TaskCheckList.builder().name("Sub task of task 1.1").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(false).isDeleted(false).build();
        subtask1.setStartDate(new SimpleDateFormat("dd/MM/yyyy").parse("25/06/2023"));
        subtask1.setEndDate(new SimpleDateFormat("dd/MM/yyyy").parse("30/06/2023"));

        TaskCheckList subtask2 = TaskCheckList.builder().name("Sub task of task 1.2").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(false).isDeleted(false).build();
        TaskCheckList subtask3 = TaskCheckList.builder().name("Sub task of task 2.1").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(false).isDeleted(false).build();
        TaskCheckList subtask4 = TaskCheckList.builder().name("Sub task of task 2.2").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(false).isDeleted(false).build();

        TaskCheckList groupTask001 = TaskCheckList.builder().name("Sub Group Task 001").createdBy("user1").isDeleted(false).typeCode("PROJECT")
                .children(Arrays.asList(subtask1, subtask2))
                .isCompleted(false)
                .isDeleted(false)
                .build();

        TaskCheckList groupTask002 = TaskCheckList.builder().name("Sub Group Task 002").createdBy("user1").isDeleted(false).typeCode("PROJECT")
                .children(Arrays.asList(subtask3, subtask4))
                .isCompleted(false)
                .isDeleted(false).build();

        TaskCheckList project1 = TaskCheckList.builder().name("PROJECT 1").createdBy("user1").isDeleted(false).typeCode("PROJECT").isCompleted(false).isDeleted(false)
                .children(Arrays.asList(groupTask001, groupTask002))
                .build();

    }

    @Test
    public void testMapper() {
        TaskCheckList childrenLv2No1 = TaskCheckList.builder().name("Task Grand Children 1").createdBy("user1").isDeleted(false).typeCode("DAILY").isCompleted(false).isDeleted(false).build();
        TaskCheckList childrenLv2No2 = TaskCheckList.builder().name("Task Grand Children 2").createdBy("user1").isDeleted(false).typeCode("DAILY").isCompleted(false).isDeleted(false).build();

        TaskCheckList childrenNo1 = TaskCheckList.builder().name("Task Children 1").createdBy("user1").isDeleted(false).typeCode("DAILY")
                .children(Arrays.asList(childrenLv2No1, childrenLv2No2))
                .isCompleted(false)
                .isDeleted(false).build();

        TaskCheckList childrenNo2 = TaskCheckList.builder().name("Task Children 2").createdBy("user1").isDeleted(false).typeCode("DAILY")
                .children(Arrays.asList(childrenLv2No1, childrenLv2No2))
                .isCompleted(false)
                .isDeleted(false).build();

        TaskCheckList Parent = TaskCheckList.builder().name("Task 1").createdBy("user1").isDeleted(false).typeCode("DAILY").isCompleted(false).isDeleted(false)
                .children(Arrays.asList(childrenNo1, childrenNo2))
                .routine(Arrays.asList("monday", "tuesday"))
                .build();

        TaskCheckListDTO test = taskMapper.toDTOWithoutChildren(Parent);
        Assert.assertEquals(null, test.getChildren());
        Assert.assertEquals(null, test.getRoutine());

    }
}
