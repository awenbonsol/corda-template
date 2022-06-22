package com.template.webserver;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;


public class APIResponse<T> {
    private int code;
    private String timeStamp;
    private String description;
    private T data;

    public APIResponse(int code, String timeStamp, String description, T data) {
        this.code = code;
        this.timeStamp = timeStamp;
        this.description = description;
        this.data = data;
    }

    public static <T> APIResponse<T> success() {
        return new APIResponse<>(HttpStatus.OK.value(), new Timestamp(System.currentTimeMillis()).toString(), "success", null);
    }

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(HttpStatus.OK.value(), new Timestamp(System.currentTimeMillis()).toString(), "success", data);
    }

    public static <T> APIResponse<T> error(T message) {
        return new APIResponse<>(HttpStatus.BAD_REQUEST.value(), new Timestamp(System.currentTimeMillis()).toString(), "error", message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
