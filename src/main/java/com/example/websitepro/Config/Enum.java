package com.example.websitepro.Config;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public enum Enum {

    ID_NOT_FOUND("UNKNOWN_ID"),
    JWT_WRONG( "JWT.WRONG"),
    ACTION_NOT_EXIST("ACTION_NOT_EXIST");

    private final String value;

    private Enum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static enum STATUS {
        DELETED(0, "DELETED"),
        NEW(2, "NEW"),
        IN_PROGRESS(3, "IN_PROGRESS"),
        COMPLETED(4, "COMPLETED");

        private final int value;
        private final String label;

        private STATUS(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public int value() {
            return this.value;
        }
        public String label() {
            return this.label;
        }


    }

}
