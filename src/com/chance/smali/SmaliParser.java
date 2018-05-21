package com.chance.smali;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.chance.dao.JavaCodeRiskMapper;
import com.chance.entities.JavaCodeRisk;
import com.chance.entities.Settings;

public class SmaliParser {
	
	JavaCodeRisk javaCodeRisk = new JavaCodeRisk();
	String md5 				  = "";
	String packageName 		  = "";		//用于存放包名
	String className 		  = "";		//用于存放类名
	String superClassName 	  = "";		//定义字符串存放父类名
	
	public void smaliReader(String filePath, String md5) {
		
		this.md5 = md5;
		
		int lineNumber 				  = 0;
		FileReader fileReader 		  = null;
		BufferedReader bufferedReader = null;
		Map<String, String> constMap  = new HashMap<String, String>();	//记录所有变量
		
		CheckSmali checkSmali 		  = new CheckSmali(md5, new boolean[3], false, true, false, true);
		
		Pattern pattern 			  = null;
		Matcher matcher 			  = null;
		boolean annotated 			  = false;	//是否加入 @SuppressLint({ "JavascriptInterface" })的标志
		
		try {
			fileReader 		= new FileReader(filePath);
			bufferedReader 	= new BufferedReader(fileReader);
			String line 	= null;
			int num			= 0;	//用于统计方法体中的代码行数
			
			//按行循环读取文件
			while((line = bufferedReader.readLine()) != null) {
				line = line.trim();				
				if(line.length() != 0) {
					//读取方法名					
					if(line.contains(".method")) {
						
						//获取方法名
						String methodName = "";
						//匹配规则：空格符+0/多个字符+(
						//.method protected onDestroy()V
						pattern = Pattern.compile("\\s\\w*\\(");
						matcher = pattern.matcher(line);
						if(matcher.find()) {
							methodName = matcher.group(0).substring(0, matcher.group(0).lastIndexOf("(")).trim();
						}
						
						line = bufferedReader.readLine().trim();
						while(!line.contains(".end method")) {
							
							//保存读取到的寄存器中的常量，若相同则覆盖原值
							saveConst(line, constMap);

							num = 0;
							
							//按源java程序中的行号读取
							if(line.contains(".line ")) {
								num ++;
								String[] split = line.split(" ");
								if(split.length >= 2)
									lineNumber = Integer.parseInt(split[1]);
								while(!(line = bufferedReader.readLine().trim()).contains(".line")) {
									
									//保存读取到的寄存器中的常量，若相同则覆盖原值
									saveConst(line, constMap);
									
									if(line.contains(".end method"))
										break;
									else if(line.length() != 0) {
										
										checkSmali.setMethodName(methodName);
										checkSmali.setLineNumber(lineNumber);
										
										//检测代码中是否使用AddJavaScriptInterface()方法
										checkSmali.findAddJavaScriptInterface(line, annotated);
										//检测代码中是否使用removeJavascriptInterface()方法关闭了危险接口&检查WebView file跨域访问漏洞
										checkSmali.findRemJsIntfAndFileAccess(line, constMap);
						                //检测是否出现存储漏洞
										checkSmali.findStoreRisk(line, constMap);
										//检测密码明文保存漏洞
										checkSmali.WebViewPassword(line, constMap);
										//检测Activity劫持漏洞
										checkSmali.ActivityHijack(line);
										//检测数据库注入漏洞
										checkSmali.DatabaseInject(line, constMap);
										//检测HTTPS证书未校验漏洞
										checkSmali.HttpsCertNotVerify(line, constMap, num);
									}
								}
							}
							//读取方法上的注解
							else if(line.contains(".annotation")) {
								// 检测代码中是否加入@SuppressLint({ "JavascriptInterface" })注解
								while(!(line = bufferedReader.readLine().trim()).equals(".end annotation")){
									if(line.contains("JavascriptInterface")){
										//找到 @SuppressLint({ "JavascriptInterface" })注解
										annotated=true;
									}
								}							      
							}
							else {
								line = bufferedReader.readLine().trim();
							}
						}
					}
					//读取类上的注解
					else if(line.contains(".annotation")) {								
						// 检测代码中是否加入@SuppressLint({ "JavascriptInterface" })注解
						while(!(line = bufferedReader.readLine().trim()).equals(".end annotation")){
							if(line.contains("JavascriptInterface")){
								//找到 @SuppressLint({ "JavascriptInterface" })注解
								annotated=true;
							}
						}	
					 }
					//获取父类
					else if(line.contains(".super")) {
						superClassName = line.substring(line.indexOf("L"), line.indexOf(";"));
						checkSmali.setSuperClassName(superClassName);
						checkSmali.isActivity(superClassName);
					}
					//获取包名
					else if(line.contains(".class")) {
						if(line.lastIndexOf("/") != -1)
							packageName = line.substring(line.indexOf("L") + 1, line.lastIndexOf("/"));
						else
							packageName = line.substring(line.indexOf("L") + 1, line.indexOf(";"));
						checkSmali.setPackageName(packageName);
					}
					//获取类名
					else if(line.contains(".source")) {
						className = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
						checkSmali.setClassName(className);
					}
			    }
			}
			if(checkSmali.isActivity() && checkSmali.isActivityHijack()) {
				writeToDataBase(Settings.Risk_ActivityHijack);
				System.out.println("存在Activity劫持");
			}
			if(checkSmali.isHasWebViewObj()) {
				if(!checkSmali.getHasRemoveJavascriptInterfaces()[0]) {
					saveRiskBySingleClass(Settings.Risk_RemoveJsAccess);
					System.out.println("WebView的危险接口accessibility未关闭");
				}
				if(!checkSmali.getHasRemoveJavascriptInterfaces()[1]) {
					saveRiskBySingleClass(Settings.Risk_RemoveJsAccessTra);
					System.out.println("WebView的危险接口accessibilityTraversal未关闭");						
				}
				if(!checkSmali.getHasRemoveJavascriptInterfaces()[2]) {
					saveRiskBySingleClass(Settings.Risk_RemoveJsSearchBox);
					System.out.println("WebView的危险接口searchBoxJavaBridge_未关闭");
				}
				if(checkSmali.isWebViewFileAccess()) {
					saveRiskBySingleClass(Settings.Risk_FileAccess);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fileReader != null)
					fileReader.close();
				if(bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 统计方法中所有的常量，保存到map中
	 * @param line 待检测的行
	 */
	public void saveConst(String line, Map<String, String> constMap) {
		
		//记录常量名
		String constName = "";
		//记录常量值
		String constValue = null;
		//匹配const*v
		Pattern pattern = Pattern.compile("^const.*[v|p]");
		Matcher matcher = pattern.matcher(line);
		if(matcher.find()) {
			 String[] split = line.split(" ");
			 constName = split[1].substring(0, 2);
			 constValue = split[split.length-1];
		     constMap.put(constName, constValue);
//		     System.out.println("寄存器"+constName+"="+constValue);
		}
	}
	
	/**
	 * 检测同一个风险对于同一个类文件是否已经在数据库中存储过
	 * 如果已经存储过则不再存储,否则，向数据库中存储
	 * @param riskName
	 */
	public void saveRiskBySingleClass(String riskName) {
		javaCodeRisk.setMD5(md5);
		javaCodeRisk.setRiskName(riskName);
		List<JavaCodeRisk> risks = getRisks(md5, riskName);
		if(risks.size() == 0) {
			writeToDataBase(riskName);
		} else {
			boolean finded = false;
			for (JavaCodeRisk risk : risks) {
				if(risk.getPackageName().equals(packageName) && risk.getClassName().equals(className)) {
					finded = true;
					break;
				}
			}
			if(finded == false) {
				writeToDataBase(riskName);
			}
		}
	}
	
	/**
	 * 根据MD5值和风险名称查询相关信息
	 * @param javaCodeRisk
	 * @return
	 */
	public List<JavaCodeRisk> getRisks(String md5, String riskname) {
		
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
		
		SqlSession sqlSession = null;
		List<JavaCodeRisk> javaCodeRisksByMD5AndRiskname = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到apkMapper接口
			JavaCodeRiskMapper javaCodeRiskMapper = sqlSession.getMapper(JavaCodeRiskMapper.class);
			javaCodeRisksByMD5AndRiskname = javaCodeRiskMapper.selectJavaCodeRisksByMD5AndRiskname(md5, riskname);
			
			//提交数据
			sqlSession.commit();
			
		} catch(Exception e) {
			System.out.println("判断MD5值时，读取数据库失败：" + e.getMessage());
			//如果数据库写入出现异常，则回滚
			if(sqlSession!=null)
				sqlSession.rollback();
		} finally {
			//关闭资源
			if(sqlSession!=null)
				sqlSession.close();
		}
		
		return javaCodeRisksByMD5AndRiskname;
	}
	
	/**
	 * 将相关信息写进数据库
	 */
	public void writeToDataBase(String riskName) {
		
		javaCodeRisk.setMD5(md5);
		javaCodeRisk.setRiskName(riskName);
		javaCodeRisk.setPackageName(packageName);
		javaCodeRisk.setClassName(className);
		javaCodeRisk.setSuperClassName(superClassName);
		
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到javaCodeRiskMapper接口
			JavaCodeRiskMapper javaCodeRiskMapper = sqlSession.getMapper(JavaCodeRiskMapper.class);
			javaCodeRiskMapper.addJavaCodeRisk(javaCodeRisk);
			//提交数据
			sqlSession.commit();
			
		} catch(Exception e) {
			System.out.println("写入数据库失败：" + e.getMessage());
			//如果数据库写入出现异常，则回滚
			if(sqlSession!=null)
				sqlSession.rollback();
		} finally {
			//关闭资源
			if(sqlSession!=null)
				sqlSession.close();
		}
	}
	
	/**
	 * 用于产生SqlSession工厂
	 * @return
	 */
	public SqlSessionFactory getSqlFactory() {
		//MyBatis配置文件位置
		String resource = "mybatis-config.xml";
		InputStream inputStream = null;
		SqlSessionFactory sqlSessionFactory = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
			//根据配置文件创建出SqlSessionFactory，此工厂方法用于产生sql会话
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("SqlSessionFactory创建失败");
		} finally {
			try {
				if(inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println("输入流关闭失败");
			}
		}
		
		return sqlSessionFactory;
	}
	
}
