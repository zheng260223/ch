package com.workprocess.utils.fileconverter.data;

public class MbdcData {
    private boolean success;
    private String opMsg;
    private String fileCode;
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getOpMsg() {
        return opMsg;
    }
    public void setOpMsg(String opMsg) {
        this.opMsg = opMsg;
    }
    public String getFileCode() {
        return fileCode;
    }
    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

}
