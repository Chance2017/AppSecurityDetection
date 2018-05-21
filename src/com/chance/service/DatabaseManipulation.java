package com.chance.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.chance.dao.ApkInfoMapper;
import com.chance.dao.JavaCodeRiskMapper;
import com.chance.dao.ManifestRiskMapper;
import com.chance.dao.TaskStatusMapper;
import com.chance.entities.ApkInfo;
import com.chance.entities.JavaCodeRisk;
import com.chance.entities.ManifestRisk;
import com.chance.entities.TaskStatus;

public class DatabaseManipulation {
	
	/**
	 * 用于产生SqlSession工厂
	 * @return
	 */
	public static SqlSessionFactory getSqlFactory() {
		InputStream inputStream = null;
		SqlSessionFactory sqlSessionFactory = null;
		try {
			inputStream = Resources.getResourceAsStream("mybatis-config.xml");
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
	
	/**
	 * 将任务状态信息写进数据库
	 * @param taskStatus 任务状态信息
	 */
	public static void writeToDataBase(TaskStatus taskStatus) {
		
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到TaskStatusMapper接口
			TaskStatusMapper taskStatusMapper = sqlSession.getMapper(TaskStatusMapper.class);
			taskStatusMapper.addTaskStatus(taskStatus);
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
	 * 将Apk文件相关信息写进数据库
	 * @param apkInfo
	 */
	public static void writeToDataBase(ApkInfo apkInfo) {
		
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到apkMapper接口
			ApkInfoMapper apkInfoMapper = sqlSession.getMapper(ApkInfoMapper.class);
			apkInfoMapper.addApkInfo(apkInfo);
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
	 * 将AndroidManifest.xml检测信息写进数据库
	 * @param manifestDetection
	 */
	public static void writeToDataBase(ManifestRisk manifestRisk) {
		
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到apkMapper接口
			ManifestRiskMapper manifestDetectionMapper = sqlSession.getMapper(ManifestRiskMapper.class);
			manifestDetectionMapper.addManifestRisk(manifestRisk);
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
	 * 更新任务状态数据库的状态信息
	 * @param code
	 * @param message
	 * @param status
	 * @param md5
	 */
	public static void updateTaskStatus(int code, String message, int status, String md5) {
		
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到TaskStatusMapper接口
			TaskStatusMapper taskStatusMapper = sqlSession.getMapper(TaskStatusMapper.class);
			taskStatusMapper.updateCodeMessageAndStatus(code, message, status, md5);
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
	 * 更新检查点总数
	 * @param total
	 * @param md5
	 */
	public static void updateTotal(int total, String md5) {
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到TaskStatusMapper接口
			TaskStatusMapper taskStatusMapper = sqlSession.getMapper(TaskStatusMapper.class);
			taskStatusMapper.updateTotal(total, md5);
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
	 * 更新已完成点数
	 * @param finished
	 * @param md5
	 */
	public static void updateFinished(int finished, String md5) {
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
				
		SqlSession sqlSession = null;
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到TaskStatusMapper接口
			TaskStatusMapper taskStatusMapper = sqlSession.getMapper(TaskStatusMapper.class);
			taskStatusMapper.updateFinished(finished, md5);
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
	 * 获取任务状态信息
	 * @param md5
	 */
	public static TaskStatus getTaskStatus(String md5) {
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
		SqlSession sqlSession = null;
		
		TaskStatus taskStatus = null;
		
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到TaskStatusMapper接口
			TaskStatusMapper taskStatusMapper = sqlSession.getMapper(TaskStatusMapper.class);
			taskStatus = taskStatusMapper.selectTaskStatus(md5);
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
		
		return taskStatus;
	}
	
	/**
	 * 获取高危参数配置风险
	 * @param md5
	 * @return
	 */
	public static ManifestRisk getManifestRisk(String md5) {
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
		SqlSession sqlSession = null;
		
		ManifestRisk manifestRisk = null;
		
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到ManifestRiskMapper接口
			ManifestRiskMapper manifestRiskMapper = sqlSession.getMapper(ManifestRiskMapper.class);
			manifestRisk = manifestRiskMapper.selectManifestRisk(md5);
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
		
		return manifestRisk;
	}
	
	/**
	 * 获取Java代码风险
	 * @param md5
	 * @return
	 */
	public static List<JavaCodeRisk> getJavaCodeRiskList(String md5) {
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
		SqlSession sqlSession = null;
		
		List<JavaCodeRisk> javaCodeRiskList = null;
		
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到JavaCodeRiskMapper接口
			JavaCodeRiskMapper javaCodeRiskMapper = sqlSession.getMapper(JavaCodeRiskMapper.class);
			javaCodeRiskList = javaCodeRiskMapper.selectJavaCodeRisksByMD5(md5);
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
		
		return javaCodeRiskList;
	}

	/**
	 * 获取相关信息
	 * @param md5
	 * @return
	 */
	public static ApkInfo getApkInfo(String md5) {
		//得到SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = getSqlFactory();
		SqlSession sqlSession = null;
		
		ApkInfo apkInfo = null;
		
		try {
			//获得一个sql会话
			sqlSession = sqlSessionFactory.openSession();
			//获取到ApkInfoMapper接口
			ApkInfoMapper apkInfoMapper = sqlSession.getMapper(ApkInfoMapper.class);
			apkInfo = apkInfoMapper.selectApkInfo(md5);
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
		
		return apkInfo;
	}
}
