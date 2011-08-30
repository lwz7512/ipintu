package com.pintu.cache;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import com.pintu.beans.TPicDesc;
import com.pintu.beans.User;
import com.pintu.utils.PintuUtils;

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
		System.out.println(">>> commentCache status: "+pictureCache.getStatus()+" commentCache size: "+commentCache.getKeysNoDuplicateCheck().size());
		System.out.println(">>> storyCache status: "+storyCache.getStatus()+" pictureCache size: "+storyCache.getKeysNoDuplicateCheck().size());
		System.out.println(">>> voteCache status: "+pictureCache.getStatus()+" voteCache size: "+voteCache.getKeysNoDuplicateCheck().size());
		System.out.println(">>> thumbnailCache status: "+thumbnailCache.getStatus()+" thumbnailCache size: "+thumbnailCache.getKeysNoDuplicateCheck().size());
		System.out.println(">>> pictureCache status: "+pictureCache.getStatus()+" pictureCache size: "+pictureCache.getKeysNoDuplicateCheck().size());
//		printCacheKeys();
		System.out.println("--------------- trace end --------------------");
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
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

	//用户登录到系统时缓存用户信息
	public void cacheUser(User user){
		Long updateTime = Long.parseLong(user.getLastUpdateTime());
		long minlong = updateTime/(60*1000);
		int min = Math.round(minlong);
		String key = String.valueOf(min);
		Element savedMinute = userCache.get(key);
		if(savedMinute==null){
			//保存的分钟数，缩略图id,缩略图对象
			Element elmt = new Element(key,new HashMap<String, User>());
			@SuppressWarnings("unchecked")
			HashMap<String, User>  usersUpdateInOneMinute = (HashMap<String, User>) elmt.getObjectValue();
			usersUpdateInOneMinute.put(user.getId(),user);
			synchronized(userCache){
				userCache.put(elmt);
			}
		}else{
			@SuppressWarnings("unchecked")
			HashMap<String, User>  savedMap = (HashMap<String, User>) savedMinute.getObjectValue();
			savedMap.put(user.getId(), user);
			Element ele = new Element(key,savedMap);
			synchronized(userCache){
				userCache.put(ele);
			}
		}
	}


	//更新登录到系统中缓存的用户的最后操作时间
	@SuppressWarnings("unchecked")
	public void updateCachedUser(String userId, String updateTime){
		Long upTime = Long.parseLong(updateTime);
		long minlong = upTime/(60*1000);
		int min = Math.round(minlong);
		String key = String.valueOf(min);
		synchronized(userCache){
			List<String> timeList = userCache.getKeys();
			for(int i = 0;i<timeList.size();i++){
				HashMap<String, User> userMap = (HashMap<String, User>) userCache.get(timeList.get(i)).getObjectValue();
				User user = userMap.get(userId);
				if(user != null){
					user.setLastUpdateTime(updateTime);
					userMap.put(userId, user);
					Element ele = new Element(key,userMap);
					userCache.put(ele);
				}
			}
		}
	}
	
	//根据更新时间取出一个时间点的活跃用户信息
	@SuppressWarnings("unchecked")
	public List<User> getLiveUser(String updateTime){
		List<User> userList = new ArrayList<User>();
		synchronized(userCache){
			Element savedMinute = userCache.get(updateTime);
			if(savedMinute!=null){
				HashMap<String, User>  savedMap = (HashMap<String, User>) savedMinute.getObjectValue();
				if(savedMap != null){
						userList.addAll(savedMap.values());
				}
			}
		}
		return userList;
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


	/**
	 * 缓存缩略图 thumbnailCache  <分钟数，<thumbnailId,TpicDesc>>
	 * @param pic
	 */
	public void cacheThumbnail(TPicDesc pic){
		Long pubTime = Long.parseLong(pic.getCreationTime());
		long minlong = pubTime/(60*1000);
		int min = Math.round(minlong);
		String key = String.valueOf(min);
		Element savedMinute = thumbnailCache.get(key);

		if(savedMinute==null){
			//保存的分钟数，缩略图id,缩略图对象
			Element elmt = new Element(key,new HashMap<String, TPicDesc>());
			@SuppressWarnings("unchecked")
			HashMap<String, TPicDesc>  picsInOneMinute = (HashMap<String, TPicDesc>) elmt.getObjectValue();
			picsInOneMinute.put(pic.getThumbnailId(), pic);
			synchronized(thumbnailCache){
				thumbnailCache.put(elmt);
			}
		}else{
			@SuppressWarnings("unchecked")
			HashMap<String, TPicDesc>  savedMap = (HashMap<String, TPicDesc>) savedMinute.getObjectValue();
			savedMap.put(pic.getThumbnailId(), pic);
			Element ele = new Element(key,savedMap);
			synchronized(thumbnailCache){
				thumbnailCache.put(ele);
			}
		}
	}
	
	/**
	 * 根据发布时间取图片
	 * @param pubTime 格式化的时间字符串
	 * @param picId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TPicDesc> getCachedThumbnail(String createTime){
		List<TPicDesc> list=new ArrayList<TPicDesc>();
		synchronized(thumbnailCache){
			Element savedMinute = thumbnailCache.get(createTime);
			if(savedMinute!=null){
				HashMap<String, TPicDesc>  savedMap = (HashMap<String, TPicDesc>) savedMinute.getObjectValue();
				if(savedMap != null){
						list.addAll(savedMap.values());
				}
			}
		}
		return list;
	}
	
	
}
