package com.chance.entities;

import java.sql.Date;

public class ApkInfo {
	//文件MD5值
	private String md5;
	//返回码
	private Integer code;
	//简要说明
	private String message;
	//软件版本
	private String version;
	//应用程序名
	private String appName;
	//应用程序包名
	private String packageName;
	//创建时间
	private Date date;
	
	public ApkInfo() {
		super();
	}
	
	public ApkInfo(String md5, String version, String appName, String packageName) {
		super();
		this.md5 = md5;
		this.version = version;
		this.appName = appName;
		this.packageName = packageName;
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "ApkInfo [md5=" + md5 + ", version=" + version + ", appName=" + appName + ", packageName=" + packageName + "]";
	}
}
