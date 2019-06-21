package com.workprocess.services.impl;

import com.greenpanit.utils.Identities;
import com.workprocess.dao.EmergencytaskitemtemplateDAO;
import com.workprocess.entity.Emergencytaskitemtemplate;
import com.workprocess.services.EmergencytaskitemtemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmergencytaskitemtemplateServiceImpl implements EmergencytaskitemtemplateService {
    @Autowired
    private EmergencytaskitemtemplateDAO emergencytaskitemtemplateDAO;

    /**
     * 根据应急模板ID查询应急项目
     *
     * @param templateId 应急模板ID
     * @return List
     */
    @Override
    public List<Emergencytaskitemtemplate> getItemtemplateList(String templateId) {
        return emergencytaskitemtemplateDAO.getItemtemplateList(templateId);
    }

    /**
     * 更新应急模板状态
     *
     * @param ids     应急模板ID(以","分隔)
     * @param enabled 应急模板状态
     */
    @Override
    public void upStatus(String ids, long enabled) {
        String[] idsStr = ids.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(idsStr));
        emergencytaskitemtemplateDAO.upStatus(list, enabled);
    }

    /**
     * 根据设备ID查询应急项目清单(去重)
     *
     * @param deviceType 设备类型
     * @return List
     */
    @Override
    public List getItemtemplateListForDevice(Long deviceType) {
        List<Map<String, Object>> deviceList = new ArrayList<>();
        for (Object[] objs : emergencytaskitemtemplateDAO.getItemListForDevice(deviceType)) {
            Map<String, Object> objMap = new HashMap<>();
            objMap.put("standard", objs[1]);
            objMap.put("item", objs[0]);
            deviceList.add(objMap);
        }
        return deviceList;
    }

    /**
     * 根据设备ID查询应急项目清单(去重)
     *
     * @param deviceType 设备类型
     * @return List
     */
    @Override
    public List findByEnabledFalseAndDevice(Long deviceType) {
        return emergencytaskitemtemplateDAO.findByEnabledFalseAndDevice(deviceType);
    }

    /**
     * 根据计划ID查询项目清单
     *
     * @param templateId 计划ID
     * @return List
     */
    @Override
    public List getItemListForTemplate(String templateId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] objs : emergencytaskitemtemplateDAO.getItemListForTemplate(templateId)) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", objs[0]);
            map.put("standard", objs[1]);
            list.add(map);
        }
        return list;
    }

    /**
     * 添加或更新项目
     *
     * @param emergencytaskitemtemplate 计划的对象
     */
    @Override
    public void setIemtemplateList(Emergencytaskitemtemplate emergencytaskitemtemplate) {
        if (emergencytaskitemtemplate.getId() == null || emergencytaskitemtemplate.getId().equals("")) {
            emergencytaskitemtemplate.setId(Identities.uuid());
        }
        emergencytaskitemtemplateDAO.saveAndFlush(emergencytaskitemtemplate);
    }

    /**
     * 根据计划ID删除项目
     *
     * @param templateId 计划ID
     */
    @Override
    public void delItemListById(String templateId) {
        emergencytaskitemtemplateDAO.delItemListById(templateId);
    }

    /**
     * 根据计划ID查询未禁用的项目
     *
     * @param templateId 计划ID
     * @return List
     */
    @Override
    public List<Emergencytaskitemtemplate> findByTemplateIdAndEnabledFalse(String templateId) {
        return emergencytaskitemtemplateDAO.findByTemplateIdAndEnabled(templateId, 1L);
    }
}
