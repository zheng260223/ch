package com.workprocess.dao;

import com.workprocess.entity.Emergencytasktemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmergencytasktemplateDAO extends JpaRepository<Emergencytasktemplate, String>, JpaSpecificationExecutor<Emergencytasktemplate> {
    @Modifying
    @Transactional
    @Query("update Emergencytasktemplate set enabled=:enabled where id in (:ids)")
    void upStatus(@Param("ids") List<String> ids, @Param("enabled") Long enabled);

    List<Emergencytasktemplate> findByEnabledAndDeviceType(Long enabled, Long devieType);

}
