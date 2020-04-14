package com.hbxy.gmall.manage.controller;


import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl;

    //http://localhost:8082/fileUpload  图片上传
    //获取文件路径，sprinMVC文件上传
    @RequestMapping("fileUpload")
    public String fileUpload (MultipartFile file) throws IOException, MyException {
        String imgUrl = fileUrl;//imgUrl=http://172.17.152.219
        if (file != null){
            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            // 创建一个TrackerClient对象。
            TrackerClient trackerClient = new TrackerClient();
            // 创建一个TrackerServer对象。
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient=new StorageClient(trackerServer,null);
            String orginalFilename = file.getOriginalFilename();
            //设置文件的后缀名
            String extName = StringUtils.substringAfterLast(orginalFilename, ".");
//            String orginalFilename="C:\\Users\\PotatoHighness\\Desktop\\1.jpg";
            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                //字符串拼接
                imgUrl+="/"+path;
            }
        }
        return imgUrl;
    }
}
