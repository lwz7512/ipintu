package com.pintu.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class PintuUtils {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//TODO, some static functions here;
	
	public static String generateUID(){
		String uid = UUID.randomUUID().toString().replace("-", "").substring(16);
		return uid;
	}
	
	
	public static String getFormatNowTime(){
		String now = sdf.format(new Date().getTime());
		return now;
	}
	
	public static String formatDate(Date date){
		return sdf.format(date);
	}

	public static String formatLong(Long time){
		return sdf.format(time);
	}
	//转化格式化后的时间字符串成时间格式
	public static Date parseToDate(String time){
		Date date = new Date();
		try {
			 date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}
