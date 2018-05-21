package com.chance.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.chance.entities.BufferReadSuccess;

/**
 * 读取控制台错误流线程
 * @author Chance
 */
public class ErrorThread extends Thread {
	private BufferReadSuccess success;
	private InputStream errorStream;

	public ErrorThread() {
		super();
	}

	public ErrorThread(BufferReadSuccess success, InputStream errorStream) {
		super();
		this.success = success;
		this.errorStream = errorStream;
	}

	@Override
	public void run() {
		BufferedReader errorBufferReader = new BufferedReader(new InputStreamReader(errorStream));
		String line = "";
		try {
			while((line=errorBufferReader.readLine()) != null) {
				System.out.println("Failed:" + line);
				success.setSuccess(false);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				errorStream.close();
			} catch (Exception e2) {
				System.out.println("错误流关闭失败");
				System.out.println(e2.toString());
			}
		}
	}
}
