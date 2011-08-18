package com.pintu.utils;

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

}
