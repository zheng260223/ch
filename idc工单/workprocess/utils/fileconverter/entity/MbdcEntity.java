package com.workprocess.utils.fileconverter.entity;

public class MbdcEntity {
    public static boolean isOpen;
    public static String userId;
    public static String password;
    public static String uploadUrl;
    public static String viewUrl;
    public static String statusFileUrl;

    public void setIsOpen(String isOpen) {
        boolean temp=false;
        if(isOpen.equals("1")){
            temp=true;
        }
        MbdcEntity.isOpen = temp;
    }

    public void setUserId(String userId) {
        MbdcEntity.userId = userId;
    }

    public void setPassword(String password) {
        MbdcEntity.password = password;
    }

    public void setUploadUrl(String uploadUrl) {
        MbdcEntity.uploadUrl = uploadUrl;
    }

    public void setViewUrl(String viewUrl) {
        MbdcEntity.viewUrl = viewUrl;
    }

    public void setStatusFileUrl(String statusFileUrl) {
        MbdcEntity.statusFileUrl = statusFileUrl;
    }
}
