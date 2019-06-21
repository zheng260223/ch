package com.workprocess.services;

import com.workprocess.common.model.ProcessMessage;

import java.util.Map;

public interface ActCycleTaskService {
    Map<String, Object> dispatchWork_getFilterCondition(String workTaskId);

    ProcessMessage cancel_dispatchWork(String flowTaskId);

    ProcessMessage dispatchWork(String flowTaskId, String workTaskId, String userIds_selected);

    ProcessMessage confirmReceive(String flowId, String workTaskId, boolean accept);

    Map<String, Object> dealTaskSubmit_getFilterCondition(String flowId);

    ProcessMessage dealTaskSubmit(String flowTaskId, String workTaskId, String userIds_selected);

    ProcessMessage checkWorkTask(String[] flowIds, boolean pass, String desription);

    ProcessMessage queryTaskFormData(String flowTaskId);

    ProcessMessage installProcess();

    ProcessMessage uninstallProcess();

    ProcessMessage writable(String workTaskId);
}
