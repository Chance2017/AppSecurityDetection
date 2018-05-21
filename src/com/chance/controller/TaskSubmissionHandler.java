package com.chance.controller;

import java.io.File;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chance.entities.RisksCategories;
import com.chance.entities.Settings;
import com.chance.entities.TaskStatus;
import com.chance.service.DatabaseManipulation;
import com.chance.thread.RiskDetectThread;

@Controller
@RequestMapping("/security")
public class TaskSubmissionHandler {
	
	@RequestMapping("/submission")
	@ResponseBody
	public Map<String, Object> taskSubmission(String filename, String md5, RisksCategories risksCategories) {
		
		long startTime = System.currentTimeMillis();
		
		Map<String, Object> map = new HashMap<>();
		
		System.out.println("\n\n\n" + risksCategories + "\n\n\n");
		
		int code;
		String message;
		int status;
		Date date = new java.sql.Date((new java.util.Date()).getTime());
		
		if(filename == null || md5 == null || md5.length() != 32 || risksCategories == null) {
			//格式错误，上传的数据不是要求的json或者json数据某字段格式错误
			code 	= 3;
			message = "上传数据格式错误";
			status 	= 1;
		} else {
			File file = new File(Settings.Apk_File_Path + filename);
			//获取文件扩展名
			String suffixName = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
			System.out.println("文件扩展名：" + suffixName);
			
			if(!file.exists()) {
				code 	= 103;
				message = "文件不存在";
				status 	= 1;
			} else if(!suffixName.equals("apk")) {
				code 	= 104;
				message = "文件格式不正确";
				status 	= 1;
			} else {
				TaskStatus taskStatus = new TaskStatus();
				taskStatus = DatabaseManipulation.getTaskStatus(md5);
				
				//根据此md5值判断其之前是否已经被本系统检测过或者正在检测
				if(taskStatus != null) {
					if(taskStatus.getStatus() ==  4) {
						code 	= 2;
						message = "任务创建失败";
						status 	= 1;
					} else {
						code 	= 0;
						message = "任务创建成功";
						status 	= 0;
					}
					date = taskStatus.getDate();
				} else {
					code 	= 0;
					message = "任务创建成功";
					status 	= 0;
					
					//设置任务状态对象并保存进数据库
					taskStatus = new TaskStatus(md5, 0, "任务创建成功", risksCategories.risksCount() + 1, 0, 0);
					DatabaseManipulation.writeToDataBase(taskStatus);
					
					RiskDetectThread riskDetectThread = new RiskDetectThread(filename, md5, risksCategories);
					riskDetectThread.start();
				}
			}
		}
		
		map.put("code", code);
		map.put("message", message);
		map.put("status", status);
		//While the task has not created, this value is null
		map.put("date", date);
		
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
		
		return map;
	}
}
