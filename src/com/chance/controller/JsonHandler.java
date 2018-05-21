package com.chance.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JsonHandler {
	
	@RequestMapping(value="/jsonParse", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> jsonParser(String add, Info info, boolean risk1, boolean risk2) {
		
		Map<String, Object> map = new HashMap<>(10);
		map.put("add", add);
		map.put("info", info);
		map.put("name", info.getName());
		map.put("password", info.getPassword());
		map.put("risk1", risk1);
		map.put("risk2", risk2);
		
		System.out.println("java打印：" + map.get("add"));
		System.out.println("java打印：" + map.get("name"));
		System.out.println("java打印：" + map.get("password"));
		System.out.println("java打印：" + map.get("risk1"));
		System.out.println("java打印：" + map.get("risk2"));
		
		return map;
	}
}
class Info {
	private String name;
	private String password;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
