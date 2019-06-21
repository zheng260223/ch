package com.workprocess.dao;

import com.workprocess.entity.Worktask;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkTaskDao extends JpaRepository<Worktask, String>, JpaSpecificationExecutor<Worktask> {

    Page<Worktask> findByDeletedFalseAndTemplateTypeAndStatus(Integer templateType, Integer status, Pageable pageable);

    @Query("select w from Worktask w where w.templateType like concat('%',?1,'%') and w.status like concat('%',?2,'%') and w.taskName like concat('%',?3,'%') and w.deviceType like concat('%',?4,'%') and w.priority like concat('%',?5,'%') order by w.createTime desc")
    Page<Worktask> getWorkTaskList(String templateType, String status,String taskName,String deviceType,String priority,Pageable pageable);

    Page<Worktask> findByDeletedFalseAndTemplateTypeAndStatusAndTaskNameLikeAndDeviceTypeLikeAndPriorityLikeOrderByCreateTimeDesc
            (Integer templateType, Integer status,String taskName,Integer deviceType,Integer priority,Pageable pageable);

    Page<Worktask> findByDeletedFalseAndTemplateTypeAndStatusAndDeviceType(Integer templateType, Integer status, Integer deviceType, Pageable pageable);


    List<Worktask> findByDeletedFalseAndIdIn(String[] ids);

    Worktask findByFlowId(String flowId);

    @Query("from Worktask where flowId=:flowId")
    Worktask getWorktaskByFlowId(@Param("flowId") String flowId);

    List<Worktask> findByFlowIdIn(List<String> flowIds);

    Page<Worktask> findByDeletedFalseAndDeviceTypeAndDeviceIdOrderByCreateTimeDesc(Integer deviceType, String deviceId, Pageable pageable);

    @Query(value = "select max(w.serialnumber) from worktask w", nativeQuery = true)
    Integer getMaxSerialNumber();
}
