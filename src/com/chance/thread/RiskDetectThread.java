package com.chance.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.chance.entities.ApkInfo;
import com.chance.entities.BufferReadSuccess;
import com.chance.entities.RisksCategories;
import com.chance.entities.Settings;
import com.chance.service.DatabaseManipulation;
import com.chance.service.RisksDetection;

public class RiskDetectThread extends Thread {
	private String filename;
	private String md5;
	private RisksCategories risksCategories;
	
	public RiskDetectThread() {
		super();
	}
	public RiskDetectThread(String filename, String md5, RisksCategories risksCategories) {
		super();
		this.filename = filename;
		this.md5 = md5;
		this.risksCategories = risksCategories;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public RisksCategories getRisksCategories() {
		return risksCategories;
	}
	public void setRisksCategories(RisksCategories risksCategories) {
		this.risksCategories = risksCategories;
	}
	
	@Override
	public void run() {
		
		ApkInfo apkInfo = new ApkInfo();
		
		//对apk文件反编译，反编译后的文件名设置为其MD5值
		boolean decompiled = decompile(filename, md5);
		//如果反编译成功
		if(decompiled) {
			
			apkInfo.setMd5(md5);
			//获取应用程序版本号
			String version = getVersion(md5, "UTF-8");
			apkInfo.setVersion(version);
			//获取包名及应用程序名
			Map<String, String> appAndPackageNameMap = getAppAndPackageName(md5);
			apkInfo.setPackageName(appAndPackageNameMap.get("packageName"));
			apkInfo.setAppName(appAndPackageNameMap.get("appName"));
			//将apkInfo对象写入数据库
			DatabaseManipulation.writeToDataBase(apkInfo);
			
			DatabaseManipulation.updateTaskStatus(201, "文件反编译完成", 2, md5);
			DatabaseManipulation.updateFinished(1, md5);
			
			RisksDetection risksDetection = new RisksDetection();
			if(risksCategories.isManifestRisks()) {
				risksDetection.manifestRisksDetect(md5);
				DatabaseManipulation.updateFinished(2, md5);
			}
			risksDetection.smaliRisksDetect(md5);
			DatabaseManipulation.updateFinished(risksCategories.risksCount() + 1, md5);
			DatabaseManipulation.updateTaskStatus(0, "任务完成", 3, md5);
		} else {
			DatabaseManipulation.updateTaskStatus(105, "文件反编译失败", 4, md5);
		}
	}
	
	/**
	 * 对Apk文件进行反编译
	 * @param filename	用Apk文件的MD5值作为文件名
	 * @return 返回反编译操作是否成功
	 */
	public boolean decompile(String filename, String md5) {
		
		System.out.println("\n开始执行反编译过程\n");
		
		boolean decompileSuccess = false;
		
		Runtime rt = Runtime.getRuntime();
		Process p = null;
		
		//apk文件文件位置
		String apkPath = Settings.Apk_File_Path + filename;
		//反编译后输出路径
		String outputPath = Settings.Decompile_Out_Path + md5;
		//反编译控制台命令
		String cmd = "java -jar " + Settings.Apk_Tool_Path + " d -f " + apkPath + " -o " + outputPath;
		
		try {
			p = rt.exec(cmd);
			
			decompileSuccess = BufferReader(p, "decompile");
			
			p.waitFor();
			p.destroy();
			
		} catch (Exception e) {
			decompileSuccess = false;
			System.out.println(e.getMessage());
			try {
				p.getInputStream().close();
				p.getOutputStream().close();
				p.getErrorStream().close();
			} catch (Exception e2) {
				System.out.println(e2.toString());
			}
		}
		
		return decompileSuccess;
	}
	
	/**
	 * 获取应用程序版本号（反编译后在apktool.yml文件中）
	 * @param filename	文件名，用户获得文件路径
	 * @param charset	编码方式
	 * @return
	 */
	public String getVersion(String filename, String charset) {
		
		String versionName = null;
		
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(Settings.Decompile_Out_Path + filename + "/apktool.yml", "r");
            long fileLength = rf.length();
            long start = rf.getFilePointer();// 返回此文件中的当前偏移量
            long readIndex = start + fileLength -1;
            String line;
            rf.seek(readIndex);// 设置偏移量为文件末尾
            int c = -1;
            while (readIndex > start) {
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (line != null) {
                        if(line.contains("versionName")) {
                        	versionName = line.split(":")[1].replace("'", "").replace("\"", "").replace(" ", "");
                        	break;	//找到后就停止遍历，返回结果
                        }
                    }
                    readIndex--;
                }
                readIndex--;
                rf.seek(readIndex);
            }
        } catch (FileNotFoundException e) {
        	System.out.println("apktool.yml文件未找到");
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println("版本名读取失败");
            e.printStackTrace();
        } finally {
            try {
                if (rf != null)
                    rf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return versionName;
	}
	
	/**
	 * 获取应用程序名称和包名(反编译后，在AndroidManifest.xml文件中)
	 * @param filename 文件名，用于获取文件路径
	 */
	public Map<String, String> getAppAndPackageName(String filename) {
		Map<String, String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File(Settings.Decompile_Out_Path + filename + "/AndroidManifest.xml"));
			Element root = document.getRootElement();
			//获取包名
			String packageName = root.attributeValue("package");
			map.put("packageName", packageName);
			System.out.println("包名：" + packageName);
			//获取应用程序名称
			String appName = root.element("application").attributeValue("label");
			System.out.println(appName);
			if(appName.split("/").length > 1 && appName.split("/")[0].equals("@string")) {
				document = reader.read(new File(Settings.Decompile_Out_Path + filename + "/res/values/strings.xml"));
				root = document.getRootElement();
				String name = "string[@name='" + appName.split("/")[1] + "']";
				Node node = root.selectSingleNode(name);

				map.put("appName", node.getText());
			} else {
				map.put("appName", appName);
			}
		} catch (DocumentException e) {
			System.out.println("xml文件读写错误");
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * 将p进程在缓冲区中产生的数据读取出去，以防进程阻塞
	 * @param p 进程
	 * @param name 名称
	 */
	public boolean BufferReader(Process p, String name) {
		
		long startTime = System.currentTimeMillis();
		
		final InputStream inputStream = p.getInputStream();
		final InputStream errorStream = p.getErrorStream();
		
		BufferReadSuccess success = new BufferReadSuccess(true);
		
		//读取标准输出流
		Thread inputThread = new InputThread(success, inputStream);
		
		//读取标准错误流
		Thread errorThread = new ErrorThread(success, errorStream);
		
		//启动两个线程，使其并发执行
		inputThread.start();
		errorThread.start();
		
		//等待线程执行完成
		try {
			inputThread.join();
			inputThread.join();
		} catch (InterruptedException e) {
			System.out.println("1." + e.getMessage());
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println(name + ":控制台输出流读取时间：" + (endTime - startTime) + "ms");
		
		return success.isSuccess();
	}

}
