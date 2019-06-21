package com.workprocess.controller;

import com.greenpanit.log.LogLevel;
import com.greenpanit.qtidc.entity.base.*;
import com.greenpanit.qtidc.entity.base.Dictionary;
import com.greenpanit.qtidc.entity.state.SysLogInfostate;
import com.greenpanit.qtidc.service.base.DeviceService;
import com.greenpanit.qtidc.service.state.SysLogInfostateServiceUtilities;
import com.greenpanit.shiro.ShiroUser;
import com.greenpanit.utils.DateUtilities;
import com.greenpanit.utils.Identities;
import com.greenpanit.utils.SecurityUtils;
import com.greenpanit.utils.ServletUtils;
import com.workprocess.entity.Taskadjunct;
import com.workprocess.entity.Worktask;
import com.workprocess.services.*;
import com.workprocess.utils.RtJson;

import com.greenpanit.qtidc.service.base.*;
import com.greenpanit.qtidc.service.base.DictionaryService;
import com.greenpanit.qtidc.service.base.BuildingService;
import com.greenpanit.qtidc.entity.base.User;
import com.greenpanit.qtidc.service.base.UserService;

import com.workprocess.utils.Utiliy;
import com.workprocess.utils.fileconverter.entity.MbdcEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/workprocess/work")
public class WorkTaskController {
    @Autowired
    WorkTaskService workTaskService;
    @Autowired
    CyclechecktasktemplateService cyclechecktasktemplateService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    EmergencytasktemplateService emergencytasktemplateService;
    @Autowired
    TaskadjunctService taskadjunctService;


    @Autowired
    private GeospatialAreaService geospatialAreaService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private BuildingService buildingService;
    @Autowired
    private UserService userService;

    private static final String VIEW = "workprocess/worktask/index";
    private static final String GET_VIEW = "workprocess/worktask/getIndex";
    private static final String GET_WORKTASK_List = "workprocess/worktask/getWorkTaskList";
    private static final String UPDATAWORK = "workprocess/worktask/updataWorkTask";
    private static final String GETWORK = "workprocess/worktask/getWorkTask";
    private static final String tree = "workprocess/worktask/tree";
    //    private static final String dispatch = "workprocess/worktask/dispatchWorkTask";
    private static final String MOBILE_WORKTASKINFO_INFO = "mobile/workTaskInfo/info";
    private static final String MOBILE_WORKTASKINFO_SPOT = "mobile/workTaskInfo/spot";
    private static final String MBDC_READ = "workprocess/worktask/mbdcRead";
    private static final String MOBILE_MBDC_READ = "mobile/readFile/index";
    private static final String PDF_INDEX = "pdf/viewer";
    private static final String MOBILE_PDF_INDEX = "pdf/mobileViewer";


    @RequestMapping(value = "/view", method = {RequestMethod.GET, RequestMethod.POST})
    public String view(ServletRequest request, Map<String, Object> map) {
        String pageNumber = request.getParameter("pageNumber");
        String templateType = request.getParameter("templateType");
        String status = request.getParameter("status");
        if (null == pageNumber || "".equals(pageNumber)) {
            pageNumber = "0";
        }
        map.put("status", status);
        map.put("templateType", templateType);
        map.put("pageNumber", pageNumber);
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了查看工单管控操作");
        SysLogInfostateServiceUtilities.insertSysLogInfo(sysLogInfostate);//保存信息到ES
        return VIEW;
    }

    @RequestMapping(value = "/getView", method = {RequestMethod.GET, RequestMethod.POST})
    public String getView(ServletRequest request, Map<String, Object> map) {
        String pageNumber = request.getParameter("pageNumber");
        String templateType = request.getParameter("templateType");
        String status = request.getParameter("status");
        String taskName = request.getParameter("taskName");
        String deviceType = request.getParameter("deviceType");
        String priority = request.getParameter("priority");
        if (null == pageNumber || "".equals(pageNumber)) {
            pageNumber = "0";
        }
        map.put("status", status);
        map.put("taskName", taskName);
        map.put("deviceType", deviceType);
        map.put("priority", priority);
        map.put("templateType", templateType);
        map.put("pageNumber", pageNumber);
        return GET_VIEW;
    }

    @RequestMapping(value = "/getWorkTaskByDevice", method = {RequestMethod.GET, RequestMethod.POST})
    public String getWorkTaskByDevice(ServletRequest request, Map<String, Object> map) {
        String pageNumber = request.getParameter("pageNumber");
        String deviceType = request.getParameter("deviceType");
        String deviceId = request.getParameter("deviceId");
        if (null == pageNumber || "".equals(pageNumber)) {
            pageNumber = "0";
        }
        map.put("deviceType", deviceType);
        map.put("deviceId", deviceId);
        map.put("pageNumber", pageNumber);
        return GET_WORKTASK_List;
    }

//    @RequestMapping(value = "/dispatch", method = {RequestMethod.GET, RequestMethod.POST})
//    public String dispatch(ServletRequest request, Map<String, Object> map) {
//        String workId = request.getParameter("workId");
//        String returnworkId = "";
//        byte[] bytes = null;
//        try {
//            bytes = workId.getBytes("iso-8859-1");
//            returnworkId = new String(bytes, "utf-8");
//        } catch (Exception e) {
//
//        }
//        map.put("workId", returnworkId);
//        return dispatch;
//    }

    @RequestMapping(value = "/tree", method = {RequestMethod.GET, RequestMethod.POST})
    public String tree(ServletRequest request, Map<String, Object> map) {
        map.put("parkZonesForPhase", getParkZonesAndPhase());
        return tree;
    }

    @RequestMapping(value = "/mobile/workTaskInfo/info", method = {RequestMethod.GET, RequestMethod.POST})
    public String info(HttpServletRequest request, Map<String, Object> map) {
        String id = request.getParameter("id");
        map.put("id", id);
        return MOBILE_WORKTASKINFO_INFO;
    }

    @RequestMapping(value = "/mobile/workTaskInfo/spot", method = {RequestMethod.GET, RequestMethod.POST})
    public String spot(HttpServletRequest request, Map<String, Object> map) {
        String id = request.getParameter("id");
        map.put("id", id);
        return MOBILE_WORKTASKINFO_SPOT;
    }

    @RequestMapping(value = "/mbdc/read", method = {RequestMethod.GET, RequestMethod.POST})
    public String mbdcRead(HttpServletRequest request, Map<String, Object> map) {
        String id = request.getParameter("id");
        map.put("id", id);
        if (ServletUtils.isMobile(request)) {
            return MOBILE_MBDC_READ;
        } else {
            return MBDC_READ;
        }
    }

    @RequestMapping(value = "/pdf/viewer", method = {RequestMethod.GET, RequestMethod.POST})
    public String readPdfFile(HttpServletRequest request, Map<String, Object> map) {
        String filePath = request.getParameter("filePath");
        map.put("filePath", filePath);
        if (ServletUtils.isMobile(request)) {
            return MOBILE_PDF_INDEX;
        } else {
            return PDF_INDEX;
        }
    }

    /**
     * 取得设施所属阶段
     *
     * @return
     */
    public String getParkZonesAndPhase() {
        List<Geospatialarea> oGeospatialareas = geospatialAreaService.findAll("root", GeospatialType.PARK.getValue());
        if (oGeospatialareas.size() <= 0) {
            return "";
        }
        String parkZones = "[";
        Geospatialarea parentArea = oGeospatialareas.get(0);
        parkZones += "{id:" + "\"" + parentArea.getId() + "\"" + ",name:" + "\"" + parentArea.getName() + "\"" + ",nodeType:" + "\"" + "10" + "\"" + "}";
        List<Geospatialarea> parkArea = geospatialAreaService.findAll(oGeospatialareas.get(0).getId(), GeospatialType.PARKAREA.getValue());
        for (Geospatialarea geospatialarea : parkArea) {
            parkZones += ",{id:" + "\"" + geospatialarea.getId() + "\"" + ",pId:" + "\"" + parentArea.getId() + "\"" + ",name:" + "\"" + geospatialarea.getName() + "\"" + ",nodeType:" + "\"" + "11" + "\"" + "}";
        }
        List<Building> buidings = buildingService.findAll(oGeospatialareas.get(0).getId());
        for (Building building : buidings) {
            parkZones += ",{id:" + "\"" + building.getId() + "\"" + ",pId:" + "\"" + parentArea.getId() + "\"" + ",name:" + "\"" + building.getName() + "\"" + ",nodeType:" + "\"" + "12" + "\"" + "}";
        }
        parkZones += "]";
        return parkZones;
    }


    @RequestMapping(value = "/updatawork", method = {RequestMethod.GET, RequestMethod.POST})
    public String updatawork(ServletRequest request, Map<String, Object> map) {
        String workId = request.getParameter("workId");
        map.put("workId", workId);
        return UPDATAWORK;
    }

    @RequestMapping(value = "/getwork", method = {RequestMethod.GET, RequestMethod.POST})
    public String getwork(ServletRequest request, Map<String, Object> map) {
        String workId = request.getParameter("workId");
        map.put("workId", workId);
        return GETWORK;
    }

    /**
     * 新增工单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public RtJson saveWork(HttpServletRequest request) {
        try {
            List<MultipartFile> files = new ArrayList<>();
            Map<String, Object> dataMap = new HashMap<>();
            CommonsMultipartResolver multpartResolver = new CommonsMultipartResolver();
            if (multpartResolver.isMultipart(request)) {
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                Enumeration em = multiRequest.getParameterNames();
                while (em.hasMoreElements()) {
                    String name = String.valueOf(em.nextElement());
                    dataMap.put(name, multiRequest.getParameter(name));
                }
                Iterator<String> iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    // 适配名字重复的文件
                    List<MultipartFile> fileRows = multiRequest.getFiles(iter.next());
                    if (fileRows != null && fileRows.size() != 0) {
                        for (MultipartFile file : fileRows) {
                            if (file != null && !file.isEmpty()) {
                                files.add(file);
                            }
                        }
                    }
                }
            }
            boolean temp = false;
            for (DeviceType deviceType : DeviceType.values()) {
                if (dataMap.get("deviceType").equals(String.valueOf(deviceType.getValue()))) {
                    temp = true;
                    break;
                }
            }
            if (!temp) {
                return new RtJson<>(1, "当前设备类型不符，请与管理员联系", "");
            }
            String fileIds = String.valueOf(dataMap.get("fileIds"));//已有附件的ID
            Worktask worktask = (Worktask) Utiliy.mapToObject(dataMap, Worktask.class);
            if (null == worktask.getId() || StringUtils.isEmpty(worktask.getId())) {
                worktask.setId(Identities.uuid());
                worktask.setSerialnumber(workTaskService.getMaxSerialNumber() + 1);
            }
            worktask.setCreateTime(new Timestamp(System.currentTimeMillis()));
            if (null == deviceService.getDevice(worktask.getDeviceId(), worktask.getDeviceType())) {
                return new RtJson<>(1, "没有找到该设备", "");
            }
            workTaskService.saveWork(worktask);
            taskadjunctService.uploadFile(worktask.getId(), files, fileIds);
            String workTaskId = worktask.getId();
            //记录日志
            ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
            String currentDateTime = DateUtilities.getCurrentDateTime();
            String logLevelKey = LogLevel.INFO.getValue();
            String logLevelName = LogLevel.INFO.getName();
            SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
            String username = shiroUser.getUsername();
            String loginAccount = shiroUser.getLoginAccount();
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了新增工单管控操作");
            return new RtJson<>(0, "操作成功", workTaskId);
        } catch (Exception e) {
            return new RtJson<>(1, "操作失败", e.getMessage());
        }
    }

    /**
     * 查询工单详情
     *
     * @param id 工单id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RtJson findOne(@PathVariable String id) {
        try {
            return new RtJson<>(0, "查询数据成功", workTaskService.findOne(id));
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public RtJson findUserOne(String userId) {
        try {
            return new RtJson<>(0, "查询数据成功", userService.get(userId).getUsername());
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    /**
     * 分页获取工单列表
     *
     * @param templateType 模板类型(1=周期巡检；2=维护保养；3=应急处理)
     * @param status       状态(1=待派；2=待接；3=处理；4=待审；5=已关)
     * @param deviceType   设备类型
     * @param pageSize     获取条数
     * @param pageNumber   当前页数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public RtJson getPage(Integer templateType, Integer status, Integer deviceType, Integer pageSize, Integer pageNumber) {
        try {
            return new RtJson<>(0, "查询数据成功", workTaskService.getWorkTaskByTemplateType(templateType, status, deviceType, pageSize, pageNumber));
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getPageForWorkTaskByDevice", method = RequestMethod.GET)
    public RtJson getPageForWorkTaskByDevice(Integer deviceType, String deviceId, Integer pageSize, Integer pageNumber) {
        try {
            return new RtJson<>(0, "查询成功", workTaskService.findByDeletedFalseAndDeviceTypeAndDeviceIdAndStatus(deviceType, deviceId, pageSize, pageNumber));
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getPageForWorkTaskList", method = RequestMethod.GET)
    public RtJson getPageForWorkTaskList(Integer templateType, Integer status, String taskName, Integer deviceType, Integer priority, Integer pageSize, Integer pageNumber) {
        try {
            return new RtJson<>(0, "查询数据成功", workTaskService.getWorkTaskList(templateType, status, taskName, deviceType, priority, pageSize, pageNumber));
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    /**
     * 批量逻辑删除工单
     *
     * @param ids 工单id数组
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public RtJson deleteWorkTask(String[] ids) {
        //记录日志
        ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
        String currentDateTime = DateUtilities.getCurrentDateTime();
        String logLevelKey = LogLevel.INFO.getValue();
        String logLevelName = LogLevel.INFO.getName();
        SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
        String username = shiroUser.getUsername();
        String loginAccount = shiroUser.getLoginAccount();
        try {
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了删除工单管控成功操作");
            return new RtJson<>(0, "删除数据成功", workTaskService.deleteByIds(ids));
        } catch (Exception e) {
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了删除工单管控失败操作");
            return new RtJson<>(1, "删除数据失败", e.getMessage());
        }
    }

    /**
     * 获取所有设备类型
     * url:/cyclechecktask/deviceTypeList
     *
     * @return rtStatus:返回状态
     * rtMsg:返回消息
     * rtData:返回数据
     */
    @RequestMapping(value = "/deviceType", method = {RequestMethod.GET})
    public
    @ResponseBody
    RtJson deviceTypeList() {
        try {
            return new RtJson<>(0, "查询数据成功", cyclechecktasktemplateService.getDeviceList());
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/templateList", method = RequestMethod.GET)
    public RtJson getTemplateList(Integer templateType, String deviceType) {
        try {
            boolean temp = false;
            Long dType = 1L;
            for (DeviceType type : DeviceType.values()) {
                if (deviceType.equals(String.valueOf(type.getValue()))) {
                    dType = Long.parseLong(deviceType);
                    temp = true;
                    break;
                }
            }
            if (!temp) {
                return new RtJson<>(1, "当前设备类型不符，请与管理员联系", "");
            }
            switch (templateType) {
                case 1:
                    return new RtJson<>(0, "查询数据成功", cyclechecktasktemplateService.findByDeviceType(dType));
                case 2:
                    return new RtJson<>(0, "查询数据成功", null);
                case 3:
                    return new RtJson<>(0, "查询数据成功", emergencytasktemplateService.findByDeviceType(dType));
                default:
                    return new RtJson<>(1, "查询数据失败", null);
            }
        } catch (Exception e) {
            return new RtJson<>(1, "查询数据失败", e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/findItem", method = RequestMethod.GET)
    public RtJson findItem(Integer templateType, String workTaskId, String templateId) {
        try {
            return new RtJson<>(0, "查询成功", workTaskService.findItem(templateType, workTaskId, templateId));
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }

    /**
     * 获取拥有该角色的用户列表
     *
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userByRole", method = RequestMethod.GET)
    public RtJson getUser(String roleId) {
        try {
            return new RtJson<>(0, "查询成功", workTaskService.getUser(roleId));
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }

    /**
     * 根据附件ID下载附件
     *
     * @param id       附件ID
     * @param response response
     */
    @RequestMapping(value = "/{id}/downFile", method = {RequestMethod.GET, RequestMethod.POST})
    public void downFile(@PathVariable String id, HttpServletResponse response) {
        taskadjunctService.downloadFile(id, response);
    }

    /**
     * 根据附件ID查询附件详情
     *
     * @param id 附件ID
     * @return RtJson
     */
    @ResponseBody
    @RequestMapping(value = "/{id}/getFile", method = RequestMethod.GET)
    public RtJson getFile(@PathVariable String id) {
        try {
            return new RtJson<>(0, "查询成功", taskadjunctService.findById(id));
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }

    /**
     * 查询是否支持在线阅读
     *
     * @param id 附件ID
     * @return RtJson
     */
    @ResponseBody
    @RequestMapping(value = "/{id}/readOnlineFile", method = RequestMethod.GET)
    public RtJson readOnlineFile(@PathVariable String id) {
        try {
            return new RtJson<>(0, "查询成功", taskadjunctService.readOnlineFile(id));
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }

    /**
     * 根据附件ID删除附件
     *
     * @param id 附件ID
     * @return RtJson
     */
    @ResponseBody
    @RequestMapping(value = "/{id}/deleteFile", method = {RequestMethod.GET, RequestMethod.POST})
    public RtJson deleteFile(@PathVariable String id) {
        try {
            taskadjunctService.deleteFile(id);
            //记录日志
            ShiroUser shiroUser = SecurityUtils.getShiroUser();//获取shiroUser
            String currentDateTime = DateUtilities.getCurrentDateTime();
            String logLevelKey = LogLevel.INFO.getValue();
            String logLevelName = LogLevel.INFO.getName();
            SysLogInfostate sysLogInfostate =new SysLogInfostate(shiroUser,logLevelKey,logLevelName,currentDateTime);
            String username = shiroUser.getUsername();
            String loginAccount = shiroUser.getLoginAccount();
            sysLogInfostate.setMessage("用户"+username+"("+loginAccount+"),在时间为："+currentDateTime+"进行了删除工单管控附件操作");
            return new RtJson<>(0, "删除成功", id);
        } catch (Exception e) {
            return new RtJson<>(1, "删除失败", e.getMessage());
        }
    }

    /**
     * 跳转到在线阅读页面
     *
     * @param id  附件ID
     * @param map 传递的参数
     * @return String
     */
    @RequestMapping(value = "/{id}/readFile", method = RequestMethod.GET)
    public String mbdcReadFile(@PathVariable String id, Map<String, Object> map) {
        boolean isRead = taskadjunctService.readOnlineFile(id);
        if (isRead) {
            Taskadjunct taskadjunct = taskadjunctService.findById(id);
            String fileExtName = taskadjunct.getFileExtName();
            String fileCode = fileExtName.substring(0, fileExtName.lastIndexOf("."));
            String suffix = fileExtName.substring(fileExtName.lastIndexOf(".") + 1);//获取文件后缀名
            if ("pdf".equals(suffix)) {
                map.put("filePath", "/workprocess/work/" + id + "/downFile");
                return "redirect:" + "/workprocess/work/pdf/viewer";
            } else {
                map.put("userId", MbdcEntity.userId);
                map.put("password", MbdcEntity.password);
                map.put("fileCode", fileCode);
                return "redirect:" + MbdcEntity.viewUrl;
            }
        } else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getTemplate", method = RequestMethod.GET)
    public RtJson getTemplate(Integer templateType, String templateId) {
        try {
            return new RtJson<>(0, "查询成功", workTaskService.getTemplate(templateType, templateId));
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getInspectorName", method = RequestMethod.GET)
    public RtJson getInspectorName(String inspectorId) {
        try {
            User user = userService.get(inspectorId);
            return new RtJson<>(0, "查询成功", user.getUsername());
        } catch (Exception e) {
            return new RtJson<>(1, "查询失败", e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/dealTask", method = RequestMethod.POST)
    public RtJson dealTask(Integer templateType, String[] id, String[] conditions, String[] execution, Long[] isNormal, String workTaskId, String didCondition, HttpServletRequest request) {
        try {
            List<MultipartFile> files = new ArrayList<>();
            Map<String, Object> dataMap = new HashMap<>();
            CommonsMultipartResolver multpartResolver = new CommonsMultipartResolver();
            if (multpartResolver.isMultipart(request)) {
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                Enumeration em = multiRequest.getParameterNames();
                while (em.hasMoreElements()) {
                    String name = String.valueOf(em.nextElement());
                    if (name.contains("file")) {
                        dataMap.put(name, multiRequest.getParameter(name));
                    }
                }
                Iterator<String> iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    // 适配名字重复的文件
                    List<MultipartFile> fileRows = multiRequest.getFiles(iter.next());
                    if (fileRows != null && fileRows.size() != 0) {
                        for (MultipartFile file : fileRows) {
                            if (file != null && !file.isEmpty()) {
                                files.add(file);
                            }
                        }
                    }
                }
            }
            String fileIds = String.valueOf(dataMap.get("fileIds"));//已有附件的ID
            workTaskService.dealTask(templateType, id, conditions, execution, isNormal, workTaskId, didCondition);
            taskadjunctService.uploadFile(workTaskId, files, fileIds);
            return new RtJson<>(0, "操作成功", null);
        } catch (Exception e) {
            return new RtJson<>(1, "操作失败", e.getMessage());
        }
    }
}
