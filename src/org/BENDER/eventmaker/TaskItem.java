package org.BENDER.eventmaker;

public class TaskItem {
	private String task;
	private String status;
	private String time;
	private int taskType;// 0 - required, 1 - optional
	private int statusID;// 0 - none, 1 - code fail
						 // 2 - code compiles, 3 - review fail
						 // 4 - code works, 5 - almost win, 6 - epic win

	TaskItem(String task, String status, String time, int taskType, int statusID) {
		this.task = task;
		this.status = status;
		this.time = time;
		}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getStatusID() {
		return statusID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}

	
}
