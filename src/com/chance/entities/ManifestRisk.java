package com.chance.entities;

public class ManifestRisk {
	//文件MD5值
	private String md5;
	//sharedUserId
	private boolean sharedUserId;
	//allowBackup
	private boolean allowBackup;
	//debuggable
	private boolean debuggable;
	//protectionLevel
	private boolean protectionLevel;
	//exported
	private boolean exported;
	
	public ManifestRisk() {
		super();
	}
	public ManifestRisk(String md5, boolean sharedUserId, boolean allowBackup, boolean debuggable,
			boolean protectionLevel, boolean exported) {
		super();
		this.md5 = md5;
		this.sharedUserId = sharedUserId;
		this.allowBackup = allowBackup;
		this.debuggable = debuggable;
		this.protectionLevel = protectionLevel;
		this.exported = exported;
	}
	public String getMD5() {
		return md5;
	}
	public void setMD5(String md5) {
		this.md5 = md5;
	}
	public boolean isSharedUserId() {
		return sharedUserId;
	}
	public void setSharedUserId(boolean sharefUserId) {
		this.sharedUserId = sharefUserId;
	}
	public boolean isAllowBackup() {
		return allowBackup;
	}
	public void setAllowBackup(boolean allowBackup) {
		this.allowBackup = allowBackup;
	}
	public boolean isDebuggable() {
		return debuggable;
	}
	public void setDebuggable(boolean debuggable) {
		this.debuggable = debuggable;
	}
	public boolean isProtectionLevel() {
		return protectionLevel;
	}
	public void setProtectionLevel(boolean protectionLevel) {
		this.protectionLevel = protectionLevel;
	}
	public boolean isExported() {
		return exported;
	}
	public void setExported(boolean exported) {
		this.exported = exported;
	}
	
}
