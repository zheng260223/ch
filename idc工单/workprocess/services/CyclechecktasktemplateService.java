package com.workprocess.services;

import com.workprocess.entity.Cyclechecktasktemplate;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CyclechecktasktemplateService {
    Page<Cyclechecktasktemplate> getTemplateList(Integer pageSize, Integer pageNum);

    void setTemplate(Cyclechecktasktemplate cyclechecktasktemplate);

    void upStatus(String ids, Long enabled);

    Cyclechecktasktemplate getTemplate(String id);

    List<Cyclechecktasktemplate> getTemplateForServiceCycle(String serviceCycleId);

    List getDeviceList();

    String getDeviceName(int value);

    List<Cyclechecktasktemplate> findByDeviceType(Long deviceType);
}
