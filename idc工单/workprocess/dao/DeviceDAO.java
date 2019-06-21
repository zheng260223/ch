package com.workprocess.dao;

import com.workprocess.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 获取设备综合数据 不要使用（findOne方法会出问题）
 */
public interface DeviceDAO extends JpaRepository<Device, String>, JpaSpecificationExecutor<Device> {

    List<Device> findByDeviceType(Integer deviceType);

    Device findByDeviceTypeAndId(Integer deviceType, String id);
}
