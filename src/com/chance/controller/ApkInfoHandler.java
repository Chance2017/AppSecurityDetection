package com.chance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chance.entities.ApkInfo;
import com.chance.entities.TaskStatus;
import com.chance.service.DatabaseManipulation;

@Controller
@RequestMapping("/security")
public class ApkInfoHandler {
	@RequestMapping("/getApkinfo")
	@ResponseBody
	public ApkInfo getApkInfo(String md5) {
		
		ApkInfo apkInfo = new ApkInfo();
		
		if(md5 == null || md5.length() != 32) {
			apkInfo.setCode(3);
			apkInfo.setMessage("上传数据格式错误");
		}
		
		TaskStatus taskStatus = DatabaseManipulation.getTaskStatus(md5);
		if(taskStatus == null) {
			apkInfo.setCode(2);
			apkInfo.setMessage("任务未创建，拒绝访问");
			return apkInfo;
		} else if(taskStatus.getStatus() == 4) {
			apkInfo.setCode(2);
			apkInfo.setMessage("任务异常，拒绝访问");
			return apkInfo;
		}
		
		apkInfo = DatabaseManipulation.getApkInfo(md5);
		
		if(apkInfo == null) {
			apkInfo = new ApkInfo();
			apkInfo.setCode(4);
			apkInfo.setMessage("所查询数据为空");
			return apkInfo;
		}
		apkInfo.setCode(0);
		apkInfo.setMessage("成功");
		
		return apkInfo;
	}
}
