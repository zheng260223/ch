package com.workprocess.services.impl;

import com.greenpanit.qtidc.entity.base.DeviceType;
import com.greenpanit.utils.Identities;
import com.workprocess.dao.CyclechecktasktemplateDAO;
import com.workprocess.entity.Cyclechecktasktemplate;
import com.workprocess.services.CyclechecktasktemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class CyclechecktasktemplateServiceImpl implements CyclechecktasktemplateService {
    @Autowired
    private CyclechecktasktemplateDAO cyclechecktasktemplateDAO;

    /**
     * 查询计划列表
     *
     * @param pageSize 每页显示条数
     * @param pageNum  当前页数(从0开始)
     * @return Page
     */
    @Override
    public Page<Cyclechecktasktemplate> getTemplateList(Integer pageSize, Integer pageNum) {
        Pageable pageable = new PageRequest(pageNum, pageSize);
        return cyclechecktasktemplateDAO.findAll(pageable);
    }

    /**
     * 添加或更新计划
     *
     * @param cyclechecktasktemplate 计划的对象
     */
    @Override
    public void setTemplate(Cyclechecktasktemplate cyclechecktasktemplate) {
        if (cyclechecktasktemplate.getId() == null || cyclechecktasktemplate.getId().equals("")) {
            cyclechecktasktemplate.setId(Identities.uuid());
        }
        cyclechecktasktemplateDAO.saveAndFlush(cyclechecktasktemplate);
    }

    /**
     * 更新计划状态
     *
     * @param ids     计划ID,以","分隔
     * @param enabled 计划状态
     */
    @Override
    public void upStatus(String ids, Long enabled) {
        String[] idsStr = ids.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(idsStr));
        cyclechecktasktemplateDAO.upStatus(list, enabled);
    }

    /**
     * 根据计划ID查询计划
     *
     * @param id 计划ID
     * @return Cyclechecktasktemplate
     */
    @Override
    public Cyclechecktasktemplate getTemplate(String id) {
        return cyclechecktasktemplateDAO.findOne(id);
    }

    /**
     * 根据周期类型查询计划
     *
     * @param serviceCycleId 周期类型值
     * @return List
     */
    @Override
    public List<Cyclechecktasktemplate> getTemplateForServiceCycle(String serviceCycleId) {
        return cyclechecktasktemplateDAO.getTemplateForServiceCycle(serviceCycleId);
    }

    /**
     * 查询所有设备类型
     *
     * @return list
     */
    @Override
    public List getDeviceList() {
        List list = new ArrayList();
        for (DeviceType deviceType : DeviceType.values()) {
            Map map = new HashMap();
            map.put("name", deviceType.getName());
            map.put("value", deviceType.getValue());
            list.add(map);
        }
        return list;
    }


    /**
     * 根据设备类型值查询设备名称
     *
     * @param value 设备类型值
     * @return String
     */
    @Override
    public String getDeviceName(int value) {
        String name = "";
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getValue() == value) {
                name = deviceType.getName();
                break;
            }
        }
        return name;
    }

    @Override
    public List<Cyclechecktasktemplate> findByDeviceType(Long deviceType) {
        return cyclechecktasktemplateDAO.findByEnabledAndDeviceType(1L, deviceType);
    }
}
