package com.pinyougou.shop.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String file_server_url;
	@RequestMapping("/upload")
	public Result Upload(MultipartFile file) {
		System.out.println("经过了@RequestMapping的Upload方法");
		try {
			
			String originalFilename = file.getOriginalFilename();
			System.out.println("===========>"+originalFilename+"<==== file.getOriginalFilename();=================");
			String suffixAndName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			String fileId = fastDFSClient.uploadFile(file.getBytes(),suffixAndName);
			String url= file_server_url+fileId;
			return new Result(true,url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"上传失败");
		}
	}
}
