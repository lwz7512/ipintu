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

import com.pintu.beans.Comment;
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

	private Cache commentCache;

	private Cache storyCache;

	private Cache userCache;

	private Cache voteCache;

	private Cache thumbnailCache;

	// private Logger log = Logger.getLogger(PintuCache.class);

	public PintuCache() {
<<<<<<< HEAD

		cacheManager = CacheManager.getInstance();

=======
		
		cacheManager = CacheManager.getInstance();
		
>>>>>>> b702bc6fd55cc0c96408d05048ebb3747cf8f515
		initUserCache();

		pictureCache = cacheManager.getCache("picturecache");
		commentCache = cacheManager.getCache("commentcache");
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
<<<<<<< HEAD
				1000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
=======
				10000).memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
>>>>>>> b702bc6fd55cc0c96408d05048ebb3747cf8f515
				.overflowToDisk(false).eternal(false).timeToLiveSeconds(7200)
				.timeToIdleSeconds(3600).diskPersistent(false);
		cacheConfig.addSearchable(searchable);

		userCache = new Cache(cacheConfig);
		cacheManager.addCache(userCache);
	}

	
	public void traceAll() {
		System.out.println("--------------- trace begin: -----------------");
		System.out.println(">>> commentCache status: "
				+ pictureCache.getStatus() + " commentCache size: "
				+ commentCache.getKeysNoDuplicateCheck().size());
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

	public void cacheComment(String picId, String cmtId, Comment cmt) {
		Element savedPic = commentCache.get(picId);
		if (savedPic == null) {
			// 评论所属图片，评论id,评论对象
			Element elmt = new Element(picId, new HashMap<String, TPicItem>());
			@SuppressWarnings("unchecked")
			HashMap<String, Comment> cmtsForOnePic = (HashMap<String, Comment>) elmt
					.getObjectValue();
			cmtsForOnePic.put(cmtId, cmt);
			synchronized (commentCache) {
				commentCache.put(elmt);
			}
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Comment> savedMap = (HashMap<String, Comment>) savedPic
					.getObjectValue();
			savedMap.put(cmtId, cmt);
			Element ele = new Element(picId, savedMap);
			synchronized (commentCache) {
				commentCache.put(ele);
			}
		}
	}

	public void cacheStory(String picId, String storyId, Story story) {
		Element savedPic = storyCache.get(picId);
		if (savedPic == null) {
			// 故事所属图片，故事id,故事对象
			Element elmt = new Element(picId, new HashMap<String, TPicItem>());
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
			Element ele = new Element(picId, savedMap);
			synchronized (storyCache) {
				storyCache.put(ele);
			}
		}
	}

	public void cacheVote(String storyId, String voteId, Vote vote) {
		Element savedStory = voteCache.get(storyId);
		if (savedStory == null) {
			// 投票所属故事，投票id,投票对象
			Element elmt = new Element(storyId, new HashMap<String, TPicItem>());
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
			Element ele = new Element(storyId, savedMap);
			synchronized (voteCache) {
				voteCache.put(ele);
			}
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
			Element ele = new Element(key, savedMap);
			synchronized (thumbnailCache) {
				thumbnailCache.put(ele);
			}
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
	public List<Object> getCachedPicture(List<String> picIds) {
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

	public List<Comment> getCachedCommentByPid(List<String> ids) {
		List<Comment> list = new ArrayList<Comment>();
		synchronized (commentCache) {
			for (String picId : ids) {
				Element savedPic = commentCache.get(picId);
				if (savedPic != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, Comment> cmtMap = (HashMap<String, Comment>) savedPic
							.getObjectValue();
					if (cmtMap != null) {
						list.addAll(cmtMap.values());
					}
				}
			}
		}
		return list;
	}

	public List<Story> getCachedStoryByPid(List<String> ids) {
		List<Story> list = new ArrayList<Story>();
		synchronized (storyCache) {
			for (String picId : ids) {
				Element savedPic = storyCache.get(picId);
				if (savedPic != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, Story> storyMap = (HashMap<String, Story>) savedPic
							.getObjectValue();
					if (storyMap != null) {
						list.addAll(storyMap.values());
					}
				}
			}
		}
		return list;
	}

	public List<Vote> getCachedVoteBySid(List<String> ids) {
		List<Vote> list = new ArrayList<Vote>();
		synchronized (voteCache) {
			for (String storyId : ids) {
				Element savedStory = voteCache.get(storyId);
				if (savedStory != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, Vote> voteMap = (HashMap<String, Vote>) savedStory
							.getObjectValue();
					if (voteMap != null) {
						list.addAll(voteMap.values());
					}
				}
			}
		}
		return list;
	}

	public List<Object> getCachedComment(Map<String, LinkedList<String>> map) {
		List<Object> list = new ArrayList<Object>();
		synchronized (commentCache) {
			for (String picId : map.keySet()) {
				Element savedPic = commentCache.get(picId);
				if (savedPic != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, Comment> cmtMap = (HashMap<String, Comment>) savedPic
							.getObjectValue();
					for (LinkedList<String> cmtIdList : map.values()) {
						if (cmtIdList != null) {
							for (int i = 0; i < cmtIdList.size(); i++) {
								if (cmtMap != null) {
									list.add(cmtMap.get(cmtIdList.get(i)));
								}
							}
						}
					}
				}
			}
		}

		return list;
	}

	public List<Object> getCachedStory(Map<String, LinkedList<String>> map) {
		List<Object> list = new ArrayList<Object>();
		synchronized (storyCache) {
			for (String picId : map.keySet()) {
				Element savedPic = storyCache.get(picId);
				if (savedPic != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, Story> storyMap = (HashMap<String, Story>) savedPic
							.getObjectValue();
					for (LinkedList<String> storyIdList : map.values()) {
						if (storyIdList != null) {
							for (int i = 0; i < storyIdList.size(); i++) {
								if (storyMap != null) {
									list.add(storyMap.get(storyIdList.get(i)));
								}
							}
						}
					}
				}
			}
		}

		return list;
	}

	public List<Object> getCachedVote(Map<String, LinkedList<String>> map) {
		List<Object> list = new ArrayList<Object>();
		synchronized (voteCache) {
			for (String storyId : map.keySet()) {
				Element savedStory = voteCache.get(storyId);
				if (savedStory != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, TPicItem> voteMap = (HashMap<String, TPicItem>) savedStory
							.getObjectValue();
					for (LinkedList<String> voteIdList : map.values()) {
						if (voteIdList != null) {
							for (int i = 0; i < voteIdList.size(); i++) {
								if (voteMap != null) {
									list.add(voteMap.get(voteIdList.get(i)));
								}
							}
						}
					}
				}
			}
		}

		return list;
	}

	public boolean removeTPicById(String id) {
		synchronized (pictureCache) {
				return pictureCache.remove(id);
			}
	}

}
