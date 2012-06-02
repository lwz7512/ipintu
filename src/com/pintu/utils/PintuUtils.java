package com.pintu.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class PintuUtils {

	private static final  SimpleDateFormat simpledDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//TODO, some static functions here;
	
	public static String generateUID(){
		String uid = UUID.randomUUID().toString().replace("-", "").substring(16);
		return uid;
	}
	
	
	public static String getFormatNowTime(){
		String now = simpledDateFormat.format(new Date().getTime());
		return now;
	}
	
	public static String formatDate(Date date){
		return simpledDateFormat.format(date);
	}

	public static String formatLong(Long time){
		return simpledDateFormat.format(time);
	}
	//转化格式化后的时间字符串成时间格式
	public static Date parseToDate(String time){
		Date date = new Date();
		try {
			 date = simpledDateFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	//当日零点
	public static String getToday() {
		Calendar c = Calendar.getInstance();
	    int   year=c.get(Calendar.YEAR); 
	    int   month=c.get(Calendar.MONTH); 
	    int   day=c.get(Calendar.DATE); 
		c.set(year, month, day, 0, 0, 0);
		Date date = c.getTime();
		//得到当天零点即 "2011-08-12 00:00:00"
		String today = PintuUtils.formatDate(date);
		return today;
	}
	
	//一月后零点
	public static String getAmonthAgo() {
		Calendar c = Calendar.getInstance();
	    int   year=c.get(Calendar.YEAR); 
	    int   month=c.get(Calendar.MONTH)+1; 
	    int   day=c.get(Calendar.DATE); 
		c.set(year, month, day, 0, 0, 0);
		Date date = c.getTime();
		//得到零点即 "2011-08-12 00:00:00"
		String today = PintuUtils.formatDate(date);
		return today;
	}
	
	public static String generateInviteCode(){
		return Encrypt.encrypt(String.valueOf(System.currentTimeMillis())).substring(0, 6);
	}
	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
	}
}
