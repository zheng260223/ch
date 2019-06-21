package com.workprocess.controller;

import com.google.gson.Gson;
import com.greenpanit.log.LogLevel;
import com.greenpanit.qtidc.entity.state.SysLogInfostate;
import com.greenpanit.qtidc.service.state.SysLogInfostateServiceUtilities;
import com.greenpanit.shiro.ShiroUser;
import com.greenpanit.utils.DateUtilities;
import com.greenpanit.utils.SecurityUtils;
import com.workprocess.services.ServiceCycleService;
import com.workprocess.utils.RtJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/workprocess/cycle")
public class ServiceCycleController {
    private Gson gson = new Gson();
    @Autowired
    ServiceCycleService serviceCycleService;

    private static final String VIEW = "workprocess/cycle/index";
    private static final String updateCycle = "workprocess/cycle/updateCycle";


    @RequestMapping(value = "/view", method = {RequestMethod.GET, RequestMethod.POST})
    public String view(ServletRequest request, Map<String, Object> map) {
        String pageNumber = request.getParameter("pageNumber");
        String cycleType = request.getParameter("cycleType");
        if (null == pageNumber || "".equals(pageNumber)) {
            pageNumber = "0";
        }
        map.put("cycleType", cycleType);
        map.put("pageNumber", pageNumber);
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了查看周期类型操作");
        SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        return VIEW;
    }

    @RequestMapping(value = "/updateCycle", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateCycle(ServletRequest request, Map<String, Object> map) {
        String cycleId = request.getParameter("cycleId");
        map.put("cycleId", cycleId);
        return updateCycle;
    }


    /**
     * 根据周期类型获取周期表单
     *
     * @param cycleType  周期类型(1：日，2：周，3：月，4：季，5：年)
     * @param pageSize   获取条数
     * @param pageNumber 当前页数（默认从0开始）
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public RtJson findByCycleType(Integer cycleType, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "0") Integer pageNumber) {
        try {
            return new RtJson<>(0, "查询数据成功", serviceCycleService.findByCycleType(cycleType, pageSize, pageNumber));
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    /**
     * 根据周期类型获取周期
     *
     * @param cycleType 周期类型(1：日，2：周，3：月，4：季，5：年)
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public RtJson findByCycleType(Integer cycleType) {
        try {
            return new RtJson<>(0, "查询数据成功", serviceCycleService.findByCycleType(cycleType));
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    /**
     * 根据id获取周期详情
     *
     * @param id 周期id
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RtJson findOne(@PathVariable String id) {
        try {
            return new RtJson<>(0, "查询数据成功", serviceCycleService.findOne(id));
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    /**
     * 新增/修改周期
     *
     * @param id              周期id（新增则不填入）
     * @param name            周期名称
     * @param cycleType       周期类型
     * @param startWeek       周计划开始日期
     * @param startMonths     其他计划开始月份
     * @param startMonthsDays 其他计划开始日期
     * @param startHour       计划触发时间
     * @param enabled         是否启用
     * @param description     描述
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public RtJson save(String id, String name, Integer cycleType, String startWeek, String startMonths, String
            startMonthsDays, Integer startHour, Boolean enabled, String description) {
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        try {
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了新增/修改周期成功操作");
            return new RtJson<>(0, "操作成功", serviceCycleService.save(id, name, cycleType, startWeek, startMonths, startMonthsDays, startHour, enabled, description));
        } catch (Exception e) {
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了新增/修改周期失败操作");
            return new RtJson<>(1, "操作失败", e.getMessage());
        }
    }

    /**
     * 启用/禁用周期
     *
     * @param ids 周期id数组
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/enabled", method = RequestMethod.POST)
    @ResponseBody
    public RtJson updateEnableByIds(String[] ids) {
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        try {
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了启用/禁用周期成功操作");
            return new RtJson<>(0, "修改成功", serviceCycleService.updateEnableByIds(ids));
        } catch (Exception e) {
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了启用/禁用周期失败操作");
            return new RtJson<>(1, "修改失败", e.getMessage());
        }
    }

    /**
     * 获取所有周期类型
     * url:/cyclechecktask/cycleTypeList
     *
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/type", method = {RequestMethod.GET})
    public
    @ResponseBody
    String cycleTypeList() {
        RtJson<Object> rtJson = new RtJson<>();
        try {
            rtJson.setRtStatus(0);
            rtJson.setRtMsg("查询数据成功");
            rtJson.setRtData(serviceCycleService.getCycleTypeList());
        } catch (Exception e) {
            rtJson.setRtStatus(1);
            rtJson.setRtMsg("查询数据失败");
            return gson.toJson(rtJson);
        }
        return gson.toJson(rtJson);
    }
}
