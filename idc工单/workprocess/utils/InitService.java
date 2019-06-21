package com.workprocess.utils;

import com.greenpanit.utils.Identities;
import com.workprocess.dao.*;
import com.workprocess.entity.Cyclechecktaskitem;
import com.workprocess.entity.Cyclechecktaskitemtemplate;
import com.workprocess.entity.Device;
import com.workprocess.entity.Worktask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class InitService {
    static String[] weekDay = {"7", "1", "2", "3", "4", "5", "6"};
    static String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    static String[] templateType = {"周期巡检", "维护保养", "应急处理"};

    @Autowired
    private WorkTaskDao workTaskDao;
    @Autowired
    ServiceCycleDao serviceCycleDao;
    @Autowired
    CyclechecktaskitemDAO cyclechecktaskitemDAO;
    @Autowired
    CyclechecktasktemplateDAO cyclechecktasktemplateDAO;
    @Autowired
    CycleCheckTaskItemTemplateDAO cycleCheckTaskItemTemplateDAO;
    @Autowired
    DeviceDAO deviceDAO;

    /**
     * 定时生成工单
     * cron = "0 0 0-6 * * ?" 每天0点到6点之间，每小时执行一次
     * cron = "0/10 * * * * ?" 每间隔10秒执行,测试使用
     */
    @Scheduled(cron = "0 0 0-6 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    public void init() {
        Map<String, String> time = getNowTime();
        getTemplate(time.get("week"), time.get("month"), time.get("day"), time.get("hour"));
    }

    private Map<String, String> getNowTime() {
        Map map = new HashMap();
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int week = c.get(Calendar.DAY_OF_WEEK);
        map.put("month", months[month]);
        map.put("hour", String.valueOf(hour));
        map.put("day", String.valueOf(date));
        map.put("week", weekDay[week - 1]);
        return map;
    }

    private void getTemplate(String week, String month, String day, String hour) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] objs : serviceCycleDao.getCycle(week, month, day, hour)) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", objs[0]);
            map.put("deviceType", objs[1]);
            map.put("templateType", 1);
            list.add(map);
        }
        getDeviceId(list);
    }


    private void saveWorkTask(String workTaskId, String templateId, String deviceId, Integer deviceType, String deviceName, Integer templateType) {
        Timestamp scurrtest = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Worktask worktask = new Worktask();
        worktask.setId(workTaskId);
        worktask.setSerialnumber(workTaskDao.getMaxSerialNumber() + 1);
        worktask.setOriginType(1);
        worktask.setPriority(0);
        worktask.setStatus(1);
        worktask.setTemplateId(templateId);
        worktask.setTemplateType(templateType);
        worktask.setDeviceId(deviceId);
        worktask.setDeviceType(deviceType);
        worktask.setDeleted(false);
        worktask.setCreateTime(scurrtest);
        worktask.setTaskName(this.templateType[templateType - 1] + "-" + deviceName + "-" + df.format(new Date()));
        workTaskDao.save(worktask);
    }

    private void getDeviceId(List<Map<String, Object>> templates) {
        if (null != templates && templates.size() > 0) {
            for (Map map : templates) {
                String templateId = map.get("id").toString();
                Integer templateType = Integer.valueOf(map.get("templateType").toString());
                List list = new ArrayList();
                if (1 == templateType) {
                    list = cycleCheckTaskItemTemplateDAO.findByTemplateIdAndEnabled(templateId, 1L);
                }
                String deviceType = map.get("deviceType").toString();
                List<Device> devices = deviceDAO.findByDeviceType(Integer.valueOf(deviceType));
                for (Device device : devices) {
                    String workTaskId = Identities.uuid();
                    String deviceId = device.getId();
                    saveWorkTask(workTaskId, templateId, deviceId, Integer.valueOf(deviceType), device.getName(), templateType);
                    saveCycleCheckTaskItem(workTaskId, list, templateType);
                }
            }
        }
    }

    private void saveCycleCheckTaskItem(String workTaskId, List<Cyclechecktaskitemtemplate> cyclechecktaskitemtemplateList, Integer templateType) {
        if (null != cyclechecktaskitemtemplateList && cyclechecktaskitemtemplateList.size() > 0) {
            if (1 == templateType) {
                List<Cyclechecktaskitem> list = new ArrayList<>();
                for (Cyclechecktaskitemtemplate cyclechecktaskitemtemplate : cyclechecktaskitemtemplateList) {
                    String id = Identities.uuid();
                    Cyclechecktaskitem cyclechecktaskitem = new Cyclechecktaskitem();
                    cyclechecktaskitem.setId(id);
                    cyclechecktaskitem.setTemplateItemId(cyclechecktaskitemtemplate.getId());
                    cyclechecktaskitem.setTemplateId(cyclechecktaskitemtemplate.getTemplateId());
                    cyclechecktaskitem.setItem(cyclechecktaskitemtemplate.getItem());
                    cyclechecktaskitem.setSerialnumber(cyclechecktaskitemtemplate.getSerialnumber());
                    cyclechecktaskitem.setWorkTaskId(workTaskId);
                    cyclechecktaskitem.setIsNormal(2L);
                    cyclechecktaskitem.setStandard(cyclechecktaskitemtemplate.getStandard());
                    list.add(cyclechecktaskitem);
                }
                cyclechecktaskitemDAO.save(list);
            }
        }
    }

}
