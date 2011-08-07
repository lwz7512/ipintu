package com.pintu.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.Vote;
import com.pintu.dao.CacheAccessInterface;

/**
 * 这里是真正的缓存逻辑，缓存内容有： 上传图片的缩略图、贴图对象、故事、投票、评论 生成的图片均放在磁盘上不缓存为对象；
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

	
	//根据类型不同，存入不同的缓存中
	public void cacheObject(String type, String key, Object value) {
		Element ele = new Element(key, value);
		if(type.equals(CacheAccessInterface.PICTURE_TYPE)){
			pictureCache.put(ele);
		}else if(type.equals(CacheAccessInterface.COMMENT_TYPE)){
			commentCache.put(ele);
		}else if(type.equals(CacheAccessInterface.STORY_TYPE)){
			storyCache.put(ele);
		}else if(type.equals(CacheAccessInterface.VOTE_TYPE)){
			voteCache.put(ele);
		}else if(type.equals(CacheAccessInterface.THUMBNAIL_TYPE)){
			thumbnailCache.put(ele);
		}
	}

	
//	public Object getCachedPintu(String key){
//		Element pintu = pictureCache.get(key);
//		if(pintu!=null){
//			return pintu.getObjectValue();
//		}
//		return null;
//	}

	
	//根据传入的类型不同，返回存中的相应数据
	public List<Object> getCachedObj(String type, List<String> keys) {
		List<Object> list = new ArrayList<Object>();
		if(type.equals(CacheAccessInterface.PICTURE_TYPE)){
//		    pictureCache
			list.clear();
			for(int i=0;i<keys.size();i++){
				Element picture=pictureCache.get(keys.get(i));
				if(picture != null){
					list.add(picture.getObjectValue());
				}
			}
			
		}else if(type.equals(CacheAccessInterface.COMMENT_TYPE)){
//			commentCache
			list.clear();
			for(int i=0;i<keys.size();i++){
				Element comment=commentCache.get(keys.get(i));
				if(comment != null){
					list.add(comment.getObjectValue());
				}
			}
		}else if(type.equals(CacheAccessInterface.STORY_TYPE)){
//			storyCache
			list.clear();
			for(int i=0;i<keys.size();i++){
				Element story=storyCache.get(keys.get(i));
				if(story != null){
					list.add(story.getObjectValue());
				}
			}
		}else if(type.equals(CacheAccessInterface.VOTE_TYPE)){
//			voteCache
			list.clear();
			for(int i=0;i<keys.size();i++){
				Element vote=voteCache.get(keys.get(i));
				if(vote != null){
					list.add(vote.getObjectValue());
				}
			}
		}else if(type.equals(CacheAccessInterface.THUMBNAIL_TYPE)){
//			thumbnailCache
			list.clear();
			for(int i=0;i<keys.size();i++){
				Element thumbnail=thumbnailCache.get(keys.get(i));
				if(thumbnail != null){
					list.add(thumbnail.getObjectValue());
				}
			}
		}
		return list;
	}

}
