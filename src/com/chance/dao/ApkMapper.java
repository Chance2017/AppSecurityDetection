package com.chance.dao;

import java.util.List;

import com.chance.entities.Apk;

public interface ApkMapper {
	public void addApk(Apk apk) ;
	public Apk getApkByMD5(Apk apk);
	public Apk getApkById(int id);
	public List<Apk> getApks();
}
