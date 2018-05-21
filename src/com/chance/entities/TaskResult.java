package com.chance.entities;

import java.util.List;

public class TaskResult {
	private Integer code;
	private String message;
	private List<JavaCodeRisk> riskList;
	
	public TaskResult() {
		super();
	}
	public TaskResult(Integer code, String message, List<JavaCodeRisk> riskList) {
		super();
		this.code = code;
		this.message = message;
		this.riskList = riskList;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<JavaCodeRisk> getRiskList() {
		return riskList;
	}
	public void setRiskList(List<JavaCodeRisk> riskList) {
		this.riskList = riskList;
	}
	public void addRiskToList(JavaCodeRisk risk) {
		riskList.add(risk);
	}
}
