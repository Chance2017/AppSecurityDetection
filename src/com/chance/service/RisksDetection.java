package com.chance.service;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.chance.entities.ManifestRisk;
import com.chance.entities.Settings;
import com.chance.smali.SmaliParser;

public class RisksDetection {
	
	/**
	 * 检测AndroidManifest.xml配置文件
	 * @param MD5
	 * @return ManifestRisk
	 */
	public ManifestRisk manifestRisksDetect(String MD5) {
				
		long startTime = System.currentTimeMillis();
		
		SAXReader reader 							= new SAXReader();
		ManifestRisk manifestRisk 					= new ManifestRisk();
		String exportedText 						= null;
		String protectionLevelText 					= null;
		
		Document document;
		manifestRisk.setMD5(MD5);
		
		try {
			document = reader.read(new File(Settings.Decompile_Out_Path + MD5 + "/AndroidManifest.xml"));
			Element root = document.getRootElement();
			Element application = root.element("application");
			
			//检测sharedUserId属性
			String sharedUserId = root.attributeValue("sharedUserId");
			manifestRisk.setSharedUserId((sharedUserId == null || sharedUserId.equals("false"))?false:true);
			
			//检测allowBackup属性
			String allowBackup = application.attributeValue("allowBackup");
			manifestRisk.setAllowBackup((allowBackup == null || allowBackup.equals("true"))?true:false);
			
			//检测debuggable属性
			String debuggable = application.attributeValue("debuggable");
			manifestRisk.setDebuggable((debuggable == null || debuggable.equals("false"))?false:true);
			
			//检测所有的protectionLevel属性
			@SuppressWarnings("unchecked")
			List<Element> permissionElements = root.elements("permission");
			for (Element element : permissionElements) {
				protectionLevelText = element.attributeValue("protectionLevel");
				if(!protectionLevelText.equals("signature") && !protectionLevelText.equals("signatureOrSystem")) {
					manifestRisk.setProtectionLevel(true);
					break;
				}
			}
			
			//检测所有的exported属性
			@SuppressWarnings("unchecked")
			List<Element> elements = application.elements();
			System.out.println(elements.size());
			for (Element element : elements) {
				exportedText = element.attributeValue("exported");
				if(exportedText == null || exportedText.equals("true")){
					manifestRisk.setExported(true);
					break;
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		DatabaseManipulation.writeToDataBase(manifestRisk);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Manifest文件检测程序执行时间:" + (endTime - startTime) + "ms");
		
		return manifestRisk;
	}
	
	/**
	 * 获取所有的Smali文件夹，迭代进行检测
	 * @param md5 apk文件MD5值
	 */
	public void smaliRisksDetect(String md5) {
		long startTime = System.currentTimeMillis();
		
		File file = new File(Settings.Decompile_Out_Path + md5);
		File[] files = file.listFiles();
		Pattern pattern = Pattern.compile("^smali");
		for (File smaliDirectory : files) {
			Matcher matcher = pattern.matcher(smaliDirectory.getName());
			if(matcher.find()) {
				if(!file.exists()) {
					System.out.println("文件夹不存在");
				} else {
					checkFile(new File(file.getAbsolutePath() + "/" + smaliDirectory.getName()), md5);
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Smali文件检测程序执行时间:" + (endTime - startTime) + "ms");
	}
	
	/**
	 * 扫描所有的smali文件，递归进行检测
	 * @param checkingFile 待检测文件目录
	 * @param md5 apk文件MD5值
	 */
	public void checkFile(File checkingFile, String md5) {
		//匹配所有的smali文件，并过滤掉以R.，R$和BuildConfig.开头的smali文件
		Pattern pattern = Pattern.compile("^(?!R\\\\$|R\\\\.|BuildConfig\\\\.).*[.]smali$");
		
		if(checkingFile.isDirectory()) {
			
			if(checkingFile.getName().equals("android")) 
				return ;
			
			File[] files = checkingFile.listFiles();
			for(File file : files) {
				checkFile(file, md5);
			}
		} else {
			String filename = checkingFile.getName();
			Matcher matcher = pattern.matcher(filename);
			if(matcher.find()) {
				SmaliParser smaliParser = new SmaliParser();
				smaliParser.smaliReader(checkingFile.getAbsolutePath(), md5);
			}
		}
	}

}
