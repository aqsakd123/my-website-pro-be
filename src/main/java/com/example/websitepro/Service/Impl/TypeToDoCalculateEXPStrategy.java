package com.example.websitepro.Service.Impl;

import com.example.websitepro.Config.Constant;
import com.example.websitepro.Entity.TaskCheckList;
import com.example.websitepro.Service.CalculatingEXPPoint;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class TypeToDoCalculateEXPStrategy implements CalculatingEXPPoint {

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
        }
        return 0;
    }

    public Integer calculateFinishEXPTask(TaskCheckList task) {
        return 9;
    }

    public Integer calculateFinishUndoTask(TaskCheckList task) {
        return 10;
    }

    public Integer calculateDeleteTask(TaskCheckList task) {
        return 11;
    }

    public Integer calculateUndoDeleteTask(TaskCheckList task) {
        return 12;
    }
}
