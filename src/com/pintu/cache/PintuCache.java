package com.pintu.cache;

import java.util.ArrayList;
import java.util.List;

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

//FIXME, --------------- 同步的写入缓存对象操作 --------------------------------------

	public  synchronized void cachePicture(String key, Object value){
		Element ele = new Element(key, value);
		synchronized(pictureCache){
			pictureCache.put(ele);			
		}
	}
	
	public  synchronized void cacheComment(String key, Object value){
		Element ele = new Element(key, value);
		synchronized(commentCache){
			commentCache.put(ele);			
		}
	}
	
	public  synchronized void cacheStory(String key, Object value){
		Element ele = new Element(key, value);
		synchronized(storyCache){
			storyCache.put(ele);			
		}
	}
	
	public  synchronized void cacheVote(String key, Object value){
		Element ele = new Element(key, value);
		synchronized(voteCache){
			voteCache.put(ele);			
		}
	}
	
	public  synchronized void cacheThumbnail(String key, Object value){
		Element ele = new Element(key, value);
		synchronized(thumbnailCache){
			thumbnailCache.put(ele);			
		}
	}
	
	
//FIXME, 	-------------  同步的读取缓存对象操作 -------------------------------------
	
	/**
	 * 根据对象id取得图片缓存
	 * @param keys
	 * @return
	 */
	public synchronized List<Object> getCachedPicture( List<String> keys){
		List<Object> list = new ArrayList<Object>();
		synchronized(pictureCache){
			for(int i=0;i<keys.size();i++){
				Element picture=pictureCache.get(keys.get(i));
				if(picture != null){
					list.add(picture.getObjectValue());
				}
			}			
		}
		return list;
	}
	
	public synchronized List<Object> getCachedComment( List<String> keys){
		List<Object> list = new ArrayList<Object>();
		synchronized(commentCache){
			for(int i=0;i<keys.size();i++){
				Element comment=commentCache.get(keys.get(i));
				if(comment != null){
					list.add(comment.getObjectValue());
				}
			}			
		}
		return list;
	}

	public synchronized List<Object> getCachedVote( List<String> keys){
		List<Object> list = new ArrayList<Object>();
		synchronized(voteCache){
			for(int i=0;i<keys.size();i++){
				Element vote=voteCache.get(keys.get(i));
				if(vote != null){
					list.add(vote.getObjectValue());
				}
			}			
		}
		return list;
	}
	
	public synchronized List<Object> getCachedStory( List<String> keys){
		List<Object> list = new ArrayList<Object>();
		synchronized(storyCache){
			for(int i=0;i<keys.size();i++){
				Element story=storyCache.get(keys.get(i));
				if(story != null){
					list.add(story.getObjectValue());
				}
			}			
		}
		return list;
	}
	
	public synchronized List<Object> getCachedThumbnail( List<String> keys){
		List<Object> list = new ArrayList<Object>();
		synchronized(thumbnailCache){
			for(int i=0;i<keys.size();i++){
				Element story=thumbnailCache.get(keys.get(i));
				if(story != null){
					list.add(story.getObjectValue());
				}
			}	
		}
		return list;
	}
	
	

}
