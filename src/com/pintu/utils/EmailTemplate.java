package com.pintu.utils;

import java.util.Map;

public class EmailTemplate {
		    private static final String START_FLAG = "${";
		    private static final String END_FLAG = "}";
		    /**
		     * 将字符串中特定模式的字符转换成map中对应的值
		     *
		     * @param s
		     *            需要转换的字符串
		     * @param map
		     *            转换所需的键值对集合
		     * @return 转换后的字符串
		     */
		    public static String convert(String s, Map<String, String> map) {
		        StringBuilder ret = new StringBuilder(s.length());
		
		        int cursor = 0;
		
		        for (int start, end; (start = s.indexOf(START_FLAG, cursor)) != -1
		                && (end = s.indexOf(END_FLAG, start)) != -1;) {
		            ret.append(s.substring(cursor, start)).append(
		                    map.get(s.substring(start + START_FLAG.length(), end)));
		            cursor = end + END_FLAG.length();
		        }
		        ret.append(s.substring(cursor, s.length()));
		        return ret.toString();
		    }

}
