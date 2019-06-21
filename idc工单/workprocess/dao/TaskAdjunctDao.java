package com.workprocess.dao;

import com.workprocess.entity.Taskadjunct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskAdjunctDao extends JpaRepository<Taskadjunct, String>, JpaSpecificationExecutor<Taskadjunct> {

    @Query("from Taskadjunct where workTaskId=:workTaskId")
    List<Taskadjunct> findByWorkTaskId(@Param("workTaskId") String workTaskId);

    @Query("from Taskadjunct where workTaskId=:workTaskId and id not in (:fileIds)")
    List<Taskadjunct> findByWorkTaskIdAndNotInId(@Param("workTaskId") String workTaskId,@Param("fileIds") List<String> fileIds);
}
