package com.chance.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chance.entities.JavaCodeRisk;
import com.chance.entities.ManifestRisk;
import com.chance.entities.TaskResult;
import com.chance.entities.TaskStatus;
import com.chance.service.DatabaseManipulation;

@Controller
@RequestMapping("/security")
public class TaskResultHandler {
	
	@RequestMapping("/result")
	@ResponseBody
	public TaskResult getTaskResult(String md5) {

		//封装任务结果类
		TaskResult taskResult = new TaskResult();
		
		if(md5 == null || md5.length() != 32) {
			taskResult.setCode(3);
			taskResult.setMessage("上传数据格式错误");
			return taskResult;
		}
		
		JavaCodeRisk javaCodeRisk;
		
		TaskStatus taskStatus = DatabaseManipulation.getTaskStatus(md5);
		
		if(taskStatus == null) {
			taskResult.setCode(2);
			taskResult.setMessage("该任务未创建,拒绝访问");
			return taskResult;
		} else if(taskStatus.getStatus() == 4) {
			taskResult.setCode(2);
			taskResult.setMessage("任务异常,拒绝访问");
			return taskResult;
		} else if(taskStatus.getStatus() != 3) {
			taskResult.setCode(202);
			taskResult.setMessage("任务运行中，请等待");
			return taskResult;
		}
		
		//查询所有Java风险代码
		List<JavaCodeRisk> javaCodeRiskList = DatabaseManipulation.getJavaCodeRiskList(md5);
		taskResult.setRiskList(javaCodeRiskList);
		
		//查询所有高危配置参数
		ManifestRisk manifestRisk = DatabaseManipulation.getManifestRisk(md5);
		if(manifestRisk != null) {
			if(manifestRisk.isAllowBackup()) {
				javaCodeRisk = new JavaCodeRisk();
				javaCodeRisk.setRiskName("allowBackup");
				javaCodeRisk.setRiskExist(true);
				taskResult.addRiskToList(javaCodeRisk);
			}
			if(manifestRisk.isDebuggable()) {
				javaCodeRisk = new JavaCodeRisk();
				javaCodeRisk.setRiskName("debuggable");
				javaCodeRisk.setRiskExist(true);
				taskResult.addRiskToList(javaCodeRisk);
			}
			if(manifestRisk.isExported()) {
				javaCodeRisk = new JavaCodeRisk();
				javaCodeRisk.setRiskName("exported");
				javaCodeRisk.setRiskExist(true);
				taskResult.addRiskToList(javaCodeRisk);
			}
			if(manifestRisk.isProtectionLevel()) {
				javaCodeRisk = new JavaCodeRisk();
				javaCodeRisk.setRiskName("protectionLevel");
				javaCodeRisk.setRiskExist(true);
				taskResult.addRiskToList(javaCodeRisk);
			}
			if(manifestRisk.isSharedUserId()) {
				javaCodeRisk = new JavaCodeRisk();
				javaCodeRisk.setRiskName("sharedUserId");
				javaCodeRisk.setRiskExist(true);
				taskResult.addRiskToList(javaCodeRisk);
			}
		}
		
		taskResult.setCode(0);
		taskResult.setMessage("成功");
		
		return taskResult;
	}
}
