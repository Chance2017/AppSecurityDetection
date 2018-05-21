package com.chance.entities;

public class RisksCategories {
	
	private boolean ManifestRisks;
	private boolean StorageRisks;
	private boolean DbInjectRisks;
	private boolean ActivityHijackRisks;
	private boolean AddJsInterfaceRisks;
	private boolean HttpsCertNotVerifyRisks;
	private boolean SavePasswordRisks;
	private boolean FileAccessFromURLsRisks;
	public RisksCategories() {
		super();
	}
	public RisksCategories(boolean manifestRisks, boolean storageRisks, boolean dbInjectRisks,
			boolean activityHijackRisks, boolean addJsInterfaceRisks, boolean httpsCertNotVerifyRisks,
			boolean savePasswordRisks, boolean fileAccessFromURLsRisks) {
		super();
		ManifestRisks = manifestRisks;
		StorageRisks = storageRisks;
		DbInjectRisks = dbInjectRisks;
		ActivityHijackRisks = activityHijackRisks;
		AddJsInterfaceRisks = addJsInterfaceRisks;
		HttpsCertNotVerifyRisks = httpsCertNotVerifyRisks;
		SavePasswordRisks = savePasswordRisks;
		FileAccessFromURLsRisks = fileAccessFromURLsRisks;
	}
	public boolean isManifestRisks() {
		return ManifestRisks;
	}
	public void setManifestRisks(boolean manifestRisks) {
		ManifestRisks = manifestRisks;
	}
	public boolean isStorageRisks() {
		return StorageRisks;
	}
	public void setStorageRisks(boolean storageRisks) {
		StorageRisks = storageRisks;
	}
	public boolean isDbInjectRisks() {
		return DbInjectRisks;
	}
	public void setDbInjectRisks(boolean dbInjectRisks) {
		DbInjectRisks = dbInjectRisks;
	}
	public boolean isActivityHijackRisks() {
		return ActivityHijackRisks;
	}
	public void setActivityHijackRisks(boolean activityHijackRisks) {
		ActivityHijackRisks = activityHijackRisks;
	}
	public boolean isAddJsInterfaceRisks() {
		return AddJsInterfaceRisks;
	}
	public void setAddJsInterfaceRisks(boolean addJsInterfaceRisks) {
		AddJsInterfaceRisks = addJsInterfaceRisks;
	}
	public boolean isHttpsCertNotVerifyRisks() {
		return HttpsCertNotVerifyRisks;
	}
	public void setHttpsCertNotVerifyRisks(boolean httpsCertNotVerifyRisks) {
		HttpsCertNotVerifyRisks = httpsCertNotVerifyRisks;
	}
	public boolean isSavePasswordRisks() {
		return SavePasswordRisks;
	}
	public void setSavePasswordRisks(boolean savePasswordRisks) {
		SavePasswordRisks = savePasswordRisks;
	}
	public boolean isFileAccessFromURLsRisks() {
		return FileAccessFromURLsRisks;
	}
	public void setFileAccessFromURLsRisks(boolean fileAccessFromURLsRisks) {
		FileAccessFromURLsRisks = fileAccessFromURLsRisks;
	}
	@Override
	public String toString() {
		return "RisksCategories [ManifestRisks=" + ManifestRisks + ", StorageRisks=" + StorageRisks + ", DbInjectRisks="
				+ DbInjectRisks + ", ActivityHijackRisks=" + ActivityHijackRisks + ", AddJsInterfaceRisks="
				+ AddJsInterfaceRisks + ", HttpsCertNotVerifyRisks=" + HttpsCertNotVerifyRisks + ", SavePasswordRisks="
				+ SavePasswordRisks + ", FileAccessFromURLsRisks=" + FileAccessFromURLsRisks + "]";
	}
	public int risksCount() {
		int total = 0;
		if(ManifestRisks) total ++;
		if(StorageRisks) total ++;
		if(DbInjectRisks) total ++;
		if(ActivityHijackRisks) total ++;
		if(AddJsInterfaceRisks) total ++;
		if(HttpsCertNotVerifyRisks) total ++;
		if(SavePasswordRisks) total ++;
		if(FileAccessFromURLsRisks) total ++;
		return total;
	}
}
