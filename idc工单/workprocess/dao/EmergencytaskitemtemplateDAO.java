package com.workprocess.dao;

import com.workprocess.entity.Emergencytaskitemtemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface EmergencytaskitemtemplateDAO extends JpaRepository<Emergencytaskitemtemplate, String>, JpaSpecificationExecutor<Emergencytaskitemtemplate> {
    @Query("from Emergencytaskitemtemplate where templateId=:templateId")
    List<Emergencytaskitemtemplate> getItemtemplateList(@Param("templateId") String templateId);

    @Modifying
    @Transactional
    @Query("update Emergencytaskitemtemplate set enabled=:enabled where id in (:ids)")
    void upStatus(@Param("ids") List<String> ids, @Param("enabled") Long enabled);

    @Query(value = "select distinct a.Item,a.Standard from emergencytaskitemtemplate a, emergencytasktemplate b" +
            " where a.TemplateID = b.ID and b.DeviceType = ?1", nativeQuery = true)
    List<Object[]> getItemListForDevice(Long deviceType);

    @Query(value = "select distinct a.Item,a.Standard from emergencytaskitemtemplate a, emergencytasktemplate b" +
            " where a.enabled =1 and b.enabled=1 and a.TemplateID = b.ID and b.DeviceType = ?1", nativeQuery = true)
    List<Map> findByEnabledFalseAndDevice(Long deviceType);

    @Query(value = "select a.Item,a.Standard from emergencytaskitemtemplate a, emergencytasktemplate b" +
            " where a.TemplateID = b.ID and b.ID = ?1", nativeQuery = true)
    List<Object[]> getItemListForTemplate(String templateId);

    @Modifying
    @Transactional
    @Query("delete from Emergencytaskitemtemplate where templateId=:templateId")
    void delItemListById(@Param("templateId") String templateId);

    List<Emergencytaskitemtemplate> findByTemplateIdAndEnabled(String templateId, Long enabled);

}
