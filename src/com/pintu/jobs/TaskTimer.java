package com.pintu.jobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class TaskTimer {

	private final Timer timer = new Timer(true);
	
	private int min = 1;
	
	// һ��ĺ�����
	long daySpan = 24 * 60 * 60 * 1000;

	private CalculateTask task;

	public TaskTimer() {
		
	}

	public int getMin() {
		return min;
	}


	public void setMin(int min) {
		this.min = min;
	}

	public CalculateTask getTask() {
		return task;
	}

	public void setTask(CalculateTask task) {
		this.task = task;
	}

	public void start() {
		Date date = new Date();
		if (task != null) {
			timer.schedule(task, date, min * 60 * 1000);
		}
	}

	public void runAtFixTime(String timeToRun) {
		// �涨��ÿ��ʱ��00:00:00����
		// final SimpleDateFormat dateFormat = new
		// SimpleDateFormat("yyyy-MM-dd '00:00:00'");
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd '"
				+ timeToRun + "'");

		// �״�����ʱ��
		Date startTime = null;
		try {
			startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(dateFormat.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ���������Ѿ����� �״�����ʱ��͸�Ϊ����
		if (System.currentTimeMillis() > startTime.getTime())
			startTime = new Date(startTime.getTime() + daySpan);

		// ��ÿ24Сʱִ��һ��
		timer.scheduleAtFixedRate(task, startTime, daySpan);
	}

	public void stop() {
		timer.cancel();
	}

}
