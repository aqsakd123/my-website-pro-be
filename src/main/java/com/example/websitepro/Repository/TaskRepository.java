package com.example.websitepro.Repository;

import com.example.websitepro.Entity.Request.TaskFilterRequest;
import com.example.websitepro.Entity.Response.TaskProjection;
import com.example.websitepro.Entity.TaskCheckList;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<TaskCheckList, Long>, JpaSpecificationExecutor<TaskCheckList> {

    @Query(value = "Select t from TaskCheckList t " +
            "where t.isCompleted = :#{#filterRequest.completed} " +
            "and t.parent is null " +
            "and t.createdBy = :#{#filterRequest.author} " +
            "and t.isDeleted = false " +
            "and (:#{#filterRequest.typeCode} is null or t.typeCode = :#{#filterRequest.typeCode}) " +
            " ")
    List<TaskCheckList> filter(TaskFilterRequest filterRequest, Sort sort);

    @Query(value = "select " +
            "t.id as id," +
            "t.name as name, " +
            "t.parent.id as parentId, " +
            "t.isCompleted as isCompleted, " +
            "t.description as description, " +
            "t.pinned as pinned," +
            "t.endDate as endDate," +
            "t.taskListOrder as taskListOrder, " +
            "t.priority as priority " +
            "from TaskCheckList t " +
            "where t.isDeleted = false " +
            "and ((t.id = :id and t.parent is null) or " +
            "(t.projectParentId = :id and t.parent is not null and t.typeGroup = 2)) " +
            "order by t.taskListOrder asc, t.id asc")
    List<TaskProjection> getByProjectId(Long id);

    @Query("SELECT distinct t " +
            "FROM TaskCheckList t " +
            "left join fetch t.children child " +
            "WHERE t.isDeleted = false " +
            "and t.isCompleted = false " +
            "and t.createdBy = :#{#filterRequest.author} " +
            "and ((t.typeCode = 'DAILY' AND t.routineType = 'DAILY' and " +
            "(t.startDate IS NOT NULL AND t.startDate <= :#{#filterRequest.searchDate}) " +
            "    AND (cast(" +
            "FUNCTION('DATE_PART', 'day', cast(:#{#filterRequest.searchDate} as java.sql.Timestamp)) " +
            "- FUNCTION('DATE_PART', 'day', cast(t.startDate as java.sql.Timestamp)) as integer)" +
            "% " +
            "cast(t.routineDate as integer)) = 0) " +
            "OR (t.typeCode = 'DAILY' AND t.routineType = 'CUSTOM' and " +
            "(t.startDate IS NOT NULL AND t.startDate <= :#{#filterRequest.searchDate}) " +
            "    AND trim(FUNCTION('TO_CHAR', cast(:#{#filterRequest.searchDate} as java.sql.Timestamp), 'day')) IN (select r from t.routine r)) " +
            "OR (t.typeCode = 'TODO' AND " +
            "t.parent is null and " +
            "(t.startDate IS NOT NULL AND t.startDate <= :#{#filterRequest.searchDate} " +
            "        AND (t.endDate >= :#{#filterRequest.searchDate}))) " +
            "OR (t.typeCode = 'PROJECT' " +
            "AND t.typeGroup = 2 " +
            "AND t.parent is not null " +
            "    AND t.startDate <= :#{#filterRequest.searchDate} AND (t.endDate >= :#{#filterRequest.searchDate}))) " +
            "order by t.taskListOrder asc, t.id asc")
    List<TaskCheckList> getTaskBySearchDate(TaskFilterRequest filterRequest);

    @Modifying
    @Query("UPDATE TaskCheckList t " +
            "SET t.isCompleted = false " +
            "WHERE t.typeCode = 'DAILY' " +
            "AND t.isDeleted = false " +
            "AND ((t.parent is null) " +
            "OR (t.parent.id IN (SELECT tc.id FROM TaskCheckList tc WHERE tc.typeCode = 'DAILY' AND tc.isDeleted = false)))")
    void restartHobby();

}
