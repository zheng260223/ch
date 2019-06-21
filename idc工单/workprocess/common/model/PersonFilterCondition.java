package com.workprocess.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonFilterCondition implements Serializable {

    /**
     * 流程中 当前任务ID
     */
    private String flowTaskId;
    /**
     * 人员列表 0 表示未设置， 形如： aaa,bbb,ccc
     */
    private String userIds = "0";
    /**
     * 部门列表 0 表示未设置, 形如： aaa,bbb,ccc
     */
    private String deptIds = "0";

    /**
     * 角色列表 0 表示未设置, 形如： aaa,bbb,ccc
     */
    private String roleIds = "0";

    public PersonFilterCondition(){

    }
    public PersonFilterCondition(String userIds, String deptIds, String roleIds) {
        this.userIds = userIds.equals("") ? "0" : userIds;
        this.deptIds = deptIds.equals("") ? "0" : deptIds;
        this.roleIds = roleIds.equals("") ? "0" : roleIds;
    }

    public String getFlowTaskId() {
        return flowTaskId;
    }

    public void setFlowTaskId(String flowTaskId) {
        this.flowTaskId = flowTaskId;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * 获取Candidate Users配置 UserID
     *
     * @return List<String>
     */
    public List<String> getUserIdsList() {
        if (userIds.length() > 0) {
            String[] userIdsSplit = userIds.split(",");
            return Arrays.asList(userIdsSplit);
        }
        return new ArrayList<>();
    }

    /**
     * 获取Candidate Groups配置 DeptID
     *
     * @return List<String>
     */
    public List<String> getDeptIdsList() {
        if (deptIds.length() > 0) {
            String[] groupIdSplit = deptIds.split(",");
            return Arrays.asList(groupIdSplit);
        }
        return new ArrayList<>();
    }

    /**
     * 获取Candidate Groups配置 RoleID
     *
     * @return List<String>
     */
    public List<String> getRoleIdsList() {
        if (roleIds.length() > 0) {
            String[] groupIdSplit = roleIds.split(",");
            return Arrays.asList(groupIdSplit);
        }
        return new ArrayList<>();
    }
}
