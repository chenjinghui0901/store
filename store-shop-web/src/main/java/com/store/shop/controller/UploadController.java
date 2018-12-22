package com.store.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String file_server_url;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) {

        try {
            // 获得文件名:
            String fileName = file.getOriginalFilename();
            // 获得文件的扩展名:
            String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
            // 创建工具类
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");

            String path = client.uploadFile(file.getBytes(), extName); // group1/M00/

            String url = file_server_url + path;
            return new Result(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败！");
        }
    }


    @RequestMapping("/deleteFile")
    public Result deleteFile(String url){
        //url http://47.96.111.66/group1/M00/00/00/wKjRhFn1bH2AZAatAACXQA462ec665.jpg
        try {
            String fileId = url.substring(url.indexOf("g"));
            // 创建工具类
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            client.deleteFile(fileId);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "删除失败");
        }
    }

}
