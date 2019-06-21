package com.workprocess.utils;

import java.io.Serializable;

public class RtJson<T> implements Serializable {
    private int rtStatus = 0;
    private String rtMsg;
    private T rtData;

    public RtJson() {
        super();
    }

    public RtJson(Integer rtStatus, String rtMsg, T rtData) {
        super();
        this.rtStatus = rtStatus;
        this.rtMsg = rtMsg;
        this.rtData = rtData;
    }

    public int getRtStatus() {
        return rtStatus;
    }

    public void setRtStatus(int rtStatus) {
        this.rtStatus = rtStatus;
    }

    public String getRtMsg() {
        return rtMsg;
    }

    public void setRtMsg(String rtMsg) {
        this.rtMsg = rtMsg;
    }

    public T getRtData() {
        return rtData;
    }

    public void setRtData(T rtData) {
        this.rtData = rtData;
    }
}
