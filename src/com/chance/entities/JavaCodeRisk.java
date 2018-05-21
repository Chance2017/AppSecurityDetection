package com.chance.entities;

public class JavaCodeRisk {
	private Integer id;
	/**
	 * 文件md5值
	 */
	private String md5;
	/**
	 * 所检测的风险名称
	 */
	private String riskName;
	/**
	 * 风险所在代码的包名
	 */
	private String packageName;
	/**
	 * 风险所在代码的类名
	 */
	private String className;
	/**
	 *  风险所在代码的父类名
	 */
	private String superClassName;
	/**
	 * 风险所在代码的方法名
	 */
	private String methodName;
	/**
	 * 风险所在代码的行号
	 */
	private int lineNumber;
	/**
	 * 记录此风险是否存在
	 * 默认存在，仅对removeJavascriptInterface漏洞和文件跨域访问漏洞有用
	 */
	private boolean riskExist;
	
	public JavaCodeRisk() {
		super();
	}
	
	public JavaCodeRisk(Integer id, String md5, String riskName, String packageName, String className,
			String superClassName, String methodName, int lineNumber, boolean riskExist) {
		super();
		this.id = id;
		this.md5 = md5;
		this.riskName = riskName;
		this.packageName = packageName;
		this.className = className;
		this.superClassName = superClassName;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
		this.riskExist = riskExist;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMD5() {
		return md5;
	}

	public void setMD5(String md5) {
		this.md5 = md5;
	}

	public String getRiskName() {
		return riskName;
	}

	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public boolean isRiskExist() {
		return riskExist;
	}

	public void setRiskExist(boolean riskExist) {
		this.riskExist = riskExist;
	}
}
