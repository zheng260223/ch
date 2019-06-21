package com.workprocess.services;

import com.workprocess.entity.Cyclechecktaskitemtemplate;

import java.util.List;

public interface CyclechecktaskitemtemplateService {
    List<Cyclechecktaskitemtemplate> getItemtemplateList(String templateId);

    List<Cyclechecktaskitemtemplate> findByTemplateIdAndEnabledFalse(String templateId);

    void setIemtemplateList(Cyclechecktaskitemtemplate cyclechecktaskitemtemplate);

    void upStatus(String ids, long enabled);

    List getItemtemplateListForDevice(Long deviceType);
    List getItemListForTemplate(String templateId);
    Cyclechecktaskitemtemplate getItemByTemplateIdAndItemAndStandard(String templateId, String item,String standard);
    void delItemListById(String templateId);
}
