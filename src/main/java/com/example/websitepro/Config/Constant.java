package com.example.websitepro.Config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class Constant {
    public static final Message MESSAGE = new Message();
    public static final Action ACTION = new Action();
    public static final TaskTypeCode TASK_TYPE_CODE = new TaskTypeCode();

    public static final class Message {
         public final String ID_NOT_FOUND = "UNKNOWN_ID";
         public final String JWT_WRONG = "JWT.WRONG";
         public final String ACTION_NOT_EXIST = "ACTION_NOT_EXIST";
         public final String ERROR_OCCURRED = "ERROR_OCCURRED";

    }

    public static final class Action {
        public final String DELETE = "DELETE";
        public final String UNDO_DELETE = "UNDO_DELETE";
        public final String ACTIVE = "ACTIVE";
        public final String DEACTIVATE = "DEACTIVATE";
        public final String COMPLETE = "COMPLETE";
        public final String UNDO_COMPLETE = "UNDO_COMPLETE";
        public final String CHANGE_POSITION = "CHANGE_POSITION";
        public final String ADD_SCORE = "ADD_SCORE";
        public final String SUBTRACT_SCORE = "SUBTRACT_SCORE";
    }

    public static final class TaskTypeCode {
        public final String HOBBY = "HOBBY";
        public final String DAILY = "DAILY";
        public final String TODO = "TODO";
        public final String PROJECT = "PROJECT";
    }

}
