package com.workprocess.utils.fileconverter;

import com.google.gson.Gson;
import com.workprocess.utils.fileconverter.data.MbdcData;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MbdcUtil {
    /**
     * 判断文件是否属于写阅读文件
     * @param fileName
     * @return
     */
    public static boolean isMbdcFile(String fileName){
        String s=fileName.substring(fileName.lastIndexOf(".")+1);
        String[] n={"doc","docx","xls","xlsx","ppt","pptx","txt"};
        return Arrays.asList(n).contains(s.toLowerCase());
    }
    /**
     * 发起POST请求
     * @param url
     * @param entity
     * @return
     * @throws Exception
     */
    private static String postCommand(String url, HttpEntity entity) throws Exception{
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        if(entity!=null){
            post.setEntity(entity);
        }
        return retJson(client.execute(post));
    }
    /**
     * 发起GET请求
     * @param url
     * @return
     * @throws Exception
     */
    private static String getCommand(String url) throws Exception{
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        return retJson(client.execute(get));
    }
    /**
     * 获取请求到的内容
     * @param response
     * @return
     * @throws Exception
     */
    private static String retJson(HttpResponse response) throws Exception{
        int status=response.getStatusLine().getStatusCode();
        if(status== HttpStatus.SC_OK){
            return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        }else{
            throw new Exception("文档转换错误,错误码:"+status);
        }
    }
    /**
     * 文档在线浏览
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String viewfile(String url, Map<String, Object> data) throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for(String key:data.keySet()){
            params.add(new BasicNameValuePair(key, String.valueOf(data.get(key))));
        }
        HttpEntity entity = new UrlEncodedFormEntity(params, Charset.forName("UTF-8"));
        return postCommand(url, entity);
    }
    /**
     * 文档转换,返回Json
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String upload(String url,Map<String, Object> data) throws Exception{
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        for(String key:data.keySet()){
            if(data.get(key) instanceof File){
                entity.addPart(key, new FileBody((File)data.get(key)));
            }else{
                entity.addPart(key, new StringBody(String.valueOf(data.get(key)), Charset.forName("UTF-8")));
            }

        }
        return postCommand(url, entity);
    }
    /**
     * 文档转换,返回MbdcData
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static MbdcData uploadFile(String url,Map<String, Object> data) throws Exception{
        return new Gson().fromJson(upload(url, data), MbdcData.class);
    }
    /**
     * 判断文档状态,返回json
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String status(String url,Map<String, Object> data) throws Exception{
        url=url+"?";
        for(String key:data.keySet()){
            url+=key+"="+String.valueOf(data.get(key))+"&";
        }
        if(data.size()>0)url=url.substring(0,url.length()-1);
        return getCommand(url);
    }
}
