package com.workprocess.services;

import com.workprocess.entity.Taskadjunct;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface TaskadjunctService {
    List<Taskadjunct> findByWorkTaskId(String workTaskId);
    void uploadFile(String workTaskId,List<MultipartFile> files,String fileIds);
    void downloadFile(String id, HttpServletResponse response);
    Taskadjunct findById(String id);
    boolean readOnlineFile(String id);
    void deleteFile(String id);

}
