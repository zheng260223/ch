package com.workprocess.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenpanit.log.LogLevel;
import com.greenpanit.qtidc.entity.state.SysLogInfostate;
import com.greenpanit.qtidc.service.state.SysLogInfostateServiceUtilities;
import com.greenpanit.shiro.ShiroUser;
import com.greenpanit.utils.DateUtilities;
import com.greenpanit.utils.Identities;
import com.greenpanit.utils.SecurityUtils;
import com.workprocess.entity.Cyclechecktaskitemtemplate;
import com.workprocess.entity.Cyclechecktasktemplate;
import com.workprocess.entity.Servicecycle;
import com.workprocess.services.CyclechecktaskitemtemplateService;
import com.workprocess.services.CyclechecktasktemplateService;
import com.workprocess.services.ServiceCycleService;
import com.workprocess.utils.RtJson;
import com.workprocess.utils.Utiliy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workprocess/cyclechecktask")
public class CyclechecktaskController {
    private Gson gson = new Gson();
    private static final String VIEW = "workprocess/cyclechecktask/index";
    private static final String UPTEMPLATE = "workprocess/cyclechecktask/upTemplate";

    @Autowired
    private CyclechecktasktemplateService cyclechecktasktemplateService;

    @Autowired
    private CyclechecktaskitemtemplateService cyclechecktaskitemtemplateService;
    @Autowired
    private ServiceCycleService serviceCycleService;

    /**
     * 跳转计划列表主页面
     *
     * @param request request
     * @param map     传递的参数
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     */
    @RequestMapping(value = "/view", method = {RequestMethod.GET, RequestMethod.POST})
    public String view(HttpServletRequest request, Map<String, Object> map) {
        String pageNum = request.getParameter("pageNum");
        if (pageNum == null || "".equals(pageNum)) {
            pageNum = "1";
        }
        map.put("pageNum", pageNum);
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了查看巡检计划操作");
        SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        return VIEW;
    }

    /**
     * 跳转新增或更新页面
     *
     * @param request request
     * @param map     传递的参数
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     */
    @RequestMapping(value = "/updateForTemplate", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateForTemplate(HttpServletRequest request, Map<String, Object> map) {
        String cycleId = request.getParameter("cycleId");
        map.put("cycleId", cycleId);
        return UPTEMPLATE;
    }

    /**
     * 获取计划列表
     * url:/cyclechecktask/templateList
     *
     * @param pageSize 每页显示条数
     * @param pageNum  起始查询页数,从0开始
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/templateList", method = {RequestMethod.GET})
    public
    @ResponseBody
    String templateList(Integer pageSize, Integer pageNum) {
        pageNum = pageNum < 1 ? 1 : pageNum;
        RtJson<Object> rtJson = new RtJson<>();
        try {
            Map<String, Object> templateListOfMap = new HashMap<>();
            Page<Cyclechecktasktemplate> page = cyclechecktasktemplateService.getTemplateList(pageSize, pageNum - 1);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Cyclechecktasktemplate cyclechecktasktemplate : page.getContent()) {
                Map<String, Object> map = Utiliy.objectToMap(cyclechecktasktemplate);
                map.put("deviceTypeName", cyclechecktasktemplateService.getDeviceName(Integer.parseInt(String.valueOf(cyclechecktasktemplate.getDeviceType()))));
                Servicecycle servicecycle = serviceCycleService.findOne(cyclechecktasktemplate.getServiceCycleId());
                map.put("serviceCycleName", servicecycle.getName());
                list.add(map);
            }
            templateListOfMap.put("content", list);
            templateListOfMap.put("pageSize", page.getSize());
            templateListOfMap.put("numberOfElements", page.getNumberOfElements());
            templateListOfMap.put("totalPages", page.getTotalPages());
            templateListOfMap.put("number", page.getNumber() + 1);

            rtJson.setRtStatus(0);
            rtJson.setRtMsg("查询数据成功");
            rtJson.setRtData(templateListOfMap);
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("查询数据失败");
            return gson.toJson(rtJson);
        }
        return gson.toJson(rtJson);
    }

    /**
     * 同步添加或更新计划与对应的项目
     *
     * @param map id:计划ID(为空时,此接口添加计划与对应的项目.不为空时,此接口更新计划与对应的项目)
     *            name:计划名称
     *            deviceType:设备类型
     *            serviceCycleId:周期类型
     *            enabled:计划状态
     *            description:计划描述
     *            cycleInspectionProject: item:项目内容
     *            standard:项目标准
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     */
    @RequestMapping(value = "/setTemplateAndItem", method = {RequestMethod.POST})
    public
    @ResponseBody
    String setTemplateAndItem(@RequestParam Map<String, Object> map) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            Cyclechecktasktemplate cyclechecktasktemplate = new Cyclechecktasktemplate();
            if (map.get("id") == null || "".equals(map.get("id"))) {
                map.put("id", Identities.uuid());
            }
            cyclechecktasktemplate.setId(String.valueOf(map.get("id")));
            cyclechecktasktemplate.setName(String.valueOf(map.get("name")));
            cyclechecktasktemplate.setDeviceType(Long.parseLong(String.valueOf(map.get("deviceType"))));
            cyclechecktasktemplate.setServiceCycleId(String.valueOf(map.get("serviceCycleId")));
            cyclechecktasktemplate.setEnabled(Long.parseLong(String.valueOf(map.get("enabled"))));
            cyclechecktasktemplate.setDescription(String.valueOf(map.get("description")));
            cyclechecktasktemplateService.setTemplate(cyclechecktasktemplate);

            //清空已有项目
            cyclechecktaskitemtemplateService.delItemListById(cyclechecktasktemplate.getId());
            List<Map<String, Object>> cycleInspectionProject = gson.fromJson(String.valueOf(map.get("cycleInspectionProject")), new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            if (cycleInspectionProject != null && cycleInspectionProject.size() > 0) {
                for (Map<String, Object> cycleInspectionProjectMap : cycleInspectionProject) {
                    Cyclechecktaskitemtemplate cyclechecktaskitemtemplate = new Cyclechecktaskitemtemplate();
                    cyclechecktaskitemtemplate.setItem(String.valueOf(cycleInspectionProjectMap.get("item")));
                    cyclechecktaskitemtemplate.setStandard(String.valueOf(cycleInspectionProjectMap.get("standard")));
                    cyclechecktaskitemtemplate.setEnabled(1);
                    cyclechecktaskitemtemplate.setTemplateId(cyclechecktasktemplate.getId());
                    cyclechecktaskitemtemplateService.setIemtemplateList(cyclechecktaskitemtemplate);
                }
            }
            rtJson.setRtStatus(0);
            //记录日志
            ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
            String currentDateTime = DateUtilities.getCurrentDateTime();
            String logLevelKey = LogLevel.INFO.getValue();
            String logLevelName = LogLevel.INFO.getName();
            SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
            String username = shiroUser.getUsername();
            String loginAccount = shiroUser.getLoginAccount();
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了添加或更新计划与对应的项目操作");
            SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
            rtJson.setRtMsg("更新数据成功");
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("更新数据失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 添加或更新计划
     * url:/cyclechecktask/setTemplate
     *
     * @param cyclechecktasktemplate id:计划ID(为空时,此接口添加计划.不为空时,此接口更新计划)
     *                               name:计划名称
     *                               deviceType:设备类型
     *                               serviceCycleId:周期类型
     *                               enabled:计划状态
     *                               description:计划描述
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     */
    @RequestMapping(value = "/setTemplate", method = {RequestMethod.POST})
    public
    @ResponseBody
    String setTemplate(Cyclechecktasktemplate cyclechecktasktemplate) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            cyclechecktasktemplateService.setTemplate(cyclechecktasktemplate);
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("更新数据成功");
            //记录日志
            ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
            String currentDateTime = DateUtilities.getCurrentDateTime();
            String logLevelKey = LogLevel.INFO.getValue();
            String logLevelName = LogLevel.INFO.getName();
            SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
            String username = shiroUser.getUsername();
            String loginAccount = shiroUser.getLoginAccount();
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了添加或更新计划操作");
            SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("更新数据失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 添加或更新项目
     * url:/cyclechecktask/setItemTemplateList
     *
     * @param cyclechecktaskitemtemplate id:项目ID(为空时,此接口添加计划.不为空时,此接口更新计划)
     *                                   serialnumber:项目串行数
     *                                   item:项目内容
     *                                   standard:项目标准
     *                                   enabled:项目状态
     *                                   templateId:计划ID
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     */
    @RequestMapping(value = "/setItemTemplateList", method = {RequestMethod.POST})
    public
    @ResponseBody
    String setItemTemplateList(Cyclechecktaskitemtemplate cyclechecktaskitemtemplate) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            cyclechecktaskitemtemplateService.setIemtemplateList(cyclechecktaskitemtemplate);
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("更新数据成功");
            //记录日志
            ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
            String currentDateTime = DateUtilities.getCurrentDateTime();
            String logLevelKey = LogLevel.INFO.getValue();
            String logLevelName = LogLevel.INFO.getName();
            SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
            String username = shiroUser.getUsername();
            String loginAccount = shiroUser.getLoginAccount();
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了添加或更新项目操作");
            SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("更新数据失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 更新计划状态
     * url:/cyclechecktask/upStatus
     *
     * @param ids     计划ID(以","分隔)
     * @param enabled 计划状态
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     */
    @RequestMapping(value = "/upStatus", method = {RequestMethod.POST})
    public
    @ResponseBody
    String upStatus(String ids, Long enabled) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            cyclechecktasktemplateService.upStatus(ids, enabled);
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("更新计划状态成功");
            //记录日志
            ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
            String currentDateTime = DateUtilities.getCurrentDateTime();
            String logLevelKey = LogLevel.INFO.getValue();
            String logLevelName = LogLevel.INFO.getName();
            SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
            String username = shiroUser.getUsername();
            String loginAccount = shiroUser.getLoginAccount();
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了更新计划状态成功操作");
            SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("更新计划状态失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 更新项目状态
     * url:/cyclechecktask/upItemStatus
     *
     * @param ids     项目ID(以","分隔)
     * @param enabled 项目状态
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     */
    @RequestMapping(value = "/upItemStatus", method = {RequestMethod.POST})
    public
    @ResponseBody
    String upItemStatus(String ids, Long enabled) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            cyclechecktaskitemtemplateService.upStatus(ids, enabled);
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("更新项目状态成功");
            //记录日志
            ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
            String currentDateTime = DateUtilities.getCurrentDateTime();
            String logLevelKey = LogLevel.INFO.getValue();
            String logLevelName = LogLevel.INFO.getName();
            SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
            String username = shiroUser.getUsername();
            String loginAccount = shiroUser.getLoginAccount();
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了更新项目状态成功操作");
            SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("更新项目状态失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 根据计划ID获取计划与相关项目
     * url:/cyclechecktask/getTemplate
     *
     * @param id 计划ID
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/getTemplate", method = {RequestMethod.GET})
    public
    @ResponseBody
    String getTemplate(String id) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            Cyclechecktasktemplate cyclechecktasktemplate = cyclechecktasktemplateService.getTemplate(id);
            List list = cyclechecktaskitemtemplateService.getItemListForTemplate(cyclechecktasktemplate.getId());
            Map<String, Object> map = Utiliy.objectToMap(cyclechecktasktemplate);
            Servicecycle servicecycle = serviceCycleService.findOne(cyclechecktasktemplate.getServiceCycleId());
            map.put("itemList", list);
            map.put("cycleType", servicecycle.getCycleType());
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("查询数据成功");
            rtJson.setRtData(map);
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("查询数据失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 根据周期类型获取计划
     * url:/cyclechecktask/getTemplateForServiceCycle
     *
     * @param serviceCycleId 周期类型
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/getTemplateForServiceCycle", method = {RequestMethod.GET})
    public
    @ResponseBody
    String getTemplateForServiceCycle(String serviceCycleId) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("查询数据成功");
            rtJson.setRtData(cyclechecktasktemplateService.getTemplateForServiceCycle(serviceCycleId));
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("查询数据失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 根据设备ID获取项目清单(去重)
     * url:/cyclechecktask/getItemTemplateListForDevice
     *
     * @param deviceType 设备类型
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/getItemTemplateListForDevice", method = {RequestMethod.GET})
    public
    @ResponseBody
    String getItemTemplateListForDevice(Long deviceType) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("查询数据成功");
            rtJson.setRtData(cyclechecktaskitemtemplateService.getItemtemplateListForDevice(deviceType));
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("查询数据失败");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 获取所有设备类型
     * url:/cyclechecktask/deviceTypeList
     *
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/deviceTypeList", method = {RequestMethod.GET})
    public
    @ResponseBody
    String deviceTypeList() {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("查询数据成功");
            rtJson.setRtData(cyclechecktasktemplateService.getDeviceList());
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("查询数据失败");
            return gson.toJson(rtJson);
        }
        return gson.toJson(rtJson);
    }

    /**
     * 根据设备类型查询模板
     *
     * @param deviceType 设备类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deviceType", method = RequestMethod.GET)
    public RtJson findByDeviceType(Long deviceType) {
        try {
            return new RtJson<>(0, "查询成功", cyclechecktasktemplateService.findByDeviceType(deviceType));
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }
}

