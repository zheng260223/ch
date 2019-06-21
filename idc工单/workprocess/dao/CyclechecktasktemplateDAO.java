package com.workprocess.dao;

import com.workprocess.entity.Cyclechecktasktemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CyclechecktasktemplateDAO extends JpaRepository<Cyclechecktasktemplate, String>, JpaSpecificationExecutor<Cyclechecktasktemplate> {

    @Modifying
    @Transactional
    @Query("update Cyclechecktasktemplate set enabled=:enabled where id in (:ids)")
    void upStatus(@Param("ids") List<String> ids, @Param("enabled") Long enabled);

    @Query("from Cyclechecktasktemplate where serviceCycleId=:serviceCycleId")
    List<Cyclechecktasktemplate> getTemplateForServiceCycle(@Param("serviceCycleId") String serviceCycleId);

    @Query("from Cyclechecktasktemplate where deviceType=:deviceType")
    List<Cyclechecktasktemplate> getTemplateForDevice(@Param("deviceType") Long deviceType);

    List<Cyclechecktasktemplate> findByEnabledAndDeviceType(Long enabled, Long deviceType);
}
