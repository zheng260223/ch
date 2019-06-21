package com.workprocess.services.impl;

import com.greenpanit.utils.Identities;
import com.workprocess.dao.EmergencytasktemplateDAO;
import com.workprocess.entity.Emergencytasktemplate;
import com.workprocess.services.EmergencytasktemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmergencytasktemplateServiceImpl implements EmergencytasktemplateService {
    @Autowired
    private EmergencytasktemplateDAO emergencytasktemplateDAO;

    /**
     * 查询应急模板列表
     *
     * @param pageSize 每页显示条数
     * @param pageNum  当前页数(从0开始)
     * @return Page
     */
    @Override
    public Page<Emergencytasktemplate> getTemplateList(Integer pageSize, Integer pageNum) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        return emergencytasktemplateDAO.findAll(pageable);
    }

    /**
     * 添加或更新应急模板
     *
     * @param emergencytasktemplate 应急计划的对象
     */
    @Override
    public void setTemplate(Emergencytasktemplate emergencytasktemplate) {
        if (emergencytasktemplate.getId() == null || "".equals(emergencytasktemplate.getId())) {
            emergencytasktemplate.setId(Identities.uuid());
        }
        emergencytasktemplateDAO.saveAndFlush(emergencytasktemplate);
    }

    /**
     * 更新应急模板状态
     *
     * @param ids     应急计划ID,以","分隔
     * @param enabled 计划状态
     */
    @Override
    public void upStatus(String ids, Long enabled) {
        String[] idsStr = ids.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(idsStr));
        emergencytasktemplateDAO.upStatus(list, enabled);
    }

    /**
     * 根据应急模板ID查询该模板
     *
     * @param id 应急计划ID
     * @return Emergencytasktemplate
     */
    @Override
    public Emergencytasktemplate getTemplate(String id) {
        return emergencytasktemplateDAO.findOne(id);
    }

    @Override
    public List<Emergencytasktemplate> findByDeviceType(Long deviceType) {
        return emergencytasktemplateDAO.findByEnabledAndDeviceType(1L, deviceType);
    }
}
