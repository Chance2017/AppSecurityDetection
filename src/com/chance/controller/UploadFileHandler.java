package com.chance.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chance.dao.ApkMapper;
import com.chance.entities.Apk;

@Controller
public class UploadFileHandler {
	
	final static String Apk_Path = "/home/zzh/AndroidSecurityDetection/Apks/";
	
	@RequestMapping("/uploadApk")
	@ResponseBody
	public Map<String, Object> apk(MultipartFile file){
		
		Map<String, Object> map = new HashMap<>();
		
		if(file == null)
			 return null;
		//实例化apk对象
		Apk apk = new Apk();
		//默认设置返回码为0，即成功
		apk.setCode(0);
		//利用UUID未上传的文件生成一个唯一的识别码，并作为该上传文件的文件名，防止在服务器上保存该文件时，出现文件名冲突
		String filename = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")) + "-" + UUID.randomUUID().toString() + ".apk";
		//文件扩展名
		String suffixName = null;
		
		try {
			//该目录不存在则创建
			if(!new File(Apk_Path).exists()) {
				new File(Apk_Path).mkdir();
			}
			if(file != null && !file.isEmpty()){
				file.transferTo(new File(Apk_Path + filename));
			} else {
				//上传文件为空
				apk.setCode(103);
			}
			//判断上传文件格式是否正确
			suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
			System.out.println("文件扩展名：" + suffixName);
			if(!suffixName.equals("apk")) {
				apk.setCode(104);
				System.out.println("上传文件格式不正确");
			}
			else {
				//计算文件的MD5值
				String md5 = calculateMD5(filename);
				
				apk.setFilename(filename);
				
				//MD5值计算失败
				if(md5 == null || md5 == "") {
					apk.setCode(101);
				}
				else {
					apk.setMD5(md5);
				}
				writeToDataBase(apk);
			}
		} catch(NullPointerException ne) {
			//服务器上目录存在且创建未成功
			apk.setCode(1);					
		} catch (IOException | IllegalStateException e) {
			//文件上传失败
			apk.setCode(100);
		}

		map.put("apk", apk);
		
		return map;
	}
	
	/**
	 * 计算文件的MD5值
	 * @param filename
	 * @return
	 */
	public String calculateMD5(String filename){
		String apkMD5 = null;
		File file = new File(Apk_Path  + filename);
		
		if(!file.exists()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte[] buffer = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while((len = in.read(buffer, 0, 1024)) != -1){
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			return null;
		}
		BigInteger bigInteger = new BigInteger(1, digest.digest());
		apkMD5 = bigInteger.toString(16);
				
		return apkMD5;
	}
	
	/**
	 * 将Apk文件相关信息写进数据库
	 * @param apk
	 */
	public void writeToDataBase(Apk apk) {
		
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory(apk);
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到apkMapper接口
			ApkMapper apkMapper = sqlSession.getMapper(ApkMapper.class);
			apkMapper.addApk(apk);
			System.out.println("新添加的apk文件的id：" + apk.getId());
			//提交数据
			sqlSession.commit();
			
		} catch(Exception e) {
			System.out.println("写入数据库失败：" + e.getMessage());
			apk.setCode(102);
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
	 * @param apk Apk对象
	 * @return
	 */
	public SqlSessionFactory getSqlFactory(Apk apk) {
		//MyBatis配置文件位置
		String resource = "mybatis-config.xml";
		InputStream inputStream = null;
		SqlSessionFactory sqlSessionFactory = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
			//根据配置文件创建出SqlSessionFactory，此工厂方法用于产生sql会话
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			apk.setCode(102);
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
