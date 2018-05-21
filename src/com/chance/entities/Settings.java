package com.chance.entities;

public class Settings {
	/**
	 * 基础路径
	 */
	public final static String Base_Path 			= "/home/zzh/AndroidSecurityDetection/";
	/**
	 * 反编译工具地址
	 */
	public final static String Apk_Tool_Path 		= Base_Path + "ApkDecompileTools/apktool_2.2.2.jar";
	/**
	 * Apk文件位置
	 */
	public final static String Apk_File_Path 		= Base_Path + "Apks/";
	/**
	 * 反编译后输出路径
	 */
	public final static String Decompile_Out_Path 	= Base_Path + "Apks_Decompile/";
	
	/**
	 * addJavaScriptInterface风险名称
	 */
	public final static String Risk_AddJsInterface 	   = "AddJsInterface";
	/**
	 * removeJavascriptInterface-接口accessibility未关闭风险名称
	 */
	public final static String Risk_RemoveJsAccess     = "RemoveJsAccess";
	/**
	 * removeJavascriptInterface-接口accessibilityTraversal未关闭风险名称
	 */
	public final static String Risk_RemoveJsAccessTra  = "RemoveJsAccessTra";
	/**
	 * removeJavascriptInterface-接口searchBoxJavaBridge_未关闭风险名称
	 */
	public final static String Risk_RemoveJsSearchBox  = "RemoveJsSearchBox";
	/**
	 * WebView file跨域访问风险名称
	 */
	public final static String Risk_FileAccess 		   = "FileAccess";
	/**
	 * getSharedPreferences存储风险名称
	 */
	public final static String Risk_SharedPreferences  = "SharedPreferences";
	/**
	 * openOrCreateDatabase存储风险名称
	 */
	public final static String Risk_OpenOrCreateDb 	   = "OpenOrCreateDb";
	/**
	 * FileOutputStream存储风险名称
	 */
	public final static String Risk_FileOutputStream   = "FileOutputStream";
	/**
	 * WebView密码明文保存风险名称
	 */
	public final static String Risk_SavePassword 	   = "SavePassword";
	/**
	 * Activity劫持风险名称
	 */
	public final static String Risk_ActivityHijack	   = "ActivityHijack";
	/**
	 * 数据库注入风险名称
	 */
	public final static String Risk_DbInject 		   = "DbInject";
	/**
	 * WebView Https证书未校验风险名称
	 */
	public final static String Risk_HttpsCertNotVerify = "HttpsCertNotVerify";
}
