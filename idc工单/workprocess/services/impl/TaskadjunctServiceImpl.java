package com.workprocess.services.impl;

import com.alibaba.fastjson.JSON;
import com.greenpanit.utils.Identities;
import com.workprocess.dao.TaskAdjunctDao;
import com.workprocess.entity.Taskadjunct;
import com.workprocess.services.TaskadjunctService;
import com.workprocess.utils.EncodingDetect;
import com.workprocess.utils.Utiliy;
import com.workprocess.utils.fileconverter.MbdcUtil;
import com.workprocess.utils.fileconverter.entity.MbdcEntity;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TaskadjunctServiceImpl implements TaskadjunctService {
    @Autowired
    private TaskAdjunctDao taskAdjunctDao;

    /**
     * 根据工单ID查询相关的附件
     * @param workTaskId 工单ID
     * @return List
     */
    @Override
    public List<Taskadjunct> findByWorkTaskId(String workTaskId) {
        return taskAdjunctDao.findByWorkTaskId(workTaskId);
    }

    /**
     * 添加附件
     * @param workTaskId 工单ID
     * @param fileIds  已有附件的ID,以","分隔
     * @param files 附件
     */
    @Override
    public void uploadFile(String workTaskId, List<MultipartFile> files,String fileIds) {
        List<Taskadjunct> list=taskAdjunctDao.findByWorkTaskIdAndNotInId(workTaskId,new ArrayList<>(Arrays.asList(fileIds.split(","))));
        for(Taskadjunct taskadjunct:list){
            String fileExtName=taskadjunct.getFileExtName();
            String filePath=taskadjunct.getFilePath();
            File file=new File(Utiliy.workProcessFilePath()+File.separator+filePath+File.separator+fileExtName);
            if(file.isFile()){
                file.delete();
            }
            taskAdjunctDao.delete(taskadjunct.getId());
        }
        if(files!=null && files.size()>0){
            for(MultipartFile file:files){
                Taskadjunct taskadjunct=new Taskadjunct();
                taskadjunct.setId(Identities.uuid());
                taskadjunct.setWorkTaskId(workTaskId);
                String fileName=file.getOriginalFilename();
                taskadjunct.setFileName(fileName);
                String fileExtName=Identities.uuid()+fileName.substring(fileName.lastIndexOf("."));
                taskadjunct.setFileExtName(fileExtName);
                Timestamp createTime=new Timestamp(System.currentTimeMillis());
                taskadjunct.setCreateTime(createTime);
                String filePath="workprocess"+File.separator+new SimpleDateFormat("yyyyMM").format(createTime);
                taskadjunct.setFilePath(filePath);
                taskadjunct.setUrl("");
                taskAdjunctDao.save(taskadjunct);
                try{
                    CommonsMultipartFile cmFile=(CommonsMultipartFile)file;
                    DiskFileItem fileItem=(DiskFileItem)cmFile.getFileItem();
                    InputStream in=fileItem.getInputStream();
                    File outFile = new File(Utiliy.workProcessFilePath()+File.separator+filePath+File.separator+fileExtName);
                    File outFilePath = outFile.getParentFile();
                    if (!outFilePath.exists()) outFilePath.mkdirs();
                    if (!outFile.isFile()) outFile.createNewFile();
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
                    writeFile(in, out);

                    //添加mbdc在线阅读
                    if(MbdcUtil.isMbdcFile(fileName)){//判断文件是否支持mbdc在线阅读
                        if(MbdcEntity.isOpen){//判断系统是否开启mbdc在线阅读
                            Map<String,Object> data=new HashMap<>();
                            data.put("userId",MbdcEntity.userId);
                            data.put("password",MbdcEntity.password);
                            data.put("fileCode",fileExtName.substring(0,fileExtName.lastIndexOf(".")));
                            data.put("file",outFile);
                            MbdcUtil.upload(MbdcEntity.uploadUrl,data);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据附件ID查询附件详情
     * @param id 附件ID
     * @return Taskadjunct
     */
    @Override
    public Taskadjunct findById(String id) {
        return taskAdjunctDao.findOne(id);
    }

    /**
     * 根据附件ID判断该附件是否支持在线阅读，若支持，将在线阅读所需的参数返回
     * @param id 附件ID
     * @return Map<String, Object>
     */
    @Override
    public boolean readOnlineFile(String id) {
        boolean isRead=false;
        Taskadjunct taskadjunct=taskAdjunctDao.findOne(id);
        String fileExtName=taskadjunct.getFileExtName();
        String filePath=taskadjunct.getFilePath();
        String userId=MbdcEntity.userId;
        String password=MbdcEntity.password;
        String fileCode=fileExtName.substring(0,fileExtName.lastIndexOf("."));
        String suffix=fileExtName.substring(fileExtName.lastIndexOf(".")+1);//获取文件后缀名
        if("pdf".equals(suffix)){
            isRead=true;
        }else{
            try {
                if(MbdcUtil.isMbdcFile(taskadjunct.getFileName())){//判断文件是否支持mbdc在线阅读
                    if(MbdcEntity.isOpen){//判断系统是否开启mbdc在线阅读
                        Map<String,Object> mbdcMap=new HashMap<>();
                        mbdcMap.put("userId",userId);
                        mbdcMap.put("password",password);
                        mbdcMap.put("fileCode",fileCode);
                        String json=MbdcUtil.status(MbdcEntity.statusFileUrl,mbdcMap);
                        Map maps = (Map) JSON.parse(json);
                        String status="";
                        if(maps != null){
                            status = (String) maps.get("status");
                        }
                        if("2".equals(status)) {//如果需要重新转换
                            File mbdcFile=new File(Utiliy.workProcessFilePath()+File.separator+filePath+File.separator+fileExtName);
                            File mbdcFilePath=mbdcFile.getParentFile();
                            if(!mbdcFilePath.exists())mbdcFilePath.mkdirs();
                            if (!mbdcFile.isFile())mbdcFile.createNewFile();
                            if("txt".equals(suffix)){//防止txt文本中文乱码
                                String fileEncode= EncodingDetect.getJavaEncode(Utiliy.workProcessFilePath()+File.separator+filePath+File.separator+fileExtName);//获取文件编码格式
                                String fileContent= FileUtils.readFileToString(mbdcFile,fileEncode);//获取文件内容
                                if(mbdcFile.delete()){
                                    if(!mbdcFile.isFile())mbdcFile.createNewFile();
                                    Writer outTxt = new OutputStreamWriter(new FileOutputStream(mbdcFile,true), "UTF-8");
                                    outTxt.write(fileContent); //写入文件
                                    outTxt.close();
                                }
                            }
                            //添加mbdc在线阅读
                            Map<String,Object> mbdcUploadMap=new HashMap<>();
                            mbdcUploadMap.put("userId",userId);
                            mbdcUploadMap.put("password",password);
                            mbdcUploadMap.put("fileCode",fileCode);
                            mbdcUploadMap.put("file",mbdcFile);
                            MbdcUtil.upload(MbdcEntity.uploadUrl,mbdcUploadMap);

                        }
                        isRead=true;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return isRead;
    }

    /**
     * 根据附件ID删除附件
     * @param id 附件ID
     */
    @Override
    public void deleteFile(String id) {
        Taskadjunct taskadjunct=taskAdjunctDao.findOne(id);
        String fileName=taskadjunct.getFileName();
        String fileExtName=taskadjunct.getFileExtName();
        String filePath=taskadjunct.getFilePath();
        File file=new File(Utiliy.workProcessFilePath()+File.separator+filePath+File.separator+fileExtName);
        if(file.isFile()){
            file.delete();
        }
        taskAdjunctDao.delete(id);
    }

    /**
     * 下载附件
     * @param id 附件ID
     * @param response response
     */
    @Override
    public void downloadFile(String id,HttpServletResponse response){
        Taskadjunct taskadjunct=taskAdjunctDao.findOne(id);
        String fileName=taskadjunct.getFileName();
        String fileExtName=taskadjunct.getFileExtName();
        String filePath=taskadjunct.getFilePath();
        downloadFile(new File(Utiliy.workProcessFilePath()+File.separator+filePath+File.separator+fileExtName),fileName,response);
    }

    /**
     * 下载附件
     * @param downloadFile 需要下载的附件
     * @param fileName 附件名称(加上后缀名)
     * @param response response
     */
    private void downloadFile(File downloadFile, String fileName, HttpServletResponse response) {
        try {
            File downloadFilePath=downloadFile.getParentFile();
            if(!downloadFilePath.exists())downloadFilePath.mkdirs();
            if (!downloadFile.isFile()) {
                downloadFile.createNewFile();
            }
            InputStream in=new FileInputStream(downloadFile);
            response.setContentType("application/octet-stream");
            response.setHeader("Cache-Control", "maxage=3600");
            response.setHeader("Pragma", "public");
            response.setHeader("Accept-Ranges", "bytes");
            response.setCharacterEncoding("UTF-8");
            fileName = new String(fileName.getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
            OutputStream out = response.getOutputStream();
            writeFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 写入附件
     * @param in 输入流
     * @param out 输出流
     */
    private void writeFile(InputStream in, OutputStream out) {
        try {
            byte[] buff = new byte[1024 * 1024];
            int length = 0;
            while ((length = in.read(buff)) > 0) {
                out.write(buff, 0, length);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
