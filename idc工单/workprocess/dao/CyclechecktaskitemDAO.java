package com.workprocess.dao;

import com.workprocess.entity.Cyclechecktaskitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CyclechecktaskitemDAO extends JpaRepository<Cyclechecktaskitem, String>, JpaSpecificationExecutor<Cyclechecktaskitem> {


    List<Cyclechecktaskitem> findByWorkTaskId(String workTaskId);
}
