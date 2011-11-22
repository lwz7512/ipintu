package com.pintu.utils;

import java.security.MessageDigest;
import sun.misc.BASE64Encoder;

public class Encrypt {
	public static String encrypt(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			BASE64Encoder encoder = new BASE64Encoder();
			byte[] b = md.digest(str.getBytes("UTF-8"));
			String encryptStr = encoder.encode(b);
			return encryptStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(encrypt("111111"));
	}
}
