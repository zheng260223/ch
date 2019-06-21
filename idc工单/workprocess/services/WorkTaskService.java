package com.workprocess.services;

import com.greenpanit.qtidc.entity.base.User;
import com.workprocess.common.model.PersonFilterCondition;
import com.workprocess.entity.Worktask;
import org.hibernate.jdbc.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WorkTaskService {
    /**
     * 新增工单
     *
     * @param worktask
     * @return
     */
    String saveWork(Worktask worktask);

    /**
     * 更新工单对象
     *
     * @param worktask
     */
    void updateWorkTask(Worktask worktask);

    /**
     * 分页获取工单列表
     *
     * @param templateType 模板类型(1=周期巡检；2=维护保养；3=应急处理)
     * @param status       状态(1=待派；2=待接；3=处理；4=待审；5=已关)
     * @param deviceType   设备类型
     * @param pageSize     获取条数
     * @param pageNumber   当前页数
     * @return
     */
    Page<Worktask> getWorkTaskByTemplateType(Integer templateType, Integer status, Integer deviceType, Integer pageSize, Integer pageNumber);

    /**
     * 批量逻辑删除工单
     *
     * @param ids 工单id数组
     * @return
     */
    Boolean deleteByIds(String[] ids);

    /**
     * 查询工单详情(未做完)
     *
     * @param id 工单id
     * @return
     */
    Map<String, Object> findOne(String id);

    List<?> findItem(Integer templateType, String workTaskId, String templateId);

    List<User> getUser(String roleId);

    Worktask getWorktaskById(String id);

    /**
     * 获取可以选择的人员或者角色
     *
     * @param personFilterCondition
     * @return
     */
    Map<String, Object> getPersonFilterCondition(PersonFilterCondition personFilterCondition);

    /**
     * 工单处理步骤选人条件
     *
     * @param personFilterCondition
     * @return
     */
    Map<String, Object> getPersonFilterConditionForHandle(PersonFilterCondition personFilterCondition);

    /**
     * 根据流程实例ID获取工单详情
     *
     * @param flowId
     * @return
     */
    Worktask getWorktaskByFlowId(String flowId);

    List<Worktask> findByFlowIdIn(List<String> flowIds);

    Boolean dayIsWorkTask(Worktask worktask);

    /**
     * 根据设备类型,设备ID分页查询工单列表
     *
     * @param deviceType 设备类型
     * @param deviceId   设备ID
     * @param pageSize   获取条数
     * @param pageNumber 当前页数
     * @return Page<Worktask>
     */
    Page<Worktask> findByDeletedFalseAndDeviceTypeAndDeviceIdAndStatus(Integer deviceType, String deviceId, Integer pageSize, Integer pageNumber);

    /**
     * 根据项目类型与项目ID获取项目
     *
     * @param templateType 项目类型
     * @param templateId   项目ID
     * @return
     */
    Map<String, Object> getTemplate(Integer templateType, String templateId);

    void dealTask(Integer templateType, String[] id, String[] conditions, String[] execution, Long[] isNormal, String workTaskId, String didCondition);

    Integer getMaxSerialNumber();

    Page<Worktask> getWorkTaskList(Integer templateType, Integer status,String taskName,Integer deviceType,Integer priority,Integer pageSize, Integer pageNumber);

}
