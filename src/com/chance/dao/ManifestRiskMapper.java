package com.chance.dao;

import java.util.List;

import com.chance.entities.ManifestRisk;

public interface ManifestRiskMapper {
	public void addManifestRisk(ManifestRisk manifestDetection);
	public ManifestRisk selectManifestRisk(String md5);
	public List<ManifestRisk> selectAllManifestRisks();
}
