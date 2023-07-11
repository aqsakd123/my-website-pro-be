package com.example.websitepro.Entity.Mapper;

import com.example.websitepro.Entity.DTO.TaskCheckListDTO;
import com.example.websitepro.Entity.Request.UpdateTaskRequest;
import com.example.websitepro.Entity.Response.TaskDetailResponse;
import com.example.websitepro.Entity.TaskCheckList;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskCheckListMapper {

    TaskCheckListMapper INSTANCE = Mappers.getMapper(TaskCheckListMapper.class);

    TaskCheckListDTO toDTO(TaskCheckList task);

    TaskCheckList toEntity(TaskCheckListDTO dto);

    TaskCheckList ToEntityFromUpdateRequest(UpdateTaskRequest task);

    @Named(value = "toDetailDTONoRoutine")
    @Mapping(target = "routine", ignore = true)
    TaskDetailResponse toDetailDTONoRoutine(TaskCheckList task);

    @IterableMapping(qualifiedByName = "toDetailDTONoRoutine")
    List<TaskDetailResponse> toListNoLv2Child(List<TaskCheckList> entity);

    @Named(value = "toDTOWithoutChildren")
    @Mapping(target = "routine", ignore = true)
    @Mapping(target = "children", ignore = true)
    TaskCheckListDTO toDTOWithoutChildren(TaskCheckList taskChecklist);
}
