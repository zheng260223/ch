package com.workprocess.dao;

import com.workprocess.entity.Emergencytaskitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EmergencytaskitemDAO extends JpaRepository<Emergencytaskitem, String>, JpaSpecificationExecutor<Emergencytaskitem> {

    List<Emergencytaskitem> findByWorkTaskId(String workTaskId);

}
