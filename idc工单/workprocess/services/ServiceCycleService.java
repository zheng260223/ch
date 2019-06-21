package com.workprocess.services;

import com.workprocess.entity.Servicecycle;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ServiceCycleService {
    /**
     * 根据周期类型分页查询周期
     *
     * @param cycleType  周期类型
     * @param pageSize   获取条数
     * @param pageNumber 当前页数
     * @return
     */
    Page<Servicecycle> findByCycleType(Integer cycleType, Integer pageSize, Integer pageNumber);

    /**
     * 获取周期详情
     *
     * @param id 周期id
     * @return
     */
    Servicecycle findOne(String id);

    /**
     * 启用/禁用周期
     *
     * @param ids 周期id数组
     * @return
     */
    Boolean updateEnableByIds(String[] ids);

    /**
     * 根据周期类型查询周期
     *
     * @param cycleType 周期类型
     * @return
     */
    List<Servicecycle> findByCycleType(Integer cycleType);

    /**
     * 新增/修改周期
     *
     * @param id              周期id
     * @param name            周期名称
     * @param cycleType       周期类型
     * @param startWeek       周计划开始日期
     * @param startMonths     其他计划开始月份
     * @param startMonthsDays 其他计划开始日期
     * @param startHour       计划触发时间
     * @param enabled         是否启用
     * @param description     描述
     * @return
     */
    Boolean save(String id, String name, Integer cycleType, String startWeek, String startMonths, String startMonthsDays, Integer startHour, Boolean enabled, String description);

    /**
     * 根据周期类型值查询周期名称
     *
     * @param value 周期类型值
     * @return String
     */
    String getCycleTypeName(int value);

    /**
     * 查询所有周期类型
     *
     * @return List;
     */
    List getCycleTypeList();
}
