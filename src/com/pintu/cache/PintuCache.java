package com.pintu.cache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 这里是真正的缓存逻辑，缓存内容有：
 * 上传图片的缩略图、贴图对象、故事、投票、评论
 * 生成的图片均放在磁盘上不缓存为对象；
 * @author lwz
 *
 */
public class PintuCache {
	
	private CacheManager manager;
	
	private Cache ptCache;
	
	private Cache commentCache;
	
	private Cache storyCache;
	
	private Cache userCache;
	
	
	public PintuCache(){			
		
		//配置文件里配置各种缓存实例参数
		manager = CacheManager.getInstance();
		//用户使用默认缓存
		manager.addCache("userCache");
		userCache = manager.getCache("userCache");
		
		ptCache = manager.getCache("pintucache");
		commentCache = manager.getCache("commentcache");
		storyCache = manager.getCache("storycache");
	}
	
	public void cachePintuObject(String key, Object value){
		Element elmt = new Element(key,value);
		ptCache.put(elmt);
	}
	
	public Object getCachedPintu(String key){
		Element pintu = ptCache.get(key);
		if(pintu!=null){
			return pintu.getObjectValue();
		}
		return null;
	}
	
	
}
