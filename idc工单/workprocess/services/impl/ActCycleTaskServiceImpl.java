package com.workprocess.services.impl;

import com.google.gson.Gson;
import com.greenpanit.qtidc.entity.base.User;
import com.greenpanit.utils.SecurityUtils;
import com.workprocess.common.CandidateQuery;
import com.workprocess.common.FormFieldQuery;
import com.workprocess.common.model.FormField;
import com.workprocess.common.model.PersonFilterCondition;
import com.workprocess.common.model.ProcessMessage;
import com.workprocess.entity.WorkTaskStatus;
import com.workprocess.entity.Worktask;
import com.workprocess.services.ActCycleTaskService;
import com.workprocess.services.WorkTaskService;
import com.workprocess.utils.Utiliy;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ActCycleTaskServiceImpl implements ActCycleTaskService {
    private final static byte[] dispatchLock = new byte[0];
    private final static byte[] receiveLock = new byte[0];

    @Autowired
    TaskService taskService;

    @Autowired
    FormService formService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private CandidateQuery candidateQuery;

    @Autowired
    private FormFieldQuery formFieldQuery;

    @Autowired
    private WorkTaskService workTaskService;

    /**
     * 获取派单步骤选人条件
     *
     * @param workTaskId 工单ID
     */
    @Override
    public Map<String, Object> dispatchWork_getFilterCondition(String workTaskId) {
        Map<String, Object> map = new HashMap<>();
        ProcessMessage<Map<String, Object>> message = new ProcessMessage<>();
        User loginUser = SecurityUtils.getLoginUser();//登录用户
        Worktask worktask = workTaskService.getWorktaskById(workTaskId);
        String processKey = "cycleTask";//默认为巡检项目
        switch (worktask.getTemplateType()) {
            case 1://巡检
                processKey = "cycleTask";
                break;
            case 2://维保
                processKey = "maintainTask";
                break;
            case 3://应急
                //processKey = "emergencyTask";
                processKey = "cycleTask";
                break;
            default://默认为巡检
                processKey = "cycleTask";
                break;
        }
        if (null == worktask.getFlowId() || StringUtils.isEmpty(worktask.getFlowId())) {
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processKey).latestVersion().singleResult();
            identityService.setAuthenticatedUserId(loginUser.getId());//设置流程启动人员
            Map<String, Object> login = new HashMap<>();
            login.put("dispatch_assignee", loginUser.getId());
            login.put("dispatch_candidateGroups", "");
            login.put("dispatch_candidateUsers", "");
            ProcessInstance instance = runtimeService.startProcessInstanceById(definition.getId(), login);//启动流程并设置第一步骤Assignee 操作人员
            Task task = taskService.createTaskQuery().taskAssignee(loginUser.getId()).processInstanceId(instance.getId()).singleResult();//查找当前任务
            if (null != task && !Objects.isNull(task)) {
                PersonFilterCondition filterCondition = candidateQuery.Query(task.getId());
                filterCondition.setFlowTaskId(task.getId());
                message.setData(workTaskService.getPersonFilterCondition(filterCondition));//获取人员过滤条件
            } else {
                message.setMessage("没有找到该任务");
                message.setCode(1);
            }
        } else {
            Task task = taskService.createTaskQuery().processInstanceId(worktask.getFlowId()).taskAssignee(loginUser.getId()).singleResult();
            if (null != task) {
                PersonFilterCondition filterCondition = candidateQuery.Query(task.getId());
                filterCondition.setFlowTaskId(task.getId());
                message.setData(workTaskService.getPersonFilterCondition(filterCondition));//获取人员过滤条件
            }
        }
        map.put("message", message);
        map.put("workTask", new Gson().toJson(worktask));
        return map;
    }

    /**
     * 取消派单
     *
     * @param flowTaskId 任务ID
     * @return ProcessMessage
     */
    @Override
    public ProcessMessage cancel_dispatchWork(String flowTaskId) {
        ProcessMessage message = new ProcessMessage();
        Task task = taskService.createTaskQuery().taskId(flowTaskId).singleResult();
        if (task != null) {
            historyService.deleteHistoricTaskInstance(task.getProcessInstanceId());
            runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "取消派单");
            taskService.deleteTask(task.getId(), "取消派单");
            message.setCode(0);
            message.setMessage("取消成功");
        } else {
            message.setCode(1);
            message.setMessage("未找到当前任务.");
        }
        return message;
    }

    /**
     * 派单
     *
     * @param flowTaskId       任务ID
     * @param workTaskId       工单ID
     * @param userIds_selected 分派人员集合 形如 aaa,bbb,ccc
     * @return ProcessMessage
     */
    @Override
    public ProcessMessage dispatchWork(String flowTaskId, String workTaskId, String userIds_selected) {
        if (!StringUtils.isEmpty(userIds_selected)) {
            userIds_selected = Utiliy.setString(userIds_selected);
        }
        ProcessMessage message = new ProcessMessage();
        User loginUser = SecurityUtils.getLoginUser();
        synchronized (dispatchLock) {
            // TODO 判断工单 flowId是否 有值,有值不允许 分派
            Task task = taskService.createTaskQuery().taskId(flowTaskId).singleResult();
            if (task != null) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("receive_candidateUsers", userIds_selected);
                taskService.complete(task.getId(), variables);
                message.setCode(0);
                message.setMessage("分派任务成功.");
                // TODO 1.流程ID(task.getProcessInstanceId()) 需要写入工单 flowId 字段
                // TODO 2.需要更改工单状态为: 待接单
                Worktask worktask = workTaskService.getWorktaskById(workTaskId);//获取工单
                if (worktask.getOriginType() != 2) {
                    worktask.setCreatorId(loginUser.getId());//自动生成的工单在派单时将派单人员id存为创建人id
                }
                worktask.setFlowId(task.getProcessInstanceId());//插入流程id
                worktask.setStatus(WorkTaskStatus.RECEIPT.getValue());//修改工单状态
                workTaskService.saveWork(worktask);//保存工单
            } else {
                message.setCode(1);
                message.setMessage("未找到当前任务,或已经被提交.");
            }
        }
        return message;
    }

    /**
     * 接单
     *
     * @param flowId     流程Id
     * @param workTaskId 工单ID
     * @param accept     是否接单：true 接单，false 拒绝
     * @return ProcessMessage
     */
    @Override
    public ProcessMessage confirmReceive(String flowId, String workTaskId, boolean accept) {
        ProcessMessage message = new ProcessMessage();
        User loginUser = SecurityUtils.getLoginUser();
        Worktask worktask = workTaskService.getWorktaskById(workTaskId);//获取工单详情
        synchronized (receiveLock) {
            Task task = taskService.createTaskQuery().taskCandidateUser(loginUser.getId())
                    .processInstanceId(flowId).singleResult();
            if (task != null && task.getTaskDefinitionKey().equals("receive")) {
                if (accept) {//接单
                    taskService.claim(task.getId(), loginUser.getId());//设置接单操作人
                    Map<String, Object> variable = new HashMap<>();
                    variable.put("accept", accept);//流转条件
                    variable.put("deal_assignee", loginUser.getId());//下一步执行人
                    variable.put("deal_candidateGroups", "");
                    taskService.complete(task.getId(), variable); //流转到下一步骤
                    // TODO 更改工单状态为 待处理
                    worktask.setInspectorId(loginUser.getId());//将接单人传入工单对象
                    worktask.setStatus(WorkTaskStatus.HANDLE.getValue());//将状态传入工单对象
                    workTaskService.updateWorkTask(worktask);//更新工单
                    message.setCode(0);
                    message.setMessage("接单成功.");
                } else {//拒绝接单
                    taskService.deleteCandidateUser(task.getId(), loginUser.getId());//候选人中删除 当前人ID
                    message.setCode(0);
                    message.setMessage("拒绝接单成功。");
                    if (candidateQuery.QueryCandidateUsers(task.getId()).equals("")) {//如果候选人全部拒绝接单
                        Map<String, Object> variable = new HashMap<>();
                        variable.put("accept", false);//流转条件
                        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                                .processInstanceId(flowId).taskDefinitionKey("dispatch").singleResult();//上一步骤 任务
                        variable.put("dispatch_assignee", historicTaskInstance.getAssignee());//设置 下一步执行人
                        variable.put("dispatch_candidateGroups", "");
                        variable.put("dispatch_candidateUsers", "");
                        taskService.complete(task.getId(), variable);
                        // TODO 更改工单状态为 待派单
                        worktask.setStatus(WorkTaskStatus.DISPATCH.getValue());//将状态传入工单对象
                        workTaskService.updateWorkTask(worktask);//更新工单
                        message.setCode(0);
                        message.setMessage("回退到派单步骤.");
                    }
                }
            } else {
                message.setCode(1);
                message.setMessage("未找到当前任务.");
            }
        }
        return message;
    }

    /**
     * 获取处理工单步骤选人条件
     *
     * @param flowId 流程ID
     */
    @Override
    public Map<String, Object> dealTaskSubmit_getFilterCondition(String flowId) {
        Map<String, Object> map = new HashMap<>();
        ProcessMessage<Map<String, Object>> message = new ProcessMessage<>();
        User loginUser = SecurityUtils.getLoginUser();
        Task task = taskService.createTaskQuery().processInstanceId(flowId)
                .taskAssignee(loginUser.getId()).singleResult();
        Worktask worktask = workTaskService.getWorktaskByFlowId(flowId);
        if (task != null && task.getTaskDefinitionKey().equals("deal")) {
            message.setCode(0);
            message.setMessage("取数据成功.");
            PersonFilterCondition filterCondition = candidateQuery.Query(task.getId());
            filterCondition.setFlowTaskId(task.getId());
            message.setData(workTaskService.getPersonFilterCondition(filterCondition));//获取人员过滤条件
        } else {
            message.setCode(1);
            message.setMessage("未找到<处理工单>步骤任务.");
        }
        map.put("message", message);
        map.put("workTask", new Gson().toJson(worktask));
        return map;
    }

    /**
     * 处理工单
     *
     * @param flowTaskId       流程任务ID
     * @param workTaskId       工单ID
     * @param userIds_selected 选择的下一步骤人员形如： aaa,bbb,ccc
     * @return ProcessMessage
     */
    @Override
    public ProcessMessage dealTaskSubmit(String flowTaskId, String workTaskId, String userIds_selected) {
        ProcessMessage message = new ProcessMessage();
        User loginUser = SecurityUtils.getLoginUser();
        Worktask worktask = workTaskService.getWorktaskById(workTaskId);//获取工单详情
        boolean check = workTaskService.dayIsWorkTask(worktask);
        Task task = taskService.createTaskQuery().taskId(flowTaskId).taskAssignee(loginUser.getId()).singleResult();
        if (task != null) {
            Map<String, Object> variable = new HashMap<>();
            variable.put("check", check);
            if (check) {
                variable.put("check_assignee", null);
                variable.put("check_candidateUsers", userIds_selected);
                taskService.complete(task.getId(), variable);
                // TODO 更改工单状态为 待审核
                worktask.setInspectorId(loginUser.getId());//将处理人传入工单对象
                worktask.setStatus(WorkTaskStatus.AUDIT.getValue());//将状态传入工单对象
                workTaskService.updateWorkTask(worktask);//更新工单
                message.setCode(0);
                message.setMessage("处理工单完成,提交到审核步骤.");
            } else {
                taskService.complete(task.getId(), variable);
                // TODO 更改工单状态为 完成
                worktask.setStatus(WorkTaskStatus.COMPLETED.getValue());//将状态传入工单对象
                workTaskService.updateWorkTask(worktask);//更新工单
                message.setCode(0);
                message.setMessage("处理工单完成.工单完成.");
            }
        } else {
            message.setCode(1);
            message.setMessage("未找到当前任务.");
        }
        return message;
    }

    /**
     * 审核步骤
     *
     * @param flowIds 流程ID
     * @param pass    是否通过审核
     * @return ProcessMessage
     */
    @Override
    public ProcessMessage checkWorkTask(String[] flowIds, boolean pass, String desription) {
        ProcessMessage message = new ProcessMessage();
        if (null == flowIds || flowIds.length <= 0) {
            message.setCode(1);
            message.setMessage("未传入流程id.");
        } else {
            List<String> flowId = new ArrayList<>();
            for (int i = 0; i < flowIds.length; i++) {
                flowId.add(flowIds[i]);
            }
            User loginUser = SecurityUtils.getLoginUser();
            List<Worktask> worktasks = workTaskService.findByFlowIdIn(flowId);//根据流程ID获取工单详情
            List<Task> tasks = taskService.createTaskQuery().processInstanceIdIn(flowId).taskCandidateUser(loginUser.getId()).list();
            for (Task task : tasks) {
                if (task != null && task.getTaskDefinitionKey().equals("check")) {
                    taskService.claim(task.getId(), loginUser.getId());//确认当前人为处理者
                    Map<String, Object> variable = new HashMap<>();
                    variable.put("pass", pass);
                    variable.put("check_assignee", loginUser.getId());
                    taskService.complete(task.getId(), variable);
                } else {
                    message.setCode(1);
                    message.setMessage("未找到流程id为" + task.getProcessInstanceId() + "的任务.");
                    return message;
                }
            }
            if (pass) {
                for (Worktask worktask : worktasks) {
                    // TODO 更改工单状态为 完成
                    worktask.setStatus(WorkTaskStatus.COMPLETED.getValue());//将状态传入工单对象
                    worktask.setDesription(desription);//添加处理情况
                    workTaskService.updateWorkTask(worktask);//更新工单
                }
                message.setCode(0);
                message.setMessage("工单完成.");
            } else {
                for (Worktask worktask : worktasks) {
                    //TODO 更改工单状态为 待处理
                    worktask.setInspectorId(loginUser.getId());//将接单人传入工单对象
                    worktask.setStatus(WorkTaskStatus.HANDLE.getValue());//将状态传入工单对象
                    worktask.setDesription(desription);//添加处理情况
                    workTaskService.updateWorkTask(worktask);//更新工单
                }
                message.setCode(0);
                message.setMessage("工单退回处理.");
            }
        }
        return message;
    }

    @Override
    public ProcessMessage queryTaskFormData(String flowTaskId) {
        List<FormField> formFields = formFieldQuery.getFormFieldList(flowTaskId);
        ProcessMessage<List<FormField>> message = new ProcessMessage<>();
        if (formFields != null && formFields.size() > 0) {
            message.setCode(0);
            message.setMessage("取数据成功");
            message.setData(formFields);
        } else {
            message.setCode(1);
            message.setMessage("未找到数据.");
        }
        return message;
    }

    @Override
    public ProcessMessage installProcess() {
        ProcessMessage message = new ProcessMessage();
//        String bpmnPath = ActCycleTaskController.class.getClassLoader().getResource("/CycleTaskProcess.bpmn").getPath();
//        String pngPath = ActCycleTaskController.class.getClassLoader().getResource("/CycleTaskProcess.png").getPath();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("/CycleTaskProcess.bpmn")
                .addClasspathResource("/CycleTaskProcess.png").deploy();
        System.out.println("ID : " + deployment.getId() + "\r\n;"
                + "Name : " + deployment.getName() + "\r\n;"
                + "KEY : " + deployment.getKey() + "\r\n;"
                + "Time : " + deployment.getDeploymentTime());
//        logger.info(bpmnPath);
        message.setCode(0);
        message.setMessage("OK");
        return message;
    }

    @Override
    public ProcessMessage uninstallProcess() {
        ProcessMessage message = new ProcessMessage();
        message.setCode(0);
        message.setMessage("OK");
        return message;
    }

    @Override
    public ProcessMessage writable(String workTaskId) {
        ProcessMessage<String> message = new ProcessMessage<>();
        User loginUser = SecurityUtils.getLoginUser();
        String writable = "";
        if (StringUtils.isEmpty(workTaskId)) {
            writable = "priority,deviceId,templateType,templateId,desription,workFile";
        } else {
            Worktask worktask = workTaskService.getWorktaskById(workTaskId);
            if (StringUtils.isEmpty(worktask.getFlowId())) {
                writable = "priority,deviceId,templateType,templateId,desription,workFile";
            } else {
                Task task = taskService.createTaskQuery().processInstanceId(worktask.getFlowId()).active().singleResult();
                if (null != task) {
                    List<FormProperty> formProperties = formService.getTaskFormData(task.getId()).getFormProperties();
                    for (FormProperty formProperty : formProperties) {
                        if (formProperty.isWritable()) {
                            writable += "," + formProperty.getId();
                        }
                    }
                }
                if (StringUtils.isNotEmpty(writable)) {
                    writable = writable.substring(1);
                }
            }
        }
        message.setCode(0);
        message.setMessage("查询成功");
        message.setData(writable);
        return message;
    }
}
