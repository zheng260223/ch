package com.workprocess.common;

import com.workprocess.common.model.PersonFilterCondition;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  查询当前任务 人员和组 条件配置
 *  Candidate Users, Candidate Groups
 */
@Component
public class CandidateQuery {

    private final static String USERID_SUFFIX = "_U";
    private final static String DEPT_SUFFIX = "_D";
    private final static String ROLE_SUFFIX = "_R";

    @Autowired
    private TaskService taskService;

    /**
     * 查询
     * @param taskId 要查询任务ID
     * @see Task#getId()
     */
    public PersonFilterCondition Query(String taskId) {
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        StringBuffer userIds = new StringBuffer();
        StringBuffer deptIds = new StringBuffer();
        StringBuffer roleIds = new StringBuffer();
        for (IdentityLink link : identityLinks) {
            String conditions = link.getGroupId();
            if (conditions != null && !conditions.equals("")) {
                if(conditions.endsWith(USERID_SUFFIX)){
                    userIds.append(conditions.split("_")[0]).append(",");
                }
                if(conditions.endsWith(DEPT_SUFFIX)){
                    deptIds.append(conditions.split("_")[0]).append(",");
                }
                if(conditions.endsWith(ROLE_SUFFIX)){
                    roleIds.append(conditions.split("_")[0]).append(",");
                }
            }
        }
        if (userIds.length() > 0) {
            userIds = userIds.deleteCharAt(userIds.length() - 1);
        }
        if (deptIds.length() > 0) {
            deptIds = deptIds.deleteCharAt(deptIds.length() - 1);
        }
        if (roleIds.length() > 0) {
            roleIds = roleIds.deleteCharAt(roleIds.length() - 1);
        }
        return new PersonFilterCondition(userIds.toString(),deptIds.toString(),roleIds.toString());
    }

    /**
     * 查询 Candidate Users 设置
     * @param taskId 任务ID
     * @return String 形如: aaa,bbb,ccc
     */
    public String QueryCandidateUsers(String taskId){
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        StringBuffer userIds = new StringBuffer();
        for(IdentityLink link : identityLinks){
            String userId = link.getUserId();
            if(userId != null){
                userIds.append(userId).append(",");
            }
        }
        if(userIds.length() > 0)
            userIds = userIds.deleteCharAt(userIds.length() - 1);
        return userIds.toString();
    }
}
