package com.chance.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.chance.entities.BufferReadSuccess;

/**
 * 读取控制台输出流线程
 * @author Chance
 */
public class InputThread extends Thread {
	private BufferReadSuccess success;
	private InputStream inputStream;

	public InputThread() {
		super();
	}

	public InputThread(BufferReadSuccess success, InputStream inputStream) {
		super();
		this.success = success;
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		BufferedReader inputBufferReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		try {
			while((line = inputBufferReader.readLine()) != null){
				System.out.println("Success:" + line);
			}
		} catch (IOException e) {
			System.out.println("文件反编译失败");
			success.setSuccess(false);
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				System.out.println("输入流关闭失败");
				e.printStackTrace();
			}
		}
	}
}
