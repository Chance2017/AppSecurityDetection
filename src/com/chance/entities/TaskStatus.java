package com.chance.entities;

import java.sql.Date;

public class TaskStatus {
	private String md5;
	private Integer code;
	private String message;
	private Integer total;
	private Integer finished;
	private Integer status;
	private Date date;
	public TaskStatus() {
		super();
	}
	public TaskStatus(String md5, Integer code, String message, Integer total, Integer finished, Integer status) {
		super();
		this.md5 = md5;
		this.code = code;
		this.message = message;
		this.total = total;
		this.finished = finished;
		this.status = status;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
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
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getFinished() {
		return finished;
	}
	public void setFinished(Integer finished) {
		this.finished = finished;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
