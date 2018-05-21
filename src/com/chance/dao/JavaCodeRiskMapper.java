package com.chance.dao;

import java.util.List;

import com.chance.entities.JavaCodeRisk;

public interface JavaCodeRiskMapper {
	public void addJavaCodeRisk(JavaCodeRisk javaCodeRisk) ;
	public List<JavaCodeRisk> selectJavaCodeRisksByMD5(String md5);
	public List<JavaCodeRisk> selectJavaCodeRisksByMD5AndRiskname(String md5, String riskname);
	public void updateRiskExist(JavaCodeRisk javaCodeRisk);
}
