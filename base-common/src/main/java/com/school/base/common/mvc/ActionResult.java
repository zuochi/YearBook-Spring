package com.school.base.common.mvc;

import java.util.Map;

public class ActionResult {

    public static ActionResult OK = new ActionResult(0);
    public static ActionResult FAILD = new ActionResult(-1);
    private int code;
    private String message;
    private Object data;

    public ActionResult() {
    }

    public ActionResult(int code) {
        this.code = code;
    }

    public ActionResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ActionResult(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
