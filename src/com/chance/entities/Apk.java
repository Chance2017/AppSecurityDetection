package com.chance.entities;

public class Apk {
	//文件在数据库中的id值
	private Integer id;
	//文件名
	private String filename;
	//文件的MD5值
	private String MD5;
	//返回码
	private Integer code;
	
	public Apk() {
		super();
	}
	public Apk(Integer id, String filename, String md5, Integer code) {
		super();
		this.id = id;
		this.filename = filename;
		this.MD5 = md5;
		this.code = code;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getMD5() {
		return MD5;
	}
	public void setMD5(String mD5) {
		this.MD5 = mD5;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "Apk [id=" + id + ", filename=" + filename + ", MD5=" + MD5 + ", code=" + code + "]";
	}
}
