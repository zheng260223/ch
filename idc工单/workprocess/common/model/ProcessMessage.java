package com.workprocess.common.model;

import java.io.Serializable;

/**
 * 流程操作消息类
 */
public class ProcessMessage<T> implements Serializable {

    /**
     * 操作结果 0 成功 1 错误
     */
    private int code;
    /**
     * code=0 成功提示，code=1 错误提示
     */
    private String message;

    /**
     * 数据
     */
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
