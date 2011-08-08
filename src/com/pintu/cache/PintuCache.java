package com.pintu.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 这里是真正的缓存逻辑，缓存内容有： 上传图片的缩略图、贴图对象、故事、投票、评论
 * 生成的图片均放在磁盘上不缓存为对象；
 * 
 * @author lwz
 * 
 */
public class PintuCache {

	private CacheManager manager;

	private Cache pictureCache;

	private Cache commentCache;

	private Cache storyCache;

	private Cache userCache;

	private Cache voteCache;

	private Cache thumbnailCache;

	private Logger log = Logger.getLogger(PintuCache.class);
	
	public PintuCache() {

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
	
	public void traceAll(){
		System.out.println("--------------- trace begin: -----------------");
		System.out.println(">>> thumbnailCache status: "+thumbnailCache.getStatus()+" thumbnailCache size: "+thumbnailCache.getSize());
		System.out.println(">>> pictureCache status: "+pictureCache.getStatus()+" pictureCache size: "+pictureCache.getSize());
		printCacheKeys();
		System.out.println("--------------- trace end --------------------");
	}
	
	@SuppressWarnings("unchecked")
	private void printCacheKeys(){
		List<String> keys = thumbnailCache.getKeys();
		StringBuffer sb = new StringBuffer();
		sb.append("thumbnail keys: ");
		for(String key : keys){
			Element o = thumbnailCache.get(key);
			boolean exist = (o==null)?false:true;
			sb.append(key).append(": ").append(exist).append(", ");
		}
		sb.append("\n");
		
		//打印贴图对象ID，以便对比
		keys = pictureCache.getKeys();
		sb.append("tpicitem keys: ");
		for(String key : keys){
			Element o = pictureCache.get(key);
			boolean exist = (o==null)?false:true;
			sb.append(key).append(": ").append(exist).append(", ");
		}
		sb.append("\n");
		
		System.out.println(sb.toString());
	}

	public void cachePicture(String key, Object value) {
		Element ele = new Element(key, value);
		synchronized (pictureCache) {
			pictureCache.put(ele);
		}
	}

	public void cacheComment(String key, Object value) {
		Element ele = new Element(key, value);
		synchronized (commentCache) {
			commentCache.put(ele);
		}
	}

	public void cacheStory(String key, Object value) {
		Element ele = new Element(key, value);
		synchronized (storyCache) {
			storyCache.put(ele);
		}
	}

	public void cacheVote(String key, Object value) {
		Element ele = new Element(key, value);
		synchronized (voteCache) {
			voteCache.put(ele);
		}
	}

	public void cacheThumbnail(String key, Object value) {
		Element ele = new Element(key, value);
		synchronized (thumbnailCache) {
			thumbnailCache.put(ele);
		}
	}

	/**
	 * 根据对象id取得图片缓存
	 * 
	 * @param keys
	 * @return
	 */
	public List<Object> getCachedPicture(List<String> keys) {
		List<Object> list = new ArrayList<Object>();
		synchronized (pictureCache) {
			for (int i = 0; i < keys.size(); i++) {
				Element picture = pictureCache.get(keys.get(i));
				if (picture != null) {
					list.add(picture.getObjectValue());
				}
			}
		}
		return list;
	}

	public List<Object> getCachedComment(List<String> keys) {
		List<Object> list = new ArrayList<Object>();
		synchronized (commentCache) {
			for (int i = 0; i < keys.size(); i++) {
				Element comment = commentCache.get(keys.get(i));
				if (comment != null) {
					list.add(comment.getObjectValue());
				}
			}
		}
		return list;
	}

	public List<Object> getCachedVote(List<String> keys) {
		List<Object> list = new ArrayList<Object>();
		synchronized (voteCache) {
			for (int i = 0; i < keys.size(); i++) {
				Element vote = voteCache.get(keys.get(i));
				if (vote != null) {
					list.add(vote.getObjectValue());
				}
			}
		}
		return list;
	}

	public List<Object> getCachedStory(List<String> keys) {
		List<Object> list = new ArrayList<Object>();
		synchronized (storyCache) {
			for (int i = 0; i < keys.size(); i++) {
				Element story = storyCache.get(keys.get(i));
				if (story != null) {
					list.add(story.getObjectValue());
				}
			}
		}
		return list;
	}

	public List<Object> getCachedThumbnail(List<String> keys) {
		
		List<Object> list = new ArrayList<Object>();
		
		System.out.println("PintuCache---thumbnailCache"+ thumbnailCache.getSize());
		
		//FIXME, 缓存数已经测过，不为0，有可能是同步的原因吗？
		synchronized (thumbnailCache) {
			for (int i = 0; i < keys.size(); i++) {
				//FIXME, 有可能是key的问题，对应不上，所以取不到元素
				System.out.println(">>> 要获取元素的KEY为："+keys.get(i));
				Element thumbnail = thumbnailCache.get(keys.get(i));
				if (thumbnail != null) {
					list.add(thumbnail.getObjectValue());
				}else{
					System.out.println(">>> 哇塞，没取到缓存对象："+keys.get(i)+" 哪来的呢？");
					log.warn(">>> 哇塞，没取到缓存对象："+keys.get(i)+" 哪来的呢？");
				}
			}
		}
		return list;
	}

}
