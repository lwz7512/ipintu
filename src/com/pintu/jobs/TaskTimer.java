package com.pintu.jobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class TaskTimer {

	private final Timer timer = new Timer(true);
	
	private int min = 1;
	
	// 一天的毫秒数
	long daySpan = 24 * 60 * 60 * 1000;

	private CalculateTask calculateTask;
	
	private MidnightTask midnightTask;

	public TaskTimer() {
		
	}

	public CalculateTask getCalculateTask() {
		return calculateTask;
	}

	public void setCalculateTask(CalculateTask calculateTask) {
		this.calculateTask = calculateTask;
	}

	public MidnightTask getMidnightTask() {
		return midnightTask;
	}

	public void setMidnightTask(MidnightTask midnightTask) {
		this.midnightTask = midnightTask;
	}

	public int getMin() {
		return min;
	}


	public void setMin(int min) {
		this.min = min;
	}


	public void start() {
		
		Calendar c = Calendar.getInstance();
	    int year=c.get(Calendar.YEAR); 
	    int month=c.get(Calendar.MONTH); 
	    int day=c.get(Calendar.DATE); 
	    int hour=c.get(Calendar.HOUR_OF_DAY)+1;
		c.set(year, month, day, hour, 0, 0);
		Date date = c.getTime();
		
		if (calculateTask != null) {
			timer.schedule(calculateTask, date, min * 60 * 1000);
		}
	}

	public void runAtFixTime(String timeToRun) {
		// 规定的每天时间00:00:00运行
		// final SimpleDateFormat dateFormat = new
		// SimpleDateFormat("yyyy-MM-dd '00:00:00'");
		/**
		 * 这里设定一个时间点，查看上一个小时的活跃用户，并为其计算积分等级。这里活跃用户的识别方式需要用到用户在
		 * 登录、发图片、写故事的时候记录一下时间即最后更新时间
		 */
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd '"
				+ timeToRun + "'");

		// 首次运行时间
		Date startTime = null;
		try {
			startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(dateFormat.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 如果今天的已经过了 首次运行时间就改为明天
		if (System.currentTimeMillis() > startTime.getTime())
			startTime = new Date(startTime.getTime() + daySpan);

		// 以每24小时执行一次
		timer.scheduleAtFixedRate(midnightTask, startTime, daySpan);
	}

	public void stop() {
		timer.cancel();
	}

}
