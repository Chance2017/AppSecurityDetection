package com.chance.dao;

import com.chance.entities.TaskStatus;

public interface TaskStatusMapper {
	public void addTaskStatus(TaskStatus taskStatus);
	public TaskStatus selectTaskStatus(String md5);
	public void updateCodeMessageAndStatus(int code, String message, int status, String md5);
	public void updateTotal(int total, String md5);
	public void updateFinished(int finished, String md5);
}
