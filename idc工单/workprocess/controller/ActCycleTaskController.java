package com.workprocess.controller;

import com.google.gson.Gson;
import com.greenpanit.log.LogLevel;
import com.greenpanit.qtidc.entity.state.SysLogInfostate;
import com.greenpanit.qtidc.service.state.SysLogInfostateServiceUtilities;
import com.greenpanit.shiro.ShiroUser;
import com.greenpanit.utils.DateUtilities;
import com.greenpanit.utils.SecurityUtils;
import com.workprocess.common.model.PersonFilterCondition;
import com.workprocess.common.model.ProcessMessage;
import com.workprocess.services.ActCycleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import java.io.File;
import java.util.*;

/**
 * 周期性工单流程执行Controller
 */
@Controller
@RequestMapping(value = "/workprocess/cycletask")
public class ActCycleTaskController {
    private static final String dispatch = "workprocess/worktask/dispatchWorkTask";

    @Autowired
    private ActCycleTaskService actCycleTaskService;

    /**
     * 获取派单步骤选人条件
     *
     * @param workTaskId 工单Id (流程定义时的，流程ID){需要确认由前台绑定还是后台处理，后台处理需要增加参数，工单类型：巡检 维保 抢修}
     * @return JSON {@link PersonFilterCondition}
     */
    @RequestMapping("/{workTaskId}/getDispatchCondition")
    public String dispatchWork_getFilterCondition(@PathVariable String workTaskId, ServletRequest request, Map<String, Object> map) {
        map.putAll(actCycleTaskService.dispatchWork_getFilterCondition(workTaskId));
        return dispatch;
    }

    /**
     * 手机端获取派单步骤选人条件
     *
     * @param workTaskId 工单Id (流程定义时的，流程ID){需要确认由前台绑定还是后台处理，后台处理需要增加参数，工单类型：巡检 维保 抢修}
     * @return JSON {@link PersonFilterCondition}
     */
    @RequestMapping("/{workTaskId}/mobile/getDispatchCondition")
    @ResponseBody
    public Map<String, Object> mobile_dispatchWork_getFilterCondition(@PathVariable String workTaskId) {
        return actCycleTaskService.dispatchWork_getFilterCondition(workTaskId);
    }

    /**
     * 取消派单
     *
     * @param flowTaskId 任务ID
     * @return 操作消息 {@link ProcessMessage}
     * @see #dispatchWork_getFilterCondition(String, ServletRequest, Map)
     */
    @RequestMapping("/{flowTaskId}/cancelDispatch")
    public @ResponseBody
    String cancel_dispatchWork(@PathVariable String flowTaskId) {
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了取消派单操作");
        SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        return new Gson().toJson(actCycleTaskService.cancel_dispatchWork(flowTaskId));
    }

    /**
     * 派单
     *
     * @param flowTaskId       任务ID
     * @param workTaskId       工单ID
     * @param userIds_selected 分派人员集合 形如 aaa,bbb,ccc
     * @return 分派结果
     * @see #dispatchWork_getFilterCondition(String, ServletRequest, Map)
     * @see ProcessMessage
     */
    @RequestMapping("/{flowTaskId}/dispatch")
    public @ResponseBody
    String dispatchWork(@PathVariable String flowTaskId, String workTaskId, String userIds_selected) {
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了派单操作");
        SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        return new Gson().toJson(actCycleTaskService.dispatchWork(flowTaskId, workTaskId, userIds_selected));
    }

    /**
     * 接单
     *
     * @param flowId     流程Id
     * @param workTaskId 工单ID
     * @param accept     是否接单：true 接单，false 拒绝
     * @return 接单操作结果消息
     * @see ProcessMessage
     */
    @RequestMapping("/{flowId}/receiveTask")
    public @ResponseBody
    String confirmReceive(@PathVariable String flowId, String workTaskId, boolean accept) {
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了接单操作");
        SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        return new Gson().toJson(actCycleTaskService.confirmReceive(flowId, workTaskId, accept));
    }


    /**
     * 获取处理工单步骤选人条件
     *
     * @param flowId 流程ID
     * @return ProcessMessage
     * @see ProcessMessage
     */
    @RequestMapping("/{flowId}/getDealCondition")
    public String dealTaskSubmit_getFilterCondition(@PathVariable String flowId, ServletRequest request, Map<String, Object> map) {
        map.putAll(actCycleTaskService.dealTaskSubmit_getFilterCondition(flowId));
        return dispatch;
    }

    /**
     * 手机端获取处理工单步骤选人条件
     *
     * @param flowId 流程ID
     * @return ProcessMessage
     * @see ProcessMessage
     */
    @RequestMapping("/{flowId}/mobile/getDealCondition")
    @ResponseBody
    public Map<String, Object> mobile_dealTaskSubmit_getFilterCondition(@PathVariable String flowId) {
        return actCycleTaskService.dealTaskSubmit_getFilterCondition(flowId);
    }

    /**
     * 处理工单
     *
     * @param flowTaskId       流程任务ID
     * @param workTaskId       工单ID
     * @param userIds_selected 选择的下一步骤人员形如： aaa,bbb,ccc
     * @return {@link ProcessMessage}
     * @see #dealTaskSubmit_getFilterCondition(String, ServletRequest, Map)
     */
    @RequestMapping("/{flowTaskId}/dealTask")
    public @ResponseBody
    String dealTaskSubmit(@PathVariable String flowTaskId, String workTaskId, String userIds_selected) {
        return new Gson().toJson(actCycleTaskService.dealTaskSubmit(flowTaskId, workTaskId, userIds_selected));
    }

    /**
     * 审核步骤
     *
     * @param flowIds 流程ID
     * @param pass    是否通过审核
     * @return {@link ProcessMessage}
     */
    @RequestMapping("/checkWork")
    @ResponseBody
    public String checkWorkTask(String[] flowIds, boolean pass, String desription) {
        return new Gson().toJson(actCycleTaskService.checkWorkTask(flowIds, pass, desription));
    }

    /**
     * 获取流程每个步骤的可写字段
     *
     * @param workTaskId 工单Id
     * @return
     */
    @ResponseBody
    @RequestMapping("/writable")
    public String writable(String workTaskId) {
        return new Gson().toJson(actCycleTaskService.writable(workTaskId));
    }

    @RequestMapping("/{flowTaskId}/getFormData")
    public @ResponseBody
    String queryTaskFormData(@PathVariable String flowTaskId) {
        return new Gson().toJson(actCycleTaskService.queryTaskFormData(flowTaskId));
    }

    @RequestMapping("/install")
    public @ResponseBody
    String installProcess() {
        return new Gson().toJson(actCycleTaskService.installProcess());
    }

    @RequestMapping("/uninstall")
    public String uninstallProcess() {
        return new Gson().toJson(actCycleTaskService.uninstallProcess());
    }

    public static void main(String[] args) {
        String bpmnPath = System.getProperty("user.dir") + File.separator + "process" + File.separator + "CycleTaskProcess.bpmn";
        String pngPath = System.getProperty("user.dir") + File.separator + "process" + File.separator + "CycleTaskProcess.png";

    }
}
