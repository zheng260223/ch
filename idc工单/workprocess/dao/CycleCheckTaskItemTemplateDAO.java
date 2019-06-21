package com.workprocess.dao;

import com.workprocess.entity.Cyclechecktaskitemtemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CycleCheckTaskItemTemplateDAO extends JpaRepository<Cyclechecktaskitemtemplate, String>, JpaSpecificationExecutor<Cyclechecktaskitemtemplate> {

    @Query("from Cyclechecktaskitemtemplate where templateId=:templateId")
    List<Cyclechecktaskitemtemplate> getItemtemplateList(@Param("templateId") String templateId);

    List<Cyclechecktaskitemtemplate> findByTemplateIdAndEnabled(String templateId, Long enabled);

    @Modifying
    @Transactional
    @Query("update Cyclechecktaskitemtemplate set enabled=:enabled where id in (:ids)")
    void upStatus(@Param("ids") List<String> ids, @Param("enabled") Long enabled);

    @Query(value = "select distinct a.Item,a.Standard from cyclechecktaskitemtemplate a, cyclechecktasktemplate b" +
            " where a.TemplateID = b.ID and b.DeviceType = ?1", nativeQuery = true)
    List<Object[]> getItemListForDevice(Long deviceType);

    @Query(value = "select a.Item,a.Standard from cyclechecktaskitemtemplate a, cyclechecktasktemplate b" +
            " where a.TemplateID = b.ID and b.ID = ?1", nativeQuery = true)
    List<Object[]> getItemListForTemplate(String templateId);

    @Modifying
    @Transactional
    @Query("delete from Cyclechecktaskitemtemplate where templateId=:templateId")
    void delItemListById(@Param("templateId") String templateId);

    @Query("from Cyclechecktaskitemtemplate where templateId=:templateId and item=:item and standard=:standard")
    Cyclechecktaskitemtemplate getItemByTemplateIdAndItemAndStandard(@Param("templateId") String templateId, @Param("item") String item, @Param("standard") String standard);
}
