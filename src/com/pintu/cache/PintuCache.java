package com.pintu.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeExtractorException;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.log4j.Logger;

import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.beans.Vote;

/**
 * 这里是真正的缓存逻辑，缓存内容有： 上传图片的缩略图、贴图对象、故事、投票、评论 生成的图片均放在磁盘上不缓存为对象；
 * 
 * @author lwz
 * 
 */
public class PintuCache {

	private CacheManager cacheManager;

	private Cache pictureCache;

	private Cache storyCache;

	private Cache userCache;

	private Cache voteCache;

	private Cache thumbnailCache;

	private Logger log = Logger.getLogger(PintuCache.class);

	public PintuCache() {

		cacheManager = CacheManager.getInstance();

		initUserCache();

		pictureCache = cacheManager.getCache("picturecache");
		storyCache = cacheManager.getCache("storycache");
		voteCache = cacheManager.getCache("votecache");
		thumbnailCache = cacheManager.getCache("thumbnailcache");
	}

	private void initUserCache() {
		Searchable searchable = new Searchable();
		searchable.addSearchAttribute(new SearchAttribute().name(
				"lastUpdateTime").className(
				UpdateAttributeExtractor.class.getName()));

		CacheConfiguration cacheConfig = new CacheConfiguration("usercache",
				10000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
				.overflowToDisk(false).eternal(false).timeToLiveSeconds(7200)
				.timeToIdleSeconds(3600).diskPersistent(false);
		cacheConfig.addSearchable(searchable);

		userCache = new Cache(cacheConfig);
		cacheManager.addCache(userCache);
	}

	public void traceAll() {
		System.out.println("--------------- trace begin: -----------------");
		System.out.println(">>> storyCache status: " + storyCache.getStatus()
				+ " pictureCache size: "
				+ storyCache.getKeysNoDuplicateCheck().size());
		System.out.println(">>> voteCache status: " + pictureCache.getStatus()
				+ " voteCache size: "
				+ voteCache.getKeysNoDuplicateCheck().size());
		System.out.println(">>> thumbnailCache status: "
				+ thumbnailCache.getStatus() + " thumbnailCache size: "
				+ thumbnailCache.getKeysNoDuplicateCheck().size());
		System.out.println(">>> pictureCache status: "
				+ pictureCache.getStatus() + " pictureCache size: "
				+ pictureCache.getKeysNoDuplicateCheck().size());
		// printCacheKeys();
		System.out.println("--------------- trace end --------------------");
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void printCacheKeys() {
		List<String> keys = thumbnailCache.getKeys();
		StringBuffer sb = new StringBuffer();
		sb.append("thumbnail keys: ");
		for (String key : keys) {
			Element o = thumbnailCache.get(key);
			boolean exist = (o == null) ? false : true;
			sb.append(key).append(": ").append(exist).append(", ");
		}
		sb.append("\n");

		// 打印贴图对象ID，以便对比
		keys = pictureCache.getKeys();
		sb.append("tpicitem keys: ");
		for (String key : keys) {
			Element o = pictureCache.get(key);
			boolean exist = (o == null) ? false : true;
			sb.append(key).append(": ").append(exist).append(", ");
		}
		sb.append("\n");

		System.out.println(sb.toString());
	}

	// 缓存用户信息
	public void cacheUser(String userId, User user) {
		Element elmt = new Element(userId, user);
		synchronized (userCache) {
			userCache.put(elmt);
		}
	}

	// 更新缓存中用户的最后更新时间
	public void updateCachedUser(String userId, Long updateTime) {
		synchronized (userCache) {
			Element ele = userCache.get(userId);
			if (ele != null) {
				User user = (User) ele.getObjectValue();
				user.setLastUpdateTime(updateTime);
			}
		}
	}

	public static class UpdateAttributeExtractor implements AttributeExtractor {
		private static final long serialVersionUID = -5857114497229946414L;

		@Override
		public Object attributeFor(Element element, String str)
				throws AttributeExtractorException {
			return ((User) element.getValue()).getLastUpdateTime();
		}
	}

	// 得到活跃用户
	public List<User> getActiveUser(Long startTime, Long endTime) {
		List<User> userList = new ArrayList<User>();
		synchronized (userCache) {
			if (userCache.getKeysNoDuplicateCheck().size() > 0) {
				Attribute<Long> lastUpdateTime = userCache
						.getSearchAttribute("lastUpdateTime");
				Query query = userCache
						.createQuery()
						.addCriteria(lastUpdateTime.between(startTime, endTime))
						.includeValues().end();
				Results results = query.execute();
				List<Result> resultList = results.all();
				if (resultList != null && resultList.size() > 0) {
					for (int i = 0; i < resultList.size(); i++) {
						User user = (User) resultList.get(i).getValue();
						userList.add(user);
					}
				}
				results.discard();
			}
		}
		return userList;
	}

	public User getCachedUser(String userId) {
		User user = new User();
		synchronized (userCache) {
			Element ele = userCache.get(userId);
			if (ele != null) {
				user = (User) ele.getObjectValue();
			}
		}
		return user;
	}

	public void cachePicture(String picId, TPicItem pic) {
		Element ele = new Element(picId, pic);
		synchronized (pictureCache) {
			pictureCache.put(ele);
		}
	}

	public void cacheStory(String picId, String storyId, Story story) {
		Element savedPic = storyCache.get(picId);
		if (savedPic == null) {
			// 故事所属图片，故事id,故事对象
			Element elmt = new Element(picId, new HashMap<String, Story>());
			@SuppressWarnings("unchecked")
			HashMap<String, Story> storiesForOnePic = (HashMap<String, Story>) elmt
					.getObjectValue();
			storiesForOnePic.put(storyId, story);
			synchronized (storyCache) {
				storyCache.put(elmt);
			}
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Story> savedMap = (HashMap<String, Story>) savedPic
					.getObjectValue();
			savedMap.put(storyId, story);
		}
	}

	public void cacheVote(String storyId, String voteId, Vote vote) {
		Element savedStory = voteCache.get(storyId);
		if (savedStory == null) {
			// 投票所属故事，投票id,投票对象
			Element elmt = new Element(storyId, new HashMap<String, Vote>());
			@SuppressWarnings("unchecked")
			HashMap<String, Vote> votesForOneStory = (HashMap<String, Vote>) elmt
					.getObjectValue();
			votesForOneStory.put(voteId, vote);
			synchronized (voteCache) {
				voteCache.put(elmt);
			}
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Vote> savedMap = (HashMap<String, Vote>) savedStory
					.getObjectValue();
			savedMap.put(voteId, vote);
		}
	}

	/**
	 * 缓存缩略图 thumbnailCache <分钟数，<thumbnailId,TpicDesc>>
	 * 
	 * @param pic
	 */
	public void cacheThumbnail(TPicDesc pic) {
		Long pubTime = Long.parseLong(pic.getCreationTime());
		long minlong = pubTime / (60 * 1000);
		int min = Math.round(minlong);
		String key = String.valueOf(min);
		Element savedMinute = thumbnailCache.get(key);

		if (savedMinute == null) {
			// 保存的分钟数，缩略图id,缩略图对象
			Element elmt = new Element(key, new HashMap<String, TPicDesc>());
			@SuppressWarnings("unchecked")
			HashMap<String, TPicDesc> picsInOneMinute = (HashMap<String, TPicDesc>) elmt
					.getObjectValue();
			picsInOneMinute.put(pic.getThumbnailId(), pic);
			synchronized (thumbnailCache) {
				thumbnailCache.put(elmt);
			}
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, TPicDesc> savedMap = (HashMap<String, TPicDesc>) savedMinute
					.getObjectValue();
			savedMap.put(pic.getThumbnailId(), pic);
		}
	}

	/**
	 * 根据发布时间取图片
	 * 
	 * @param pubTime
	 *            格式化的时间字符串
	 * @param picId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TPicDesc> getCachedThumbnail(String createTime) {
		List<TPicDesc> list = new ArrayList<TPicDesc>();
		synchronized (thumbnailCache) {
			Element savedMinute = thumbnailCache.get(createTime);
			if (savedMinute != null) {
				HashMap<String, TPicDesc> savedMap = (HashMap<String, TPicDesc>) savedMinute
						.getObjectValue();
				if (savedMap != null) {
					list.addAll(savedMap.values());
				}
			}
		}
		return list;
	}

	/**
	 * 根据对象id取得图片缓存
	 * 
	 * @param picIds
	 * @return
	 */
	public List<Object> getToSavedCachedPicture(List<String> picIds) {
		List<Object> list = new ArrayList<Object>();
		synchronized (pictureCache) {
			for (int i = 0; i < picIds.size(); i++) {
				Element picture = pictureCache.get(picIds.get(i));
				if (picture != null) {
					list.add(picture.getObjectValue());
				}
			}
		}
		return list;
	}

	/**
	 * 获取即将入库的评论
	 * @param map  <picId,storyIdList>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getToSavedCachedStory(Map<String, LinkedList<String>> map) {
		List<Object> list = new ArrayList<Object>();
		synchronized (storyCache) {
			for (String picId : map.keySet()) {
				// 得到一个图片下所有的故事
				Element savedPic = storyCache.get(picId);
				
				if(savedPic==null) continue;
				
				// 即将入库的故事ID
				LinkedList<String> storiesIDs = map.get(picId);
				// 该图片所有的故事，有入库的也有未入库的
				HashMap<String, Story> storiesInPic = (HashMap<String, Story>) savedPic
						.getObjectValue();
				// 遍历未入库的故事ID
				for (String storyId : storiesIDs) {
					if(storiesInPic==null) break;
					list.add(storiesInPic.get(storyId));
				}
			}
		}
		return list;
	}

	/**
	 * 获取即将入库的投票
	 * @param map <picId,voteIdList>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getToSavedCachedVote(Map<String, LinkedList<String>> map) {
		List<Object> list = new ArrayList<Object>();
		synchronized (voteCache) {
			for (String picId : map.keySet()) {
				//得到一个图片的所有投票
				Element savedPic = voteCache.get(picId);
				
				if (savedPic == null) continue;
				
				//即将入库的投票id
				LinkedList<String> voteIDs = map.get(picId);
				//该图片的所有投票，有入库的也有未入库的
				HashMap<String, Vote> votesInPic = (HashMap<String, Vote>) savedPic
							.getObjectValue();
				// 遍历未入库的故事ID
				for (String voteId : voteIDs) {
					if(votesInPic==null) break;
					list.add(votesInPic.get(voteId));
				}
			}
		}
		return list;
	}

	/**
	 * 根据图片获取故事
	 * 
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Story> getCachedStoryByPid(String picId) {
		List<Story> list = new ArrayList<Story>();
		synchronized (storyCache) {
			Element savedPic = storyCache.get(picId);
			if (savedPic != null) {
				
				HashMap<String, Story> storyMap = (HashMap<String, Story>) savedPic
						.getObjectValue();
				if (storyMap != null) {
					list.addAll(storyMap.values());
				}
			}
		}
		return list;
	}

	/**
	 * 根据图片id得到投票
	 * 
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Vote> getCachedVoteByPid(String picId) {
		List<Vote> list = new ArrayList<Vote>();
		synchronized (voteCache) {
			Element savedPic = voteCache.get(picId);
			if (savedPic != null) {
				HashMap<String, Vote> voteMap = (HashMap<String, Vote>) savedPic
						.getObjectValue();
				if (voteMap != null) {
					list.addAll(voteMap.values());
				}
			}
		}
		return list;
	}

	// 删除缓存中不能入库的问题图片
	public boolean removeTPicById(String id) {
		boolean del = false;
		synchronized (pictureCache) {
			del = pictureCache.remove(id);
		}
		return del;
	}

	// 删除不能入库问题图片所对应的缩略图缓存
	public boolean removeThumbnailById(long time, String thumbnailId) {
		boolean del = false;
		long minlong = time / (60 * 1000);
		int min = Math.round(minlong);
		String key = String.valueOf(min);
		synchronized (thumbnailCache) {
			Element savedMinute = thumbnailCache.get(key);
			if (savedMinute != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, TPicDesc> savedMap = (HashMap<String, TPicDesc>) savedMinute
						.getObjectValue();
				for (String id : savedMap.keySet()) {
					if (thumbnailId.equals(id)) {
						savedMap.remove(id);
						del = true;
						log.info(">>>Delete unpassed thumbnail  success!");
					}
				}
			}
		}

		return del;
	}

}