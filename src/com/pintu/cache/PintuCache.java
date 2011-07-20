package com.pintu.cache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * �����������Ļ����߼������������У�
 * �ϴ�ͼƬ������ͼ����ͼ���󡢹��¡�ͶƱ������
 * ���ɵ�ͼƬ�����ڴ����ϲ�����Ϊ����
 * @author lwz
 *
 */
public class PintuCache {
	
	private CacheManager manager;
	
	private Cache ptCache;
	
	private Cache commentCache;
	
	private Cache storyCache;
	
	public PintuCache(){
		//�����ļ������ø��ֻ���ʵ������
		URL url = getClass().getResource("pintucache.xml");
		manager = new CacheManager(url);
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
