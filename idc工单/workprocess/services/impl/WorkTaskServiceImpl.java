package com.workprocess.services.impl;

import com.google.gson.Gson;
import com.greenpanit.qtidc.dao.base.RoleDAO;
import com.greenpanit.qtidc.dao.base.UserDAO;
import com.greenpanit.qtidc.entity.base.Role;
import com.greenpanit.qtidc.entity.base.User;
import com.greenpanit.utils.Identities;
import com.greenpanit.utils.SecurityUtils;
import com.workprocess.common.model.PersonFilterCondition;
import com.workprocess.dao.*;
import com.workprocess.entity.*;
import com.workprocess.services.WorkTaskService;
import com.workprocess.utils.PageUtils;
import com.workprocess.utils.Utiliy;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WorkTaskServiceImpl implements WorkTaskService {
    @Autowired
    WorkTaskDao workTaskDao;
    @Autowired
    CyclechecktaskitemDAO cyclechecktaskitemDAO;
    @Autowired
    CyclechecktasktemplateDAO cyclechecktasktemplateDAO;
    @Autowired
    CycleCheckTaskItemTemplateDAO cycleCheckTaskItemTemplateDAO;
    @Autowired
    TaskAdjunctDao taskAdjunctDao;
    @Autowired
    EmergencytaskitemDAO emergencytaskitemDAO;
    @Autowired
    EmergencytasktemplateDAO emergencytasktemplateDAO;
    @Autowired
    EmergencytaskitemtemplateDAO emergencytaskitemtemplateDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    RoleDAO roleDAO;
    @Autowired
    DeviceDAO deviceDAO;
    @Autowired
    ServiceCycleDao serviceCycleDao;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static String[] templateType = {"周期巡检", "维护保养", "应急处理"};

    /**
     * 手动新增工单
     *
     * @param worktask
     * @return
     */
    @Override
    public String saveWork(Worktask worktask) {
        //根据不同的工单类型将检查项放入不同的表中
        if (null == worktask) {
            throw new NullPointerException("工单为空");
        }
        String deviceName = deviceDAO.findByDeviceTypeAndId(worktask.getDeviceType(), worktask.getDeviceId()).getName();
        if (null == worktask.getStatus() || worktask.getStatus() == 0) {
            worktask.setStatus(1);
        }
        if ("null".equals(worktask.getFlowId())) {
            worktask.setFlowId(null);
        }
        if (null == worktask.getTaskName() || StringUtils.isEmpty(worktask.getTaskName())) {
            worktask.setTaskName(this.templateType[worktask.getTemplateType() - 1] + "-" + deviceName + "-" + df.format(new Date()));
        }
        if (null == worktask.getCreatorId() || StringUtils.isEmpty(worktask.getCreatorId())) {
            worktask.setCreatorId(SecurityUtils.getLoginUser().getId());
        }
        worktask.setDeleted(false);
        worktask = workTaskDao.save(worktask);
        switch (worktask.getTemplateType()) {
            case 1:
                List<Cyclechecktaskitem> cyclechecktaskitemList = new ArrayList<>();
                if (!StringUtils.isEmpty(worktask.getId())) {
                    cyclechecktaskitemList = cyclechecktaskitemDAO.findByWorkTaskId(worktask.getId());
                    if (null != cyclechecktaskitemList || cyclechecktaskitemList.size() > 0) {
                        cyclechecktaskitemDAO.delete(cyclechecktaskitemList);
                    }
                    cyclechecktaskitemList.removeAll(cyclechecktaskitemList);
                }
                List<Cyclechecktaskitemtemplate> cyList = cycleCheckTaskItemTemplateDAO.findByTemplateIdAndEnabled(worktask.getTemplateId(), 1L);
                for (Cyclechecktaskitemtemplate cyclechecktaskitemtemplate : cyList) {
                    Cyclechecktaskitem cyclechecktaskitem = new Cyclechecktaskitem();
                    cyclechecktaskitem.setId(Identities.uuid());
                    cyclechecktaskitem.setIsNormal(1L);
                    cyclechecktaskitem.setStandard(cyclechecktaskitemtemplate.getStandard());
                    cyclechecktaskitem.setWorkTaskId(worktask.getId());
                    cyclechecktaskitem.setSerialnumber(cyclechecktaskitemtemplate.getSerialnumber());
                    cyclechecktaskitem.setTemplateId(worktask.getTemplateId());
                    cyclechecktaskitem.setTemplateItemId(cyclechecktaskitemtemplate.getId());
                    cyclechecktaskitem.setItem(cyclechecktaskitemtemplate.getItem());
                    cyclechecktaskitemList.add(cyclechecktaskitem);
                }
                cyclechecktaskitemDAO.save(cyclechecktaskitemList);
                break;
            case 2:
                break;
            case 3:
                List<Emergencytaskitem> emergencytaskitemList = new ArrayList<>();
                if (!StringUtils.isEmpty(worktask.getId())) {
                    emergencytaskitemList = emergencytaskitemDAO.findByWorkTaskId(worktask.getId());
                    if (null != emergencytaskitemList || emergencytaskitemList.size() > 0) {
                        emergencytaskitemDAO.delete(emergencytaskitemList);
                    }
                    emergencytaskitemList.removeAll(emergencytaskitemList);
                }
                List<Emergencytaskitemtemplate> emergencytaskitemtemplates = emergencytaskitemtemplateDAO.findByTemplateIdAndEnabled(worktask.getTemplateId(), 1L);
                for (Emergencytaskitemtemplate emergencytaskitemtemplate : emergencytaskitemtemplates) {
                    Emergencytaskitem emergencytaskitem = new Emergencytaskitem();
                    emergencytaskitem.setId(Identities.uuid());
                    emergencytaskitem.setIsNormal(1L);
                    emergencytaskitem.setStandard(emergencytaskitemtemplate.getStandard());
                    emergencytaskitem.setWorkTaskId(worktask.getId());
                    emergencytaskitem.setSerialnumber(emergencytaskitemtemplate.getSerialnumber());
                    emergencytaskitem.setTemplateId(worktask.getTemplateId());
                    emergencytaskitem.setTemplateItemId(emergencytaskitemtemplate.getId());
                    emergencytaskitem.setItem(emergencytaskitemtemplate.getItem());
                    emergencytaskitemList.add(emergencytaskitem);
                }
                emergencytaskitemDAO.save(emergencytaskitemList);
                break;
            default:
                break;
        }
        return worktask.getId();
    }

    @Override
    public void updateWorkTask(Worktask worktask) {
        workTaskDao.saveAndFlush(worktask);
    }

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
    @Override
    public Page<Worktask> getWorkTaskByTemplateType(Integer templateType, Integer status, Integer deviceType, Integer pageSize, Integer pageNumber) {
        Pageable pageable = PageUtils.verify(pageSize, pageNumber, new Sort(Sort.Direction.DESC, "priority", "serialnumber"));
        Page<Worktask> page;
        if (null == deviceType) {
            page = workTaskDao.findByDeletedFalseAndTemplateTypeAndStatus(templateType, status, pageable);
        } else {
            page = workTaskDao.findByDeletedFalseAndTemplateTypeAndStatusAndDeviceType(templateType, status, deviceType, pageable);
        }
        return page;
    }

    /**
     * 批量逻辑删除工单
     *
     * @param ids 工单id数组
     * @return
     */
    @Override
    public Boolean deleteByIds(String[] ids) {
        List<Worktask> list = workTaskDao.findByDeletedFalseAndIdIn(ids);
        for (Worktask worktask : list) {
            worktask.setDeleted(true);
        }
        workTaskDao.save(list);
        return true;
    }

    /**
     * 查询工单详情(未做完)
     *
     * @param id 工单id
     * @return
     */
    @Override
    public Map<String, Object> findOne(String id) {
        Map<String, Object> map = new HashMap<>();
        Worktask worktask = workTaskDao.findOne(id);
        List<Taskadjunct> taskadjuncts = taskAdjunctDao.findByWorkTaskId(worktask.getId());
        if (null != worktask.getCreatorId() && !StringUtils.isEmpty(worktask.getCreatorId())) {
            Object[] userObj=userDAO.getNameById(worktask.getCreatorId());
            Map<String,Object> userMap=new HashMap<>();
            userMap.put("id",userObj[0]);
            userMap.put("username",userObj[1]);
            map.put("User", userMap);
        }
        Device device = deviceDAO.findByDeviceTypeAndId(worktask.getDeviceType(), worktask.getDeviceId());
        map.put("device", device);
        map.put("WorkTask", worktask);
        map.put("TaskAjunct", new ArrayList<>(taskadjuncts));
        switch (worktask.getTemplateType()) {
            case 1:
                Cyclechecktasktemplate cyclechecktasktemplate = cyclechecktasktemplateDAO.findOne(worktask.getTemplateId());
                map.put("Template", cyclechecktasktemplate);
                break;
            case 2:
                break;
            case 3:
                Emergencytasktemplate emergencytasktemplate = emergencytasktemplateDAO.findOne(worktask.getTemplateId());
                map.put("Template", emergencytasktemplate);
                break;
            default:
                break;
        }
        return map;
    }

    @Override
    public List<?> findItem(Integer templateType, String workTaskId, String templateId) {
        List<?> list = new ArrayList();
        switch (templateType) {
            case 1:
                if (null == workTaskId || StringUtils.isEmpty(workTaskId)) {
                    list = cycleCheckTaskItemTemplateDAO.findByTemplateIdAndEnabled(templateId, 1L);
                } else {
                    list = cyclechecktaskitemDAO.findByWorkTaskId(workTaskId);
                }
                break;
            case 2:
                break;
            case 3:

                if (null == workTaskId || StringUtils.isEmpty(workTaskId)) {
                    list = emergencytaskitemtemplateDAO.findByTemplateIdAndEnabled(templateId, 1L);
                } else {
                    list = emergencytaskitemDAO.findByWorkTaskId(workTaskId);
                }
                break;
            default:
                return null;
        }
        return list;
    }


    @Override
    public List<User> getUser(String roleId) {
        return userDAO.findByRole(roleId);
    }

    @Override
    public Worktask getWorktaskById(String id) {
        return workTaskDao.findOne(id);
    }

    /*    @Override
        public Map<String, Object> getPersonFilterCondition(PersonFilterCondition personFilterCondition) {
            Map<String, Object> map = new HashMap<>();
            map.put("flowTaskId", personFilterCondition.getFlowTaskId());
            if (!personFilterCondition.getRoleIds().equals("0")) {
                List<Role> roles = roleDAO.findByIdIn(personFilterCondition.getRoleIdsList());
                map.put("roles", roles);
            } else {
                map.put("roles", roleDAO.findAll());
            }
            if (!personFilterCondition.getUserIds().equals("0")) {
                List<User> users = userDAO.findByIdIn(personFilterCondition.getUserIdsList());
                map.put("users", users);
            } else {
                map.put("users", userDAO.findAll());
            }
            //现在未用组织这个模块
            if (!personFilterCondition.getDeptIds().equals("0")) {
            }
            return map;
        }*/
    @Override
    public Map<String, Object> getPersonFilterCondition(PersonFilterCondition personFilterCondition) {
        Map<String, Object> map = new HashMap<>();
        map.put("flowTaskId", personFilterCondition.getFlowTaskId());
        if (!personFilterCondition.getRoleIds().equals("0")) {
            map.put("roles", getRoles(personFilterCondition.getRoleIdsList()));
        } else {
            map.put("roles", getRoles(null));
        }
        if (!personFilterCondition.getUserIds().equals("0")) {
            map.put("users", getUsers(personFilterCondition.getUserIdsList()));
        } else {
            map.put("users", getUsers(null));
        }
        //现在未用组织这个模块
        if (!personFilterCondition.getDeptIds().equals("0")) {
        }
        return map;
    }

    private List getRoles(List<String> roleIds) {
        List<Object> roleList = new ArrayList<>();
        List<Object[]> getRoleName = roleDAO.getRoleNameById(roleIds);
        for (Object[] roleObjs : getRoleName) {
            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("roleId", roleObjs[0]);
            roleMap.put("roleName", roleObjs[1]);
            List<Object> userList = new ArrayList<>();
            List<Object[]> getUsers = roleDAO.getUsersByRoleId(String.valueOf(roleObjs[0]));
            for (Object[] userObjs : getUsers) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", userObjs[0]);
                userMap.put("userName", userObjs[1]);
                userList.add(userMap);
            }
            roleMap.put("userList", userList);
            roleList.add(roleMap);
        }
        return roleList;
    }

    private List getUsers(List<String> userIds) {
        List<Object> userList = new ArrayList<>();
        List<Object[]> getUserName = userDAO.getUserNameById(userIds);
        for (Object[] userObjs : getUserName) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", userObjs[0]);
            userMap.put("userName", userObjs[1]);
            userList.add(userMap);
        }
        return userList;
    }


    @Override
    public Map<String, Object> getPersonFilterConditionForHandle(PersonFilterCondition personFilterCondition) {
        Map<String, Object> map = new HashMap<>();
        map.put("flowTaskId", personFilterCondition.getFlowTaskId());
        if (!personFilterCondition.getRoleIds().equals("0")) {
            List<Role> roles = roleDAO.findByIdIn(personFilterCondition.getRoleIdsList());
            map.put("roles", roles);
        } else {
            map.put("roles", roleDAO.findAll());
        }
        if (!personFilterCondition.getUserIds().equals("0")) {
            List<User> users = userDAO.findByIdIn(personFilterCondition.getUserIdsList());
            map.put("users", users);
        } else {
            map.put("users", userDAO.findAll());
        }
        //现在未用组织这个模块
        if (!personFilterCondition.getDeptIds().equals("0")) {
        }
        return map;
    }

    @Override
    public Worktask getWorktaskByFlowId(String flowId) {
        return workTaskDao.findByFlowId(flowId);
    }

    @Override
    public List<Worktask> findByFlowIdIn(List<String> flowIds) {
        return workTaskDao.findByFlowIdIn(flowIds);
    }

    @Override
    public Boolean dayIsWorkTask(Worktask worktask) {
        if (3 != worktask.getTemplateType()) {
            String cycle = "";
            switch (worktask.getTemplateType()) {
                case 1:
                    cycle = cyclechecktasktemplateDAO.findOne(worktask.getTemplateId()).getServiceCycleId();
                    Servicecycle servicecycle = serviceCycleDao.findOne(cycle);
                    if (null != servicecycle && 1 == servicecycle.getCycleType()) {
                        return false;
                    }
                    return true;
                case 2://维保未做
                    break;
                default:
                    return true;
            }
        }
        return true;
    }

    @Override
    public Page<Worktask> findByDeletedFalseAndDeviceTypeAndDeviceIdAndStatus(Integer deviceType, String deviceId, Integer pageSize, Integer pageNumber) {
        Pageable pageable = PageUtils.verify(pageSize, pageNumber);
        return workTaskDao.findByDeletedFalseAndDeviceTypeAndDeviceIdOrderByCreateTimeDesc(deviceType, deviceId, pageable);
    }

    @Override
    public Map<String, Object> getTemplate(Integer templateType, String templateId) {
        Map<String, Object> templateMap = new HashMap<>();
        try {
            switch (templateType) {
                case 1:
                    templateMap.putAll(Utiliy.objectToMap(cyclechecktasktemplateDAO.findOne(templateId)));
                    break;
                case 2:
                    break;
                case 3:
                    templateMap.putAll(Utiliy.objectToMap(emergencytasktemplateDAO.findOne(templateId)));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return templateMap;
    }

    @Override
    public void dealTask(Integer templateType, String[] id, String[] conditions, String[] execution, Long[] isNormal, String workTaskId, String didCondition) {
        Integer normal = 1;
        if (null != id && id.length > 0) {
            switch (templateType) {
                case 1:
                    List<Cyclechecktaskitem> cyclechecktaskitems = new ArrayList<>();
                    for (int i = 0; i < id.length; i++) {
                        if (isNormal[i] == 0) {
                            normal = 0;
                        }
                        Cyclechecktaskitem cyclechecktaskitem = cyclechecktaskitemDAO.findOne(id[i]);
                        cyclechecktaskitem.setConditions(conditions[i]);
                        cyclechecktaskitem.setExecution(execution[i]);
                        cyclechecktaskitem.setIsNormal(isNormal[i]);
                        cyclechecktaskitem.setCheckedTime(new Timestamp(System.currentTimeMillis()));
                        cyclechecktaskitems.add(cyclechecktaskitem);
                    }
                    cyclechecktaskitemDAO.save(cyclechecktaskitems);
                    break;
                case 2:
                    break;
                case 3:
                    List<Emergencytaskitem> emergencytaskitems = new ArrayList<>();
                    for (int i = 0; i < id.length; i++) {
                        if (isNormal[i] == 0) {
                            normal = 0;
                        }
                        Emergencytaskitem emergencytaskitem = emergencytaskitemDAO.findOne(id[i]);
                        emergencytaskitem.setConditions(conditions[i]);
                        emergencytaskitem.setExecution(execution[i]);
                        emergencytaskitem.setIsNormal(isNormal[i]);
                        emergencytaskitem.setCheckedTime(new Timestamp(System.currentTimeMillis()));
                        emergencytaskitems.add(emergencytaskitem);
                    }
                    emergencytaskitemDAO.save(emergencytaskitems);
                    break;
                default:
                    break;
            }
        }
        if (null != workTaskId && StringUtils.isNotEmpty(workTaskId)) {
            Worktask worktask = workTaskDao.findOne(workTaskId);
            worktask.setIsNormal(normal);
            worktask.setDidCondition(didCondition);
            workTaskDao.save(worktask);
        }
    }

    @Override
    public Integer getMaxSerialNumber() {
        Integer max = workTaskDao.getMaxSerialNumber();
        if (null == max || max == 0) {
            return 0;
        }
        return max;
    }

    @Override
    public Page<Worktask> getWorkTaskList(Integer templateType, Integer status, String taskName, Integer deviceType, Integer priority, Integer pageSize, Integer pageNumber) {
        Pageable pageable = PageUtils.verify(pageSize, pageNumber);
        String templateTypeStr = templateType < 1 ? "" : String.valueOf(templateType);
        String statusStr = status < 1 ? "" : String.valueOf(status);
        String deviceTypeStr = deviceType < 1 ? "" : String.valueOf(deviceType);
        String priorityStr = priority < 1 ? "" : String.valueOf(priority);
        return workTaskDao.getWorkTaskList(templateTypeStr, statusStr, taskName, deviceTypeStr, priorityStr, pageable);
    }
}
