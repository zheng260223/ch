package com.workprocess.dao;

import com.workprocess.entity.Servicecycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ServiceCycleDao extends JpaRepository<Servicecycle, String>, JpaSpecificationExecutor<Servicecycle> {

    Page<Servicecycle> findByCycleType(Integer cycleType, Pageable pageable);

    List<Servicecycle> findByCycleType(Integer cycleType);

    Servicecycle findOne(String id);

    List<Servicecycle> findByIdIn(String[] ids);

    @Query(value = "SELECT c.ID, c.DeviceType FROM servicecycle s JOIN cyclechecktasktemplate c WHERE" +
            " s.Enabled = 1  AND c.Enabled = 1  AND s.ID = c.ServiceCycleID AND (" +
            " (s.CycleType = 1) OR" +
            " ( s.CycleType = 2 AND FIND_IN_SET(:week, s.StartWeek)) " +
            " OR ( s.CycleType = 3 AND FIND_IN_SET(:days, s.StartMonthsDays)) " +
            " OR (( s.CycleType = 4 OR s.CycleType = 5) AND FIND_IN_SET(:months, s.StartMonths) AND FIND_IN_SET(:days, s.StartMonthsDays)) " +
            ") AND s.StartHour = :hours", nativeQuery = true)
    List<Object[]> getCycle(@Param("week") String week, @Param("months") String month, @Param("days") String day, @Param("hours") String hour);

}
