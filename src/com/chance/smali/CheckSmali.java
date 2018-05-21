package com.chance.smali;

import java.io.IOException;
import java.io.InputStream;
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

public class CheckSmali {

	private String  md5;
	private String 	packageName;
	private String  className;
	private String  superClassName;
	private String  methodName;
	private Integer lineNumber;
	
	private boolean[] hasRemoveJavascriptInterfaces;	//removeJavascriptInterface漏洞检测标志位
	private boolean   hasWebViewObj;					//函数中是否使用了WebView对象
	private boolean   isActivityHijack;					//判断Activity劫持标识符
	private boolean   isActivity;						//判断是否为Activity
	private boolean   isWebViewFileAccess;				//判断是否为WebView file跨域访问漏洞
	
	private JavaCodeRisk javaCodeRisk;
	
	public CheckSmali() {
		super();
		this.javaCodeRisk = new JavaCodeRisk();
	}
	
	/**
	 * 有参构造函数
	 * @param hasRemoveJavascriptInterfaces removeJavascriptInterface漏洞检测标志位
	 * @param isWebViewObj					函数中是否使用了WebView对象
	 * @param isActivityHijack				判断Activity劫持标识符
	 * @param isActivity					判断是否为Activity
	 */
	public CheckSmali(String md5, boolean[] hasRemoveJavascriptInterfaces, boolean hasWebViewObj, 
			boolean isActivityHijack, boolean isActivity, boolean isWebViewFileAccess) {
		super();
		this.md5							= md5;
		this.hasRemoveJavascriptInterfaces  = hasRemoveJavascriptInterfaces;
		this.hasWebViewObj 					= hasWebViewObj;
		this.isActivityHijack 				= isActivityHijack;
		this.isActivity 					= isActivity;
		this.isWebViewFileAccess			= isWebViewFileAccess;
		this.javaCodeRisk 					= new JavaCodeRisk();
	}
	
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public boolean[] getHasRemoveJavascriptInterfaces() {
		return hasRemoveJavascriptInterfaces;
	}

	public void setHasRemoveJavascriptInterfaces(boolean[] hasRemoveJavascriptInterfaces) {
		this.hasRemoveJavascriptInterfaces = hasRemoveJavascriptInterfaces;
	}

	public boolean isActivityHijack() {
		return isActivityHijack;
	}

	public void setActivityHijack(boolean isActivityHijack) {
		this.isActivityHijack = isActivityHijack;
	}

	public boolean isHasWebViewObj() {
		return hasWebViewObj;
	}

	public void setHasWebViewObj(boolean hasWebViewObj) {
		this.hasWebViewObj = hasWebViewObj;
	}

	public boolean isWebViewFileAccess() {
		return isWebViewFileAccess;
	}

	public void setWebViewFileAccess(boolean isWebViewFileAccess) {
		this.isWebViewFileAccess = isWebViewFileAccess;
	}

	public boolean isActivity() {
		return isActivity;
	}
	
	public void setActivity(boolean isActivity) {
		this.isActivity = isActivity;
	}
	
	/**
	 * 判断是否为Activity
	 * @return
	 */
	public void isActivity(String superClassName) {
		if(superClassName.equals("Landroid/app/Activity")) {
			setActivity(true);
		}
	}
	
	/**
	 * 检测代码中是否使用AddJavaScriptInterface()方法
	 * @param code
	 * @return
	 */
	public void findAddJavaScriptInterface(String codeLine, boolean annotated){
		//定义匹配规则，AddJavaScriptInterface()的smali语法表示为：invoke-virtual {v0, p0, v1}, Landroid/webkit/WebView;->addJavascriptInterface()V
		Pattern pattern = Pattern.compile("Landroid\\/webkit\\/WebView\\;\\-\\>addJavascriptInterface\\(.+\\)");
		Matcher matcher = pattern.matcher(codeLine);
		if(matcher.find() && !annotated) {
			
			writeToDataBase(Settings.Risk_AddJsInterface);
			
			System.out.println(packageName + ":" + className + ":" + methodName + ":" + lineNumber + ":" + "AddJavaScriptInterface");
		}
	}
	
	/**
	 * 1.检测代码中是否使用removeJavascriptInterface()方法关闭了危险接口accessibility/accessibilityTraversal/searchBoxJavaBridge_
	 * 2.检查WebView file跨域访问漏洞
	 * @param codeLine
	 * @param constMap
	 */
	public void findRemJsIntfAndFileAccess(String codeLine, Map<String, String> constMap){
		//先判断方法中是否用到WebView对象
		if(!hasWebViewObj) {
			if(codeLine.contains("Landroid/webkit/WebView"))
				hasWebViewObj = true;
		}
		//判断代码中是否未移除WebView组件系统隐藏接口漏洞
		else if(codeLine.contains("Landroid/webkit/WebView") && codeLine.contains("removeJavascriptInterface")) {
			//获得存储参数寄存器名称
			codeLine = codeLine.substring(codeLine.indexOf("{") + 1, codeLine.indexOf("}"));
			String[] splits = codeLine.split(", ");
			String register = "";
			if(splits.length >= 2)
				register = splits[1];
			//从寄存器中取出参数的值
			String parameter = constMap.get(register);
				 
			//判断是否包含以下参数
			if(parameter.equals("\"accessibility\"")){
				selectAndUpdateRiskExist(Settings.Risk_RemoveJsAccess);
				hasRemoveJavascriptInterfaces[0] = true;
				return ;
			}					   
			if(parameter.equals("\"accessibilityTraversal\"")){
				selectAndUpdateRiskExist(Settings.Risk_RemoveJsAccessTra);
				hasRemoveJavascriptInterfaces[1] = true;
				return ;
			}
			if(parameter.equals("\"searchBoxJavaBridge_\"")){
				selectAndUpdateRiskExist(Settings.Risk_RemoveJsSearchBox);
				hasRemoveJavascriptInterfaces[2] = true;
				return ;
			}
		}
		//检查WebView file跨域访问漏洞
		else if(codeLine.contains("Landroid/webkit/WebSettings") && (codeLine.contains("setAllowFileAccessFromFileURLs") || codeLine.contains("setAllowFileAccess"))) {
			codeLine = codeLine.substring(codeLine.indexOf("{") + 1, codeLine.indexOf("}"));
			String[] splits = codeLine.split(",");
			String register = "";
			if(splits.length >= 2) {
				register = splits[1].trim();
			}
			if(constMap.get(register).equals("0x0")){
				selectAndUpdateRiskExist(Settings.Risk_FileAccess);
				System.out.println("WebView file跨域访问漏洞setAllowFileAccess无漏洞");
				isWebViewFileAccess = false;
			}
		}
	}
	
	/**
	 * 检测存储风险SharedPreferences类、Internal/External Storage类、SQLiteDatabase类
	 * @param codeLine
	 * @param constMap
	 */
	public void findStoreRisk(String codeLine, Map<String, String> constMap){	
		/*定义匹配getSharedPreferences()、openOrCreateDatabase()、FileOutputStream()三个方法的正则表达式
		  匹配规则：->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;
		  Landroid/content/Context;->openOrCreateDatabase(Ljava/lang/String)
		  ->openFileOutput(Ljava/lang/String;I)Ljava/io/FileOutputStream;*/
		Pattern[] patterns = {	
								Pattern.compile("\\;\\-\\>getSharedPreferences\\(.+\\)Landroid\\/content\\/SharedPreferences"),
								Pattern.compile("Landroid\\/content\\/Context\\;\\-\\>openOrCreateDatabase\\(.+\\)"),
								Pattern.compile("\\-\\>openFileOutput\\(.+\\)Ljava\\/io\\/FileOutputStream\\;")
							 };
		for(int i = 0 ; i < 3 ; i ++){
			Matcher matcher = patterns[i].matcher(codeLine);
			if(matcher.find()){
				//如果找到getSharedPreferences()方法，获取方法的参数
				String[] splits = codeLine.split(" ");
				//从寄存器中取出参数的值
				String register = "";
				if(splits.length >= 4)
					register = splits[3].substring(0,2);
				String parameter = "";
				if(constMap.get(register) != null)
					parameter = constMap.get(register);
				//判断参数的值是否是MODE_WORLD_READABLE或MODE_WORLD_WRITEABLE
				if(parameter.equals("0x1") || parameter.equals("0x2")){
					switch (i) {
						case 0:
							writeToDataBase(Settings.Risk_SharedPreferences);
							System.out.println(packageName + ":" + className + ":" + methodName + ":" + lineNumber + ":getSharedPreferences");
							break;
						case 1:
							writeToDataBase(Settings.Risk_OpenOrCreateDb);
							System.out.println(packageName + ":" + className + ":" + methodName + ":" + lineNumber + ":openOrCreateDatabase");
							break;
						case 2:
							writeToDataBase(Settings.Risk_FileOutputStream);
							System.out.println(packageName + ":" + className + ":" + methodName + ":" + lineNumber + ":FileOutputStream");
							break;
						default:
							break;
					}
				}
			}
		}
	}
	
	/**
	 * 检测WebView密码明文保存漏洞
	 * @param codeLine
	 * @param constMap
	 */
	public void WebViewPassword(String codeLine, Map<String, String> constMap){
		String constName = "";
		if(codeLine.contains("setSavePassword") && codeLine.contains("Landroid/webkit/WebSettings")){
			String[] split = codeLine.split(" ");
			if(split.length >= 3) {
				constName = split[2].substring(0, 2);
				if(constMap.get(constName).equals("0x1")){
					writeToDataBase(Settings.Risk_SavePassword);
					System.out.println(this.packageName + ":" + className + ":" + this.methodName + ":" + this.lineNumber + ":" + "这有setSavePassword漏洞");
				}
			}
		}
	}
	
	/**
	 * 检测Activity劫持漏洞
	 * @param codeLine
	 */
	public void ActivityHijack(String codeLine){
		if(isActivity) {
			if(methodName.equals("onStop") || methodName.equals("onPause")) {
				if(codeLine.contains("getShortClassName") || codeLine.contains("RunningTaskInfo")){
					//这个标注位是判断是否有activity劫持发生的，false表示劫持被阻止了，true表示可能被劫持
					isActivityHijack = false;
				}
			}
		}
	}
	
	/**
	 * 检测数据库注入漏洞
	 * @param codeLine
	 * @param constMap
	 */
	public void DatabaseInject(String codeLine, Map<String, String> constMap){
		if(codeLine.contains("->rawQuery") || codeLine.contains("->execSQL"))
		{
			if(!superClassName.equals("Landroid/database/sqlite/SQLiteOpenHelper;"))
			{
				codeLine = codeLine.substring(codeLine.indexOf("{") + 1, codeLine.indexOf("}"));
				String[] splits = codeLine.split(",");
				String register = "";
				if(splits.length >= 2) {
					register = splits[1].trim();
				}
				if(constMap.get(register) != null && constMap.get(register).contains("?")) {
					writeToDataBase(Settings.Risk_DbInject);
					System.out.println(this.packageName + ":" + className + ":" + this.methodName + ":" + this.lineNumber + ":" + "有数据库注入的漏洞");
				}
			}			
		}		
	}
	
	/**
	 * 检测HTTPS证书未校验漏洞
	 * @return
	 */
	public void HttpsCertNotVerify(String line, Map<String, String> constMap, int num){
		
		if(line.contains("Lorg/apache/http/conn/ssl/SSLSocketFactory;->ALLOW_ALL_HOSTNAME_VERIFIER")){
			writeToDataBase(Settings.Risk_HttpsCertNotVerify);
			System.out.println("有未校验Https证书漏洞");
		}
		//判断方法名是否为checkServerTrusted
		else if(methodName.equals("checkServerTrusted")) {	
			if(line.contains("return-void") && num == 1) {
				writeToDataBase(Settings.Risk_HttpsCertNotVerify);
				System.out.println("在"+methodName+"方法中存在未校验Https证书漏洞");
			}
		}
		//判断方法名是否为verify
		else if(methodName.equals("verify")) {
			if(line.contains("return") && num == 1) {
				if(line.split(" ").length >= 2) {
					if(constMap.get(line.split(" ")[1]).equals("0x1")) {
						writeToDataBase(Settings.Risk_HttpsCertNotVerify);
						System.out.println("在"+methodName+"方法中存在未校验Https证书漏洞");
					}
				}
			}
		}
		//判断方法名是否为onReceivedSslError
		else if(methodName.equals("onReceivedSslError")) {
			if(line.contains("Landroid/webkit/SslErrorHandler;->proceed()")) {
				writeToDataBase(Settings.Risk_HttpsCertNotVerify);
				System.out.println("在"+methodName+"方法中存在对不安全的Https证书不做处理的漏洞");
			}
		}
	}
	
	/**
	 * 查询并更新或保存相关信息
	 * @param riskname
	 */
	public void selectAndUpdateRiskExist(String riskname) {
		javaCodeRisk.setMD5(md5);
		javaCodeRisk.setRiskName(riskname);
		List<JavaCodeRisk> risks = getRisks(md5, riskname);
		if(risks.size() != 0) {
			boolean finded = false;
			for (JavaCodeRisk risk : risks) {
				if(risk.getPackageName().equals(packageName) && risk.getClassName().equals(className)) {
					javaCodeRisk = risk;
					finded = true;
					break;
				}
			}
			if(finded == true) {
				updateRiskExist(javaCodeRisk);
			} else {
				javaCodeRisk.setRiskExist(false);
				writeToDataBase(riskname);
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
	 * @param riskName 风险名称
	 */
	public void writeToDataBase(String riskName) {
		
		javaCodeRisk.setMD5(md5);
		javaCodeRisk.setRiskName(riskName);
		javaCodeRisk.setPackageName(packageName);
		javaCodeRisk.setClassName(className);
		javaCodeRisk.setSuperClassName(superClassName);
		javaCodeRisk.setMethodName(methodName);
		javaCodeRisk.setLineNumber(lineNumber);
		
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
	 * 更新数据库risk_exist字段
	 */
	public void updateRiskExist(JavaCodeRisk javaCodeRisk) {
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
		
		SqlSession sqlSession = null;
		
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到javaCodeRiskMapper接口
			JavaCodeRiskMapper javaCodeRiskMapper = sqlSession.getMapper(JavaCodeRiskMapper.class);
			javaCodeRiskMapper.updateRiskExist(javaCodeRisk);
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
