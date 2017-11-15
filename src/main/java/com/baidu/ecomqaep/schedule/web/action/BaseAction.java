package com.baidu.ecomqaep.schedule.web.action;

import com.baidu.ecomqaep.schedule.base.ResultType;
import com.google.gson.Gson;

public class BaseAction {

    public String serialize(ResultType result) {
        Gson gson = new Gson();
        return gson.toJson(result);
    }

    public String success() {
        Gson gson = new Gson();
        return gson.toJson(new ResultType(true, "OK", null));
    }

    public String success(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(new ResultType(true, "OK", obj));
    }

    public String success(String message, Object obj) {
        Gson gson = new Gson();
        return gson.toJson(new ResultType(true, message, obj));
    }

    public String success(int status) {
        Gson gson = new Gson();
        return gson.toJson(new ResultType(status, true, "OK", null));
    }

    public String error() {
        Gson gson = new Gson();
        return gson.toJson(new ResultType(false, "FAIL", null));
    }

    public String error(String message) {
        Gson gson = new Gson();
        return gson.toJson(new ResultType(false, message, null));
    }

    public String error(int code, String message) {
        Gson gson = new Gson();
        return gson.toJson(new ResultType(code, false, message, null));
    }
}
