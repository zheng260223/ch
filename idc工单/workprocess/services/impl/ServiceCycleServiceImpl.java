package com.workprocess.services.impl;

import com.greenpanit.utils.Identities;
import com.workprocess.dao.ServiceCycleDao;
import com.workprocess.entity.CycleType;
import com.workprocess.entity.Servicecycle;
import com.workprocess.services.ServiceCycleService;
import com.workprocess.utils.PageUtils;
import com.workprocess.utils.Utiliy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceCycleServiceImpl implements ServiceCycleService {
    @Resource
    ServiceCycleDao serviceCycleDao;

    /**
     * 分页查询周期
     *
     * @param cycleType  周期类型
     * @param pageSize   获取条数
     * @param pageNumber 当前页数
     * @return
     */
    @Override
    public Page<Servicecycle> findByCycleType(Integer cycleType, Integer pageSize, Integer pageNumber) {
        Pageable pageable = PageUtils.verify(pageSize, pageNumber, new Sort(Sort.Direction.ASC, "startHour"));
        return serviceCycleDao.findByCycleType(cycleType, pageable);
    }

    /**
     * 查询详情
     *
     * @param id 周期id
     * @return
     */
    @Override
    public Servicecycle findOne(String id) {
        return serviceCycleDao.findOne(id);
    }

    /**
     * 启用/禁用周期
     *
     * @param ids 周期id数组
     * @return
     */
    @Override
    public Boolean updateEnableByIds(String[] ids) {
        List<Servicecycle> servicecycleList = serviceCycleDao.findByIdIn(ids);
        for (Servicecycle servicecycle : servicecycleList) {
            if (servicecycle.getEnabled()) {
                servicecycle.setEnabled(false);
            } else {
                servicecycle.setEnabled(true);
            }
        }
        serviceCycleDao.save(servicecycleList);
        return true;
    }

    /**
     * 查询周期
     *
     * @param cycleType 周期类型
     * @return
     */
    @Override
    public List<Servicecycle> findByCycleType(Integer cycleType) {
        return serviceCycleDao.findByCycleType(cycleType);
    }

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
    @Override
    public Boolean save(String id, String name, Integer cycleType, String startWeek, String startMonths, String startMonthsDays, Integer startHour, Boolean enabled, String description) {
        Servicecycle servicecycle = new Servicecycle();
        if (!StringUtils.isEmpty(startWeek)) {
            startWeek = Utiliy.bubbleSort(startWeek);
        }
        if (!StringUtils.isEmpty(startMonths)) {
            startMonths = Utiliy.bubbleSort(startMonths);
        }
        if (!StringUtils.isEmpty(startMonthsDays)) {
            startMonthsDays = Utiliy.bubbleSort(startMonthsDays);
        }
        if (!StringUtils.isEmpty(id)) {
            servicecycle.setId(id);
        } else {
            servicecycle.setId(Identities.uuid());
        }
        servicecycle.setName(name);
        servicecycle.setCycleType(cycleType);
        servicecycle.setStartWeek(startWeek);
        servicecycle.setStartMonths(startMonths);
        servicecycle.setStartMonthsDays(startMonthsDays);
        servicecycle.setStartHour(startHour);
        servicecycle.setEnabled(enabled);
        servicecycle.setDescription(description);
        serviceCycleDao.save(servicecycle);
        return true;
    }

    /**
     * 根据周期类型值查询周期名称
     *
     * @param value 周期类型值
     * @return String
     */
    @Override
    public String getCycleTypeName(int value) {
        String name = "";
        for (CycleType cycleType : CycleType.values()) {
            if (cycleType.getValue() == value) {
                name = cycleType.getName();
                break;
            }
        }
        return name;
    }

    /**
     * 查询所有周期类型
     *
     * @return List;
     */
    @Override
    public List getCycleTypeList() {
        List list = new ArrayList();
        for (CycleType cycleType : CycleType.values()) {
            Map map = new HashMap();
            map.put("name", cycleType.getName());
            map.put("value", cycleType.getValue());
            list.add(map);
        }
        return list;
    }
}
