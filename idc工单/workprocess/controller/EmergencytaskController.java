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
import com.workprocess.entity.Emergencytaskitemtemplate;
import com.workprocess.entity.Emergencytasktemplate;
import com.workprocess.services.CyclechecktasktemplateService;
import com.workprocess.services.EmergencytaskitemtemplateService;
import com.workprocess.services.EmergencytasktemplateService;
import com.workprocess.utils.RtJson;
import com.workprocess.utils.Utiliy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/workprocess/emergencytask")
public class EmergencytaskController {
    private Gson gson = new Gson();
    private static final String VIEW = "workprocess/emergencytask/index";
    private static final String UPTEMPLATE = "workprocess/emergencytask/upTemplate";
    @Autowired
    private EmergencytasktemplateService emergencytasktemplateService;
    @Autowired
    private EmergencytaskitemtemplateService emergencytaskitemtemplateService;
    @Autowired
    private CyclechecktasktemplateService cyclechecktasktemplateService;

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
        sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了查看应急计划操作");
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
        String emergencyId = request.getParameter("emergencyId");
        map.put("emergencyId", emergencyId);
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
            Page<Emergencytasktemplate> page = emergencytasktemplateService.getTemplateList(pageSize, pageNum - 1);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Emergencytasktemplate emergencytasktemplate : page.getContent()) {
                Map<String, Object> map = Utiliy.objectToMap(emergencytasktemplate);
                map.put("deviceTypeName", cyclechecktasktemplateService.getDeviceName(Integer.parseInt(String.valueOf(emergencytasktemplate.getDeviceType()))));
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
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();

        try {
            Emergencytasktemplate emergencytasktemplate = new Emergencytasktemplate();
            if (map.get("id") == null || "".equals(map.get("id"))) {
                map.put("id", Identities.uuid());
            }
            emergencytasktemplate.setId(String.valueOf(map.get("id")));
            emergencytasktemplate.setName(String.valueOf(map.get("name")));
            emergencytasktemplate.setDeviceType(Long.parseLong(String.valueOf(map.get("deviceType"))));
            emergencytasktemplate.setEnabled(Long.parseLong(String.valueOf(map.get("enabled"))));
            emergencytasktemplate.setDescription(String.valueOf(map.get("description")));
            emergencytasktemplateService.setTemplate(emergencytasktemplate);

            //清空已有项目
            emergencytaskitemtemplateService.delItemListById(emergencytasktemplate.getId());

            List<Map<String, Object>> cycleInspectionProject = gson.fromJson(String.valueOf(map.get("emergencyInspectionProject")), new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            if (cycleInspectionProject != null && cycleInspectionProject.size() > 0) {
                for (Map<String, Object> cycleInspectionProjectMap : cycleInspectionProject) {
                    Emergencytaskitemtemplate emergencytaskitemtemplate = new Emergencytaskitemtemplate();
                    emergencytaskitemtemplate.setItem(String.valueOf(cycleInspectionProjectMap.get("item")));
                    emergencytaskitemtemplate.setStandard(String.valueOf(cycleInspectionProjectMap.get("standard")));
                    emergencytaskitemtemplate.setEnabled(1);
                    emergencytaskitemtemplate.setTemplateId(emergencytasktemplate.getId());
                    emergencytaskitemtemplateService.setIemtemplateList(emergencytaskitemtemplate);
                }
            }
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("更新数据成功");
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了添加或更新计划与对应的项目成功操作");
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("更新数据失败");
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了添加或更新计划与对应的项目失败操作");
        }
        return gson.toJson(rtJson);
    }

    /**
     * 根据计划ID获取计划与相关项目
     * url:/emergencytask/getTemplate
     *
     * @param id 计划ID
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "getTemplate", method = {RequestMethod.GET})
    public
    @ResponseBody
    String getTemplate(String id) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            Emergencytasktemplate emergencytasktemplate = emergencytasktemplateService.getTemplate(id);
            List list = emergencytaskitemtemplateService.getItemListForTemplate(emergencytasktemplate.getId());
            Map<String, Object> map = Utiliy.objectToMap(emergencytasktemplate);
            map.put("itemList", list);
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
     * 更新计划状态
     * url:/emergencytask/upStatus
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
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        try {
            emergencytasktemplateService.upStatus(ids, enabled);
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("更新状态成功");
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了更新状态计划成功操作");
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("更新状态失败");
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了更新状态计划失败操作");
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
    @RequestMapping(value = "getItemTemplateListForDevice", method = {RequestMethod.GET})
    public
    @ResponseBody
    String getItemTemplateListForDevice(Long deviceType) {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("查询成功");
            rtJson.setRtData(emergencytaskitemtemplateService.getItemtemplateListForDevice(deviceType));
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("查询失败");
        }
        return gson.toJson(rtJson);
    }
}
