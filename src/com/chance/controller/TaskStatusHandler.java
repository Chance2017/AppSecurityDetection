package com.chance.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chance.entities.TaskStatus;
import com.chance.service.DatabaseManipulation;

@Controller
@RequestMapping("/security")
public class TaskStatusHandler {
	
	@RequestMapping("/status")
	@ResponseBody
	public Map<String, Object> getTaskStatus(String md5) {
		
		Map<String, Object> map = new HashMap<>();
		
		if(md5 == null || md5.length() != 32) {
			map.put("code", 3);
			map.put("message", "上传数据格式错误");
			return map;
		}
		
		TaskStatus taskStatus = DatabaseManipulation.getTaskStatus(md5);
		
		if(taskStatus == null) {
			map.put("code", 4);
			map.put("message","所查询数据为空");
		} else {
			map.put("code", taskStatus.getCode());
			map.put("message", taskStatus.getMessage());
			map.put("total", taskStatus.getTotal());
			map.put("finished", taskStatus.getFinished());
			map.put("status", taskStatus.getStatus());
		}
		
		return map;
	}
	
}
