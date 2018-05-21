package com.chance.controller;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ManifestDetectionHandler {
	
	SAXReader reader = new SAXReader();
	
	@RequestMapping(value = "/manifestDetection", produces="text/html;charset=utf-8")
	@ResponseBody
	public String manifestDetection(String MD5){
		Document document;
		String exportedText = null;
		String protectionLevelText = null;
		
		String responseText = "<html><head><title>Apk安全检测结果</title></head>"
							+ "<body marginheight='50px' marginwidth='50px'><h1>返回结果</h1><hr>"
							+ "<table border='2px' bordercolor='orange' align='center'><tr><th style='padding:10px'>属性</th><th style='padding:10px'>值</th></tr>";
		
		try {
			document = reader.read(new File("D:\\AndroidSecurityDetection\\Apks_Decompile\\" + MD5 + "\\AndroidManifest.xml"));
			Element root = document.getRootElement();
			Element application = root.element("application");
			
			//检测sharedUserId属性
			String sharedUserId = root.attributeValue("sharedUserId");
			responseText += "<tr><td align='center' style='padding:10px'><h2>sharedUserId</h2></td><td align='center' style='padding:10px'><h2>" + sharedUserId + "</h2></td></tr>";
			
			//检测allowBackUp属性
			String allowBackup = application.attributeValue("allowBackup");
			responseText += "<tr><td align='center' style='padding:10px'><h2>allowBackup</h2></td><td align='center' style='padding:10px'><h2>" + allowBackup + "</h2></td></tr>";
			
			//检测debuggable属性
			String debuggable = application.attributeValue("debuggable");
			responseText += "<tr><td align='center' style='padding:10px'><h2>debuggable</h2></td><td align='center' style='padding:10px'><h2>" + debuggable + "</h2></td></tr></body></html>";
			
			//检测所有的protectionLevel属性
			@SuppressWarnings("unchecked")
			List<Element> permissionElements = root.elements("permission");
			System.out.println(permissionElements.size());
			for (Element element : permissionElements) {
				protectionLevelText = element.attributeValue("protectionLevel");
				if(!protectionLevelText.equals("signature") && !protectionLevelText.equals("signatureOrSystem")) {
					System.out.println(protectionLevelText);
					responseText += "<tr><td align='center' style='padding:10px'><h2>protectionLevel</h2></td><td align='center' style='padding:10px'><h2>存在风险</h2></td></tr></body></html>";
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
					responseText += "<tr><td align='center' style='padding:10px'><h2>exported</h2></td><td align='center' style='padding:10px'><h2>true</h2></td></tr></body></html>";
					break;
				}
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return responseText;
	}
}
