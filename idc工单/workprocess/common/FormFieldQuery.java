package com.workprocess.common;

import com.workprocess.common.model.FormField;
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前任务字段设定
 */
@Component
public class FormFieldQuery {

    @Autowired
    private FormService formService;

    /**
     * 获取Form 字段设置列表
     * @param taskId 任务Id
     * @see Task#getId()
     * @return 字段列表
     */
    public List<FormField> getFormFieldList(String taskId){
        TaskFormData formData = formService.getTaskFormData(taskId);
        List<FormProperty> properties = formData.getFormProperties();
        List<FormField> formFields = new ArrayList<>();
        for(FormProperty property : properties){
            formFields.add(FormField.getFormField(property));
        }
        return formFields;
    }
}
