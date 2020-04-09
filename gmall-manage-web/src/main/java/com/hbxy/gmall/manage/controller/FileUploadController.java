package com.hbxy.gmall.manage.controller;


import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {

    //http://localhost:8082/fileUpload  图片上传
    @RequestMapping("fileUpload")
    public String fileUpload () throws IOException, MyException {
        String file = this.getClass().getResource("/tracker.conf").getFile();
        ClientGlobal.init(file);
        // 创建一个TrackerClient对象。
        TrackerClient trackerClient = new TrackerClient();
        // 创建一个TrackerServer对象。
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageClient storageClient=new StorageClient(trackerServer,null);
        String orginalFilename="C:\\Users\\PotatoHighness\\Desktop\\1.jpg";
        String[] upload_file = storageClient.upload_file(orginalFilename, "jpg", null);
        for (int i = 0; i < upload_file.length; i++) {
            String s = upload_file[i];
            System.out.println("s = " + s);
        }
        return "";
    }
}
