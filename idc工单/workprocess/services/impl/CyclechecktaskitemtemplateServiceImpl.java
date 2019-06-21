package com.workprocess.services.impl;

import com.greenpanit.utils.Identities;
import com.workprocess.dao.CycleCheckTaskItemTemplateDAO;
import com.workprocess.entity.Cyclechecktaskitemtemplate;
import com.workprocess.services.CyclechecktaskitemtemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CyclechecktaskitemtemplateServiceImpl implements CyclechecktaskitemtemplateService {
    @Autowired
    private CycleCheckTaskItemTemplateDAO cycleCheckTaskItemTemplateDAO;

    /**
     * 根据计划ID查询项目
     *
     * @param templateId 计划ID
     * @return List
     */
    @Override
    public List<Cyclechecktaskitemtemplate> getItemtemplateList(String templateId) {
        return cycleCheckTaskItemTemplateDAO.getItemtemplateList(templateId);
    }

    /**
     * 根据计划ID查询未禁用的项目
     *
     * @param templateId 计划ID
     * @return List
     */
    @Override
    public List<Cyclechecktaskitemtemplate> findByTemplateIdAndEnabledFalse(String templateId) {
        return cycleCheckTaskItemTemplateDAO.findByTemplateIdAndEnabled(templateId, 1L);
    }

    /**
     * 添加或更新项目
     *
     * @param cyclechecktaskitemtemplate 计划的对象
     */
    @Override
    public void setIemtemplateList(Cyclechecktaskitemtemplate cyclechecktaskitemtemplate) {
        if (cyclechecktaskitemtemplate.getId() == null || cyclechecktaskitemtemplate.getId().equals("")) {
            cyclechecktaskitemtemplate.setId(Identities.uuid());
        }
        cycleCheckTaskItemTemplateDAO.saveAndFlush(cyclechecktaskitemtemplate);
    }

    /**
     * 更新项目状态
     *
     * @param ids     项目ID(以","分隔)
     * @param enabled 项目状态
     */
    @Override
    public void upStatus(String ids, long enabled) {
        String[] idsStr = ids.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(idsStr));
        cycleCheckTaskItemTemplateDAO.upStatus(list, enabled);
    }

    /**
     * 根据设备ID查询项目清单(去重)
     *
     * @param deviceType 设备类型
     * @return List
     */
    @Override
    public List getItemtemplateListForDevice(Long deviceType) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] objs : cycleCheckTaskItemTemplateDAO.getItemListForDevice(deviceType)) {
            Map<String, Object> map = new HashMap<>();
            map.put("item", objs[0]);
            map.put("standard", objs[1]);
            list.add(map);
        }
        return list;
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
        for (Object[] objs : cycleCheckTaskItemTemplateDAO.getItemListForTemplate(templateId)) {
            Map<String, Object> map = new HashMap<>();
            map.put("standard", objs[1]);
            map.put("item", objs[0]);
            list.add(map);
        }
        return list;
    }

    /**
     * 根据计划ID,项目内容,项目标准查询项目
     *
     * @param templateId 计划ID
     * @param item       项目内容
     * @param standard   项目标准
     */
    @Override
    public Cyclechecktaskitemtemplate getItemByTemplateIdAndItemAndStandard(String templateId, String item, String standard) {
        return cycleCheckTaskItemTemplateDAO.getItemByTemplateIdAndItemAndStandard(templateId, item, standard);
    }

    /**
     * 根据计划ID删除项目
     *
     * @param templateId 计划ID
     */
    @Override
    public void delItemListById(String templateId) {
        cycleCheckTaskItemTemplateDAO.delItemListById(templateId);
    }
}
