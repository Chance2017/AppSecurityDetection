package com.chance.smali;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSmali {
	public static boolean[] JavascriptInterfaces=new boolean[3];
	public static boolean WebViewObj=false;//函数中是否使用了WebView对象
	/**
	 * 检测代码中是否使用AddJavaScriptInterface()方法
	 * @param code
	 * @return
	 */
	public static boolean findAddJavaScriptInterface(String codeLine){
		//定义匹配规则，AddJavaScriptInterface()的smali语法表示为：invoke-virtual {v0, p0, v1}, 
		//Landroid/webkit/WebView;->addJavascriptInterface()V
		Pattern pattern =Pattern.compile("Landroid\\/webkit\\/WebView\\;\\-\\>addJavascriptInterface\\(.+\\)");
		Matcher matcher =pattern.matcher(codeLine);
		if(matcher.find()){
			return true;
		}			
		else return false;
	}
	/**
	 * 检测代码中是否使用removeJavascriptInterface()方法关闭了危险接口
	 * @param code
	 * @return
	 */
	public static void findremoveJavascriptInterface(String codeLine, Map<String, String> constMap){
		//先判断方法中是否用到WebView对象
		if(!WebViewObj){
			if(codeLine.contains("Landroid/webkit/WebView"))
				WebViewObj = true;
		}
		else{
			//判断代码中是否有removeJavascriptInterface()方法
			if(codeLine.contains("Landroid/webkit/WebView")&&codeLine.contains("removeJavascriptInterface")){
				 String[] spilts = codeLine.split(" ");
				 //从寄存器中取出参数的值
				 String  register = spilts[2].substring(0,2);
				 String parameter = constMap.get(register);
				 if(parameter.contains("accessibilityTraversal")){
					 JavascriptInterfaces[1] = true;	
					 return ;
				 }
				 if(parameter.contains("accessibility")){
					 JavascriptInterfaces[0] = true;	
					 return ;
				 }
				 if(parameter.contains("searchBoxJavaBridge_")){
					 JavascriptInterfaces[2] = true;
					 return ;
				 }
			}
		}

	}
	/**
	 * 检测存储风险SharedPreferences类、Internal/External Storage类、SQLiteDatabase类
	 * @param code
	 * @return
	 */
	public static int findStoreRisk(String codeLine, Map<String, String> constMap){	
		/*定义匹配getSharedPreferences()、openOrCreateDatabase()、FileOutputStream()三个方法的正则表达式
		 * 匹配规则：->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;
		 * Landroid/content/Context;->openOrCreateDatabase(Ljava/lang/String)
		 * ->openFileOutput(Ljava/lang/String;I)Ljava/io/FileOutputStream;
		*/
		Pattern[] patterns = {Pattern.compile("\\;\\-\\>getSharedPreferences\\(.+\\)Landroid\\/content\\/SharedPreferences"),
							Pattern.compile("Landroid\\/content\\/Context\\;\\-\\>openOrCreateDatabase\\(.+\\)"),
							Pattern.compile("\\-\\>openFileOutput\\(.+\\)Ljava\\/io\\/FileOutputStream\\;")
							};
		for(int i = 0 ; i < 3 ; i ++){
			Matcher matcher = patterns[i].matcher(codeLine);
			if(matcher.find()){
				//如果找到getSharedPreferences()方法，获取方法的参数
				String[] spilts = codeLine.split(" ");
				//从寄存器中取出参数的值
				String register = spilts[3].substring(0,2);
				String parameter = constMap.get(register);
				//判断参数的值是否是MODE_WORLD_READABLE或MODE_WORLD_WRITEABLE
				if(parameter.equals("0x1") || parameter.equals("0x2")){
					return i + 1;
				}
			}		
		}		
		return 0;	
	}
	/**
	 * 检查WebView file跨域访问漏洞
	 * @param line
	 * @param constMap
	 */
	public static void WebViewFile(String codeline,Map<String, String> constMap){
		String constName = "";
		if(codeline.contains("setAllowFileAccessFromFileURLs")){
			String[] split = codeline.split(" ");
			constName = split[2].substring(0, 2);
			if(constMap.get(constName).equals("0x0")){
				System.out.println("这有setAllowFileAccessFromFileURLs漏洞");
			}
		}
	}
	/**
	 * 检测WebView密码明文保存漏洞
	 * @param line
	 * @param constMap
	 */
	public static void WebViewPassword(String line,Map<String, String> constMap){
		String constName="";
		if(line.contains("setSavePassword")&&line.contains("Landroid/webkit/WebSettings")){
			String[] split=line.split(" ");
			constName=split[2].substring(0, 2);
			if(constMap.get(constName).equals("0x1")){
				System.out.println("这有setSavePassword漏洞");
			}
		}
	}
	/**
	 * 检测Activity劫持漏洞
	 * @param line
	 * @param method
	 * @param isActivityhijack
	 */
	public static void ActivityHijack(String line, String method, boolean isActivityhijack){
		if(method.equals("onStop") || method.equals("onPause")){
			if(line.contains("getShortClassName") || line.contains("RunningTaskInfo")){
				//这个标注位是判断是否有activity劫持发生的，false表示劫持被阻止了，true表示可能被劫持
				isActivityhijack = false;
			}
		}
	}
	/**
	 * 检测数据库注入漏洞
	 * @param line
	 * @param father
	 */
	public static void DatabaseInject(String line, String father){
		if(line.contains("->rawQuery") || line.contains("->execSQL"))
		{
			if(!father.equals("Landroid/database/sqlite/SQLiteOpenHelper;"))
			{
			    System.out.println("有数据库注入的漏洞");
			}			
		}		
	}
}
