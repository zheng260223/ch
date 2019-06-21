package com.workprocess.services;

import com.workprocess.entity.Emergencytasktemplate;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmergencytasktemplateService {
    Page<Emergencytasktemplate> getTemplateList(Integer pageSize, Integer pageNum);

    void setTemplate(Emergencytasktemplate emergencytasktemplate);

    void upStatus(String ids, Long enabled);

    Emergencytasktemplate getTemplate(String id);

    List<Emergencytasktemplate> findByDeviceType(Long deviceType);

}
