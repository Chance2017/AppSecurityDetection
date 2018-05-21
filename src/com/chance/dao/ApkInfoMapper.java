package com.chance.dao;

import com.chance.entities.ApkInfo;

public interface ApkInfoMapper {
	public void addApkInfo(ApkInfo apkInfo);
	public ApkInfo selectApkInfo(String md5);
}
