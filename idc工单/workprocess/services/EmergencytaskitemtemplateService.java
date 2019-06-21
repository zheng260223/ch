package com.workprocess.services;

import com.workprocess.entity.Emergencytaskitemtemplate;

import java.util.List;

public interface EmergencytaskitemtemplateService {
    List<Emergencytaskitemtemplate> getItemtemplateList(String templateId);

    void upStatus(String ids, long enabled);

    List getItemtemplateListForDevice(Long deviceType);

    List getItemListForTemplate(String templateId);

    void setIemtemplateList(Emergencytaskitemtemplate emergencytaskitemtemplate);

    void delItemListById(String templateId);

    List findByEnabledFalseAndDevice(Long deviceType);

    List<Emergencytaskitemtemplate> findByTemplateIdAndEnabledFalse(String templateId);
}
