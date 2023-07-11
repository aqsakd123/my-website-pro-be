package com.example.websitepro.Service.Impl;

import com.example.websitepro.Config.Constant;
import com.example.websitepro.Entity.TaskCheckList;
import com.example.websitepro.Service.CalculatingEXPPoint;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class TypeHobbyCalculateEXPStrategy implements CalculatingEXPPoint {

    @Override
    public Integer calculateEXPTaskByAction(String action, TaskCheckList task) {
        if(action.equals(Constant.ACTION.COMPLETE)) {
            return calculateFinishEXPTask(task);
        } else if (action.equals(Constant.ACTION.UNDO_COMPLETE)) {
            return calculateFinishUndoTask(task);
        } else if (action.equals(Constant.ACTION.DELETE)) {
            return calculateDeleteTask(task);
        } else if (action.equals(Constant.ACTION.UNDO_DELETE)) {
            return calculateUndoDeleteTask(task);
        } else if (action.equals(Constant.ACTION.ADD_SCORE)) {
            return calculateAddScore(task);
        } else if (action.equals(Constant.ACTION.SUBTRACT_SCORE)) {
            return calculateSubtractScore(task);
        }
        return 0;
    }

    public Integer calculateFinishEXPTask(TaskCheckList task) {
        return 3;
    }

    public Integer calculateFinishUndoTask(TaskCheckList task) {
        return 4;
    }

    public Integer calculateDeleteTask(TaskCheckList task) {
        return 5;
    }

    public Integer calculateUndoDeleteTask(TaskCheckList task) {
        return 6;
    }

    public Integer calculateAddScore(TaskCheckList task) {
        return 7;
    }

    public Integer calculateSubtractScore(TaskCheckList task) {
        return 8;
    }

}
