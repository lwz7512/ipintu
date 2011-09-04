package com.pintu.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

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

	// 用户登录到系统时缓存用户信息
	public void cacheUser(User user) {
		Long updateTime = Long.parseLong(user.getLastUpdateTime());
		long minlong = updateTime / (60 * 1000);
		int min = Math.round(minlong);
		String key = String.valueOf(min);
		Element savedMinute = userCache.get(key);
		if (savedMinute == null) {
			// 保存的分钟数，缩略图id,缩略图对象
			Element elmt = new Element(key, new HashMap<String, User>());
			@SuppressWarnings("unchecked")
			HashMap<String, User> usersUpdateInOneMinute = (HashMap<String, User>) elmt
					.getObjectValue();
			usersUpdateInOneMinute.put(user.getId(), user);
			synchronized (userCache) {
				userCache.put(elmt);
			}
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, User> savedMap = (HashMap<String, User>) savedMinute
					.getObjectValue();
			savedMap.put(user.getId(), user);
			Element ele = new Element(key, savedMap);
			synchronized (userCache) {
				userCache.put(ele);
			}
		}
	}

	// 更新登录到系统中缓存的用户的最后操作时间 updateTime为分钟数
	@SuppressWarnings("unchecked")
	public void updateCachedUser(String userId, String updateTime) {
		// Long upTime = Long.parseLong(updateTime);
		// long minlong = upTime/(60*1000);
		// int min = Math.round(minlong);
		String key = String.valueOf(updateTime);
		synchronized (userCache) {
			List<String> timeList = userCache.getKeys();
			for (int i = 0; i < timeList.size(); i++) {
				HashMap<String, User> userMap = (HashMap<String, User>) userCache
						.get(timeList.get(i)).getObjectValue();
				User user = userMap.get(userId);
				if (user != null) {
					user.setLastUpdateTime(updateTime);
					userMap.put(userId, user);
					Element ele = new Element(key, userMap);
					userCache.put(ele);
				}
			}
		}
	}

	// 根据更新时间取出一个时间点的活跃用户信息
	@SuppressWarnings("unchecked")
	public List<User> getLiveUserByMinute(String minute) {
		List<User> userList = new ArrayList<User>();
		synchronized (userCache) {
			Element savedMinute = userCache.get(minute);
			if (savedMinute != null) {
				HashMap<String, User> savedMap = (HashMap<String, User>) savedMinute
						.getObjectValue();
				if (savedMap != null) {
					userList.addAll(savedMap.values());
				}
			}
		}
		return userList;
	}


	public void cachePicture(String userId, String picId, TPicItem pic) {
		Element savedUser = pictureCache.get(userId);
		if (savedUser == null) {
			// 图片所有者，图id,图对象
			Element elmt = new Element(userId, new HashMap<String, TPicItem>());
			@SuppressWarnings("unchecked")
			HashMap<String, TPicItem> picsForOneUser = (HashMap<String, TPicItem>) elmt
					.getObjectValue();
			picsForOneUser.put(picId, pic);
			synchronized (pictureCache) {
				pictureCache.put(elmt);
			}
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, TPicItem> savedMap = (HashMap<String, TPicItem>) savedUser
					.getObjectValue();
			savedMap.put(picId, pic);
			Element ele = new Element(userId, savedMap);
			synchronized (pictureCache) {
				pictureCache.put(ele);
			}
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

	public List<Object> getCachedPictureByUid(List<String> ids) {
		List<Object> list = new ArrayList<Object>();
		synchronized (pictureCache) {
			for (String userId : ids) {
				Element savedUser = pictureCache.get(userId);
				if (savedUser != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, TPicItem> picMap = (HashMap<String, TPicItem>) savedUser
							.getObjectValue();
					if (picMap != null) {
						list.addAll(picMap.values());
					}
				}
			}
		}
		return list;
	}

	public List<Object> getCachedCommentByPid(List<String> ids) {
		List<Object> list = new ArrayList<Object>();
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

	public List<Object> getCachedStoryByPid(List<String> ids) {
		List<Object> list = new ArrayList<Object>();
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

	public List<Object> getCachedVoteBySid(List<String> ids) {
		List<Object> list = new ArrayList<Object>();
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

	public List<Object> getCachedPicture(Map<String, LinkedList<String>> map) {
		List<Object> list = new ArrayList<Object>();
		synchronized (pictureCache) {
			for (String userId : map.keySet()) {
				Element savedUser = pictureCache.get(userId);
				if (savedUser != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, TPicItem> picMap = (HashMap<String, TPicItem>) savedUser
							.getObjectValue();
					for (LinkedList<String> picIdList : map.values()) {
						if (picIdList != null) {
							for (int i = 0; i < picIdList.size(); i++) {
								if (picMap != null) {
									list.add(picMap.get(picIdList.get(i)));
								}
							}
						}
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
		synchronized (pictureCache) {
			for (String storyId : map.keySet()) {
				Element savedStory = pictureCache.get(storyId);
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

}
