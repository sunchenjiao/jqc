package com.baidu.ecomqaep.schedule.base;

public class ResultType {
    public class Message {
        public static final String OBJECT_NOT_EXIST = "object not exist  ";
        public static final String SAVE_FAILUER = "save failure  ";
        public static final String PARAMETER_INVAILD = "Parameter invaild ";
    }

    public class Operation {
        public static final String SAVE = "[save]";
        public static final String DELETE = "[delete]";
        public static final String UPDATE = "[update]";
        public static final String GET = "[get]";
        public static final String QUERY = "[query]";
    }

    private Integer statusCode;
    private String message;
    private Boolean status;
    private Object data;

    public ResultType() {

    }

    public ResultType(Boolean status, String message, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public ResultType(int statusCode, Boolean status, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public ResultType success() {
        if (message == null) {
            message = "ok";
        }
        this.status = true;
        return this;
    }

    public ResultType statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResultType error() {
        if (message == null) {
            message = "fail";
        }
        this.status = false;
        return this;
    }

    public ResultType object(Object object) {
        this.data = object;
        return this;
    }

    public ResultType message(String message) {
        this.message = message;
        return this;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
