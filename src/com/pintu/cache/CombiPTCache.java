package com.pintu.cache;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.pintu.beans.TPicItem;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 按时间为主索引的缓存处理方案：
 * 缓存主键是分钟数，值是hashmap，辅键是id，值是对象；
 * @author lwz
 *
 */
public class CombiPTCache {

	private CacheManager manager;

	private Cache pictureCache;

	private Cache commentCache;

	private Cache storyCache;

	private Cache userCache;

	private Cache voteCache;

	private Cache thumbnailCache;

	private Logger log = Logger.getLogger(CombiPTCache.class);
	
	public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	public CombiPTCache(){

		// 配置文件里配置各种缓存实例参数
		manager = CacheManager.getInstance();
		// 用户使用默认缓存
		manager.addCache("userCache");
		userCache = manager.getCache("userCache");

		pictureCache = manager.getCache("picturecache");
		commentCache = manager.getCache("commentcache");
		storyCache = manager.getCache("storycache");
		voteCache = manager.getCache("votecache");
		thumbnailCache = manager.getCache("thumbnailcache");
		
	}
	
	private String getMinutesByFormatTime(String formattedTime){
		String minutes = null;
		try {
			Date dt = DATE_FORMATTER.parse(formattedTime);
			long minlong = dt.getTime()/60*1000;
			int min = Math.round(minlong);
			minutes = String.valueOf(min);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return minutes;
	}
	
	private String getMinutesByMiliSeconds(long miliSeconds){
		String minutes = null;
		long minlong = miliSeconds/60*1000;
		int min = Math.round(minlong);
		minutes = String.valueOf(min);
		return minutes;
	}
	
	@SuppressWarnings("unchecked")
	public void cachePicture(TPicItem pic){
		String pubTime = pic.getPublishTime();
		String key = getMinutesByFormatTime(pubTime);
		Element savedMinute = pictureCache.get(key);
		if(savedMinute==null){
			Element elmt = new Element(key,new HashMap<String, TPicItem>());
			HashMap<String, TPicItem>  picsInOnMinute = (HashMap<String, TPicItem>) elmt.getObjectValue();
			picsInOnMinute.put(pic.getId(), pic);
		}else{
			HashMap<String, TPicItem>  savedMap = (HashMap<String, TPicItem>) savedMinute.getObjectValue();
			savedMap.put(pic.getId(), pic);
		}
	}
	
	/**
	 * 根据发布时间取图片
	 * @param pubTime 格式化的时间字符串
	 * @param picId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TPicItem getCachedPicture(String pubTime, String picId){
		String key = getMinutesByFormatTime(pubTime);
		Element savedMinute = pictureCache.get(key);
		if(savedMinute!=null){
			HashMap<String, TPicItem>  savedMap = (HashMap<String, TPicItem>) savedMinute.getObjectValue();
			return savedMap.get(picId);
		}
		return null;
	}
	
	public List<TPicItem> getCachedPics(String startTime, String endTime){
		
		return null;
	}
	
	
	
	
} //end of class
