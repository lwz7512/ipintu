package com.pintu.facade;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.pintu.beans.Applicant;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.StoryDetails;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
import com.pintu.beans.Tag;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
import com.pintu.beans.UserDetail;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;
import com.pintu.jobs.MidnightTask;
import com.pintu.tools.ImgDataProcessor;
import com.pintu.utils.EmailTemplate;
import com.pintu.utils.Encrypt;
import com.pintu.utils.MailSenderInfo;
import com.pintu.utils.PintuUtils;
import com.pintu.utils.SimpleMailSender;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class PintuServiceImplement implements PintuServiceInterface {
	
	private Logger log = Logger.getLogger(PintuServiceImplement.class);

	// 由Spring注入
	private DBAccessInterface dbVisitor;
	// 由Spring注入
	private CacheAccessInterface cacheVisitor;

	// 由Spring注入
	private ImgDataProcessor imgProcessor;

	private Properties propertyConfigurer;
	
	private Properties systemConfigurer;

	public void setSystemConfigurer(Properties systemConfigurer) {
		this.systemConfigurer = systemConfigurer;
	}

	public void setPropertyConfigurer(Properties propertyConfigurer) {
		this.propertyConfigurer = propertyConfigurer;
	}

	private String imagePath;

	// 设定输出的类型
	private static final String GIF = "image/gif;charset=UTF-8";

	private static final String JPG = "image/jpeg;charset=UTF-8";

	private static final String PNG = "image/png;charset=UTF-8";

	// Constructor
	public PintuServiceImplement() {
		// DO NOTHING CURRENTLY
	}

	public void setImgProcessor(ImgDataProcessor imgProcessor) {
		this.imgProcessor = imgProcessor;
	}

	public void setDbVisitor(DBAccessInterface dbVisitor) {
		this.dbVisitor = dbVisitor;
	}

	public void setCacheVisitor(CacheAccessInterface cacheVisitor) {
		this.cacheVisitor = cacheVisitor;
	}

	public DBAccessInterface getDbVisitor() {
		return dbVisitor;
	}

	public CacheAccessInterface getCacheVisitor() {
		return cacheVisitor;
	}

	@Override
	public List<TPicDesc> getInviteTpicsToday() {
		// 这个功能暂时不在1.0中实现，界面中没有设计
		return null;
	}

	@Override
	public void createTastePic(TastePic pic, String user) {
		System.out.println("3 Construct obj: pintuservice createTastePic");
		System.out.println("TastePic:" + pic.getFileType() + "   user:" + user);
		if (pic != null && user != null) {
			// 1. 构造TPicItem对象
			TPicItem tpicItem = new TPicItem();
			String pid = UUID.randomUUID().toString().replace("-", "")
					.substring(16);
			tpicItem.setId(pid);
			tpicItem.setName(pid + "." + pic.getFileType());
			tpicItem.setOwner(user);
			tpicItem.setTags(pic.getTags());
			tpicItem.setPublishTime(PintuUtils.getFormatNowTime());
			System.out.println("publishTime:" + tpicItem.getPublishTime());

			tpicItem.setDescription(pic.getDescription());
			tpicItem.setSource(pic.getSource());

			if (pic.getIsOriginal() > -1) {
				tpicItem.setIsOriginal(pic.getIsOriginal());
			} else {
				tpicItem.setIsOriginal(0);
			}
			tpicItem.setPass(1);

			// 2. 放入缓存
			cacheVisitor.cachePicture(tpicItem);
			
			//FIXME 更新用户的最后操作时间
			Long updateTime = System.currentTimeMillis();
			cacheVisitor.updateCachedUser(tpicItem.getOwner(), updateTime);
			
			
			// 3. 提交imgProcessor生成文件
			imgProcessor.createImageFile(pic.getRawImageData(), tpicItem);

			// 4. 入库的事情就交由同步工具CacheToDB来处理，这里就结束了！

		} else {
			// TODO
		}

	}

	@Override
	public void addStoryToPintu(Story story) {
		cacheVisitor.cacheStory(story);
		
		// 更新用户的最后操作时间
		Long updateTime = System.currentTimeMillis();
		cacheVisitor.updateCachedUser(story.getOwner(), updateTime);
	}

	@Override
	public void addVoteToStory(Vote vote) {
		cacheVisitor.cacheVote(vote);
	}

	@Override
	public List<TPicDesc> getCommunityTpics() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 将长字符串的毫秒数转化成分钟
	 * 
	 * @param longTime
	 * @return
	 */
	private int getMinutes(String longTime) {
		Long lTime = Long.parseLong(longTime);
		long minlong = lTime / (60 * 1000);
		int min = Math.round(minlong);
		return min;
	}

	@Override
	public List<TPicDesc> getTpicsByTime(String startTime, String endTime) {
		List<TPicDesc> resultList = new ArrayList<TPicDesc>();
		List<TPicDesc> thumbnailList = new ArrayList<TPicDesc>();
		int start = getMinutes(startTime);
		int end = getMinutes(endTime);

		for (int i = end; i >= start; i--) {
			List<TPicDesc> cacheList = cacheVisitor.getCachedThumbnail(String
					.valueOf(i));
			thumbnailList.addAll(cacheList);
			//控制画廊图片数量，超过32就不查了~
			if(thumbnailList.size() > Integer.parseInt(propertyConfigurer
					.getProperty("galleryImgNum"))){
				break;
			}
		}

		System.out.println("get cache thumbnial size:" + thumbnailList.size());

		if (thumbnailList != null) {
			for (int j = thumbnailList.size() - 1; j >= 0; j--) {
				resultList.add(thumbnailList.get(j));
			}
		}

		return resultList;
	}

	@Override
	public List<Wealth> getWealthDetails(String userId) {
		return dbVisitor.getOnesWealth(userId);
	}

	@Override
	public TPicDetails getTPicDetailsById(String tpId) {
		TPicDetails details = new TPicDetails();
		// 根据图片id到缓存中取图片的基本信息
		TPicItem item = cacheVisitor.getSpecificPic(tpId);
		// 若缓存里不存在该图片的信息则转向查数据库
		if ( item.getId() == null) {
			item = dbVisitor.getPictureById(tpId);
		}

		// 取一个品图的所有者，即用户ID
		String userId = item.getOwner();
		// 得到图片的所有者即userId,再到数据库里取出user的详细信息
		User user = this.getUserInfo(userId);
		int storyNum = cacheVisitor.getStoriesOfPic(tpId).size();
		if (storyNum == 0) {
			storyNum = dbVisitor.getStoriesOfPic(tpId).size();
		}
		details.setStoriesNum(storyNum);
		if (user != null) {
			details.setAuthor(user.getNickName());
			details.setScore(user.getScore());
			details.setLevel(user.getLevel());
			details.setAvatarImgPath(user.getAvatar());
		}
		details.setId(item.getId());
		details.setName(item.getName());
		details.setOwner(userId);
		details.setMobImgId(item.getMobImgId());
		details.setRawImgId(item.getRawImgId());
		details.setPublishTime(item.getPublishTime());
		details.setDescription(item.getDescription());
		details.setSource(item.getSource());
		details.setIsOriginal(item.getIsOriginal());
//		details.setBrowseCount(item.getBrowseCount());
		// 取一个图片详情时，我们认为这张图获得了一个点击量
		int counter = 1;

		// 在缓存的热图id里查看是否存在这个id,若存在直接给其点击量累加，否则将新的放到里面
		if (CacheAccessInterface.hotPicCacheIds.containsKey(item.getId())) {
			Integer value = CacheAccessInterface.hotPicCacheIds.get(item
					.getId());
			CacheAccessInterface.hotPicCacheIds.put(item.getId(), value
					+ counter);
		} else {
			CacheAccessInterface.hotPicCacheIds.put(item.getId(), counter);
		}

		details.setCounter(CacheAccessInterface.hotPicCacheIds.get(item.getId()));
		
		//要根据当天的点击量来累加从库里取出来的浏览次数~即得到最新的浏览次数
		details.setBrowseCount(item.getBrowseCount()+details.getCounter());
		
		return details;
	}

	@Override
	public List<StoryDetails> getStoryDetailsOfPic(String tpId) {
		List<StoryDetails> storyDeatilList = new ArrayList<StoryDetails>();
		List<Story> storyList = cacheVisitor.getStoriesOfPic(tpId);
		if (storyList.size() == 0) {
			storyList = dbVisitor.getStoriesOfPic(tpId);
		}
		if (storyList != null && storyList.size() > 0) {
			for (int i = 0; i < storyList.size(); i++) {
				StoryDetails storyDetail = new StoryDetails();
				String storyId = storyList.get(i).getId();
				String userId = storyList.get(i).getOwner();
				storyDetail.setId(storyId);
				String picId = storyList.get(i).getFollow();
				storyDetail.setFollow(picId);
				storyDetail.setOwner(userId);
				storyDetail.setPublishTime(storyList.get(i).getPublishTime());
				storyDetail.setContent(storyList.get(i).getContent());
				storyDetail.setClassical(storyList.get(i).getClassical());
				User user = this.getUserInfo(userId);
				if (user != null) {
					storyDetail.setAuthor(user.getAccount());
				}
				
				List<Vote> voteList = this.getVotesOfPic(picId);
				
				if (voteList != null && voteList.size() > 0) {
					for (int j = 0; j < voteList.size(); j++) {
						Vote vote = voteList.get(j);
						if (vote.getType().equals(Vote.COOL_TYPE)) {
							storyDetail.setCool(vote.getAmount());
						}
					}
				}else{
						storyDetail.setCool(0);
				}
//				if (voteList != null && voteList.size() > 0) {
//					for (int j = 0; j < voteList.size(); j++) {
//						Vote vote = voteList.get(j);
//						if (vote.getType().equals(Vote.FLOWER_TYPE)) {
//							storyDetail.setFlower(vote.getAmount());
//						} else if (vote.getType().equals(Vote.EGG_TYPE)) {
//							storyDetail.setEgg(vote.getAmount());
//						} else if (vote.getType().equals(Vote.HEART_TYPE)) {
//							storyDetail.setHeart(vote.getAmount());
//						} else if (vote.getType().equals(Vote.STAR_TYPE)) {
//							storyDetail.setStar(vote.getAmount());
//						}
//					}
//				} else {
//					storyDetail.setFlower(0);
//					storyDetail.setEgg(0);
//					storyDetail.setHeart(0);
//					storyDetail.setStar(0);
//				}
				storyDeatilList.add(storyDetail);
			}
		}
		return storyDeatilList;
	}


	@Override
	public List<Vote> getVotesOfPic(String picId) {
		List<Vote> voteList = dbVisitor.getVoteOfPic(picId);
		return voteList;
	}

	@Override
	public User getUserInfo(String userId) {
		User user = cacheVisitor.getUserById(userId);
		if (user.getId() == null) {
			user = dbVisitor.getUserById(userId);
		}
		// FIXME 添加发图数和发故事统计
		user.setStoryNum(dbVisitor.getStoryCountByUser(userId));
		user.setTpicNum(dbVisitor.getTPicCountByUser(userId));
		return user;
	}

	@Override
	public UserDetail getUserEstate(String userId) {
		UserDetail uDetail = new UserDetail();
		User user = cacheVisitor.getSpecificUser(userId);
		if (user.getId() == null) {
			user = dbVisitor.getUserById(userId);
		}
		uDetail.setId(userId);
		uDetail.setAccount(user.getAccount());
		uDetail.setAvatar(user.getAvatar());
		uDetail.setLevel(user.getLevel());
		uDetail.setScore(user.getScore());
		uDetail.setExchangeScore(user.getExchangeScore());
		// FIXME 这里给用户资产添加发图数和发故事统计
		uDetail.setStoryNum(dbVisitor.getStoryCountByUser(userId));
		uDetail.setTpicNum(dbVisitor.getTPicCountByUser(userId));

		List<Wealth> wealthList = this.getWealthDetails(userId);
		if (wealthList.size() > 0) {
			for (int i = 0; i < wealthList.size(); i++) {
				Wealth wealth = wealthList.get(i);
				if (wealth.getType().equals(Wealth.ONE_YUAN)) {
					uDetail.setSeaShell(wealth.getAmount());
				} else if (wealth.getType().equals(Wealth.TEN_YUAN)) {
					uDetail.setCopperShell(wealth.getAmount());
				} else if (wealth.getType().equals(Wealth.FIFTY_YUAN)) {
					uDetail.setSilverShell(wealth.getAmount());
				} else if (wealth.getType().equals(Wealth.HUNDRED_YUAN)) {
					uDetail.setGoldShell(wealth.getAmount());
				}
			}
		}

		return uDetail;
	}

	@Override
	public List<Message> getUserMessages(String userId) {
		List<Message> resList = new ArrayList<Message>();
		List<Message> msgList = dbVisitor.getUserMessages(userId);
		for(int i = 0;i<msgList.size();i ++){
			Message msg = msgList.get(i);
			//为消息的接收者添加名字和肖像
			String receiverId = msg.getReceiver();
			User receiveUser = this.getUserInfo(receiverId);
			String receiverName = receiveUser.getAccount();
			String receiverAvatar = receiveUser.getAvatar();
			msg.setReceiverName(receiverName);
			msg.setReceiverAvatar(receiverAvatar);
			//为消息发送者添加名字和肖像
			String senderId = msg.getSender();
			User sendUser = this.getUserInfo(senderId);
			String senderName = sendUser.getAccount();
			String senderAvatar = sendUser.getAvatar();
			msg.setSenderName(senderName);
			msg.setSenderAvatar(senderAvatar);
			resList.add(msg);
		}
		return resList;
	}

	@Override
	public String sendMessage(Message msg) {
		int i = dbVisitor.insertMessage(msg);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public String changeMsgState(List<String> msgIdList) {
		int rows = dbVisitor.updateMsg(msgIdList);
		if (rows > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public void saveImagePathToProcessor(String filePath) {
		this.imgProcessor.setImagePath(filePath);
		imagePath = filePath;
	}

	@Override
	public void getImageFile(String picId, HttpServletResponse res) {
		File file = new File(imagePath + File.separator + "defaultImage.jpg");
		File jpgFile = new File(imagePath + File.separator + picId + ".jpg");
		File pngFile = new File(imagePath + File.separator + picId + ".png");
		File gifFile = new File(imagePath + File.separator + picId + ".gif");

		if (jpgFile.exists()) {
			writeJPGImage(jpgFile, res);
		} else if (pngFile.exists()) {
			writePNGImage(pngFile, res);
		} else if (gifFile.exists()) {
			writeGIFImage(gifFile, res);
		} else {
			writeJPGImage(file, res);
		}
	}

	private void writeJPGImage(File file, HttpServletResponse res) {
		try {
			res.setContentType(JPG);
			OutputStream out = res.getOutputStream();
			InputStream imageIn = new FileInputStream(file);
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn);
			// 得到编码后的图片对象
			BufferedImage image = decoder.decodeAsBufferedImage();
			// 得到输出的编码器
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);
			// 对图片进行输出编码
			imageIn.close();
			// 关闭文件流
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writePNGImage(File file, HttpServletResponse res) {
		res.setContentType(PNG);
		getOutInfo(file, res);
	}

	private void writeGIFImage(File file, HttpServletResponse res) {
		res.setContentType(GIF);
		getOutInfo(file, res);
	}

	private void getOutInfo(File file, HttpServletResponse res) {
		try {
			OutputStream out = res.getOutputStream();
			InputStream imageIn = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(imageIn);
			// 输入缓冲流
			BufferedOutputStream bos = new BufferedOutputStream(out);
			// 输出缓冲流
			byte data[] = new byte[4096];
			// 缓冲字节数
			int size = 0;
			size = bis.read(data);
			while (size != -1) {
				bos.write(data, 0, size);
				size = bis.read(data);
			}
			bis.close();
			bos.flush();
			// 清空输出缓冲流
			bos.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据综略图的文件名得到系统路径下的文件信息
	 */
	@Override
	public File getThumbnail(String thumbnailName) {
		String thumbnail = imagePath + File.separator + thumbnailName;
		File file = new File(thumbnail);
		return file;
	}

	/**
	 * path里就包含了路径信息和图片名称
	 */
	@Override
	public void getImageByPath(String path, HttpServletResponse res) {
		File defaultFile = new File(imagePath + File.separator + "avatarImg"
				+ File.separator + "defaultAvatar.png");
		String type = path.substring(path.lastIndexOf(".") + 1);
		File file = new File(path);
		if (file.exists()) {
			if (type.toLowerCase().equals("jpg")) {
				writeJPGImage(file, res);
			} else if (type.toLowerCase().equals("png")) {
				writePNGImage(file, res);
			} else if (type.toLowerCase().equals("gif")) {
				writeGIFImage(file, res);
			} else {
				writePNGImage(defaultFile, res);
			}
		} else {
			writePNGImage(defaultFile, res);
		}

	}

	@Override
	public List<TPicDetails> getHotPicture() {
		List<TPicDetails> hotList = new ArrayList<TPicDetails>();
		Map<String, Integer> map = CacheAccessInterface.hotPicCacheIds;

		if (map.size() == 0) {
			return hotList;
		}

		int[] counterArray = new int[map.size()];
		int i = 0;

		for (Integer counter : map.values()) {
			// 点击量取配置文件中的设置值
			if (counter > Integer.parseInt(propertyConfigurer
					.getProperty("hotPintuCounter"))) {
				Array.set(counterArray, i, counter);
				i++;
			}
		}

		if (counterArray != null && counterArray.length > 1) {
			sortArray(counterArray);
		}

		// 根据排序后的结果从大到小，取得缓存中的tpId，并查出详情
		for (int j = 0; j < counterArray.length; j++) {
			if (counterArray[j] == 0)
				break;
			for (String tpId : map.keySet()) {
				if (map.get(tpId) == counterArray[j]) {
					TPicDetails tpic = this.getTPicDetailsById(tpId);
					hotList.add(tpic);
				}
			}
		}

		return hotList;
	}

	// 给数组排序
	private void sortArray(int[] array) {
		int left, right, num;
		int middle, j;

		for (int i = 1; i < array.length; i++) {
			// 准备
			left = 0;
			right = i - 1;
			num = array[i];

			// 二分法查找插入位置
			while (right >= left) {
				// 指向已排序好的中间位置
				middle = (left + right) / 2;

				if (num > array[middle]) {
					// 插入的元素在左区间
					right = middle - 1;
				} else {
					// 插入的元素在右区间
					left = middle + 1;
				}
			}

			// 后移排序码小于R[i]的记录
			for (j = i - 1; j >= left; j--) {
				array[j + 1] = array[j];
			}
			// 插入
			array[left] = num;
		}

	}

	@Override
	public List<StoryDetails> getClassicalPintu() {
		List<StoryDetails> classicalList = new ArrayList<StoryDetails>();
		// List<Story> list = dbVisitor.getClassicalPintu();
		// 取得前一天更新的经典story
		StringBuffer strBuffer = MidnightTask.newClassicalStoryIds;
		//判断如果有经典的就去数据库中取，没有不取
		if(strBuffer.length() >0){
			List<Story> list = dbVisitor
					.getClassicalPintuByIds(MidnightTask.newClassicalStoryIds
							.toString());
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Story story = list.get(i);
					String userId = story.getOwner();
					User user = this.getUserInfo(userId);
					StoryDetails sDetails = new StoryDetails();
					sDetails.setAuthor(user.getAccount());
					sDetails.setId(story.getId());
					sDetails.setOwner(userId);
					sDetails.setAvatarImgPath(user.getAvatar());
					sDetails.setPublishTime(story.getPublishTime());
					sDetails.setContent(story.getContent());
					sDetails.setClassical(story.getClassical());
					sDetails.setFollow(story.getFollow());
					classicalList.add(sDetails);
				}
			}
		}
		return classicalList;
	}

	@Override
	public boolean checkExistFavorite(String userId, String picId) {
		int i = dbVisitor.checkExistFavorite(userId, picId);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String markFavoritePic(Favorite fav) {
		int i = dbVisitor.insertFavorite(fav);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public String deleteOneFavorite(String fId) {
		int i = dbVisitor.deleteFavoriteById(fId);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public List<TPicItem> getFavoriteTpics(String userId, int pageNum) {
		int pageSize = Integer.parseInt(propertyConfigurer
				.getProperty("pageSize"));
		List<TPicItem> picList = dbVisitor.getFavoriteTpics(userId, pageNum,
				pageSize);
		return  calcPicItemCount(picList);
	}

	@Override
	public List<TPicItem> getTpicsByUser(String userId, int pageNum) {
		int pageSize = Integer.parseInt(propertyConfigurer
				.getProperty("pageSize"));
		List<TPicItem> list = dbVisitor.getTpicsByUser(userId, pageNum,
				pageSize);
		return  calcPicItemCount(list);
	}

	@Override
	public List<StoryDetails> getStroiesByUser(String userId, int pageNum) {
		List<StoryDetails> resList = new ArrayList<StoryDetails>();
		int pageSize = Integer.parseInt(propertyConfigurer
				.getProperty("pageSize"));
		List<Story> sList = dbVisitor.getStoriesByUser(userId, pageNum,
				pageSize);
		for (int i = 0; i < sList.size(); i++) {
			StoryDetails details = new StoryDetails();
			String storyId = sList.get(i).getId();
			details.setId(storyId);
			String picId = sList.get(i).getFollow();
			details.setFollow(picId);
			details.setOwner(userId);
			details.setPublishTime(sList.get(i).getPublishTime());
			details.setContent(sList.get(i).getContent());
			details.setClassical(sList.get(i).getClassical());
			User user = this.getUserInfo(userId);
			if (user != null) {
				details.setAuthor(user.getAccount());
			}
			List<Vote> voteList = this.getVotesOfPic(picId);
			if (voteList != null && voteList.size() > 0) {
				for (int j = 0; j < voteList.size(); j++) {
					Vote vote = voteList.get(j);
					if (vote.getType().equals(Vote.COOL_TYPE)) {
						details.setCool(vote.getAmount());
					}
				}
			}else{
				details.setCool(0);
			}
//			if (voteList != null && voteList.size() > 0) {
//				for (int j = 0; j < voteList.size(); j++) {
//					Vote vote = voteList.get(j);
//					if (vote.getType().equals(Vote.COOL_TYPE)) {
//						details.setCool(vote.getAmount());
//					}
//					if (vote.getType().equals(Vote.FLOWER_TYPE)) {
//						details.setFlower(vote.getAmount());
//					} else if (vote.getType().equals(Vote.EGG_TYPE)) {
//						details.setEgg(vote.getAmount());
//					} else if (vote.getType().equals(Vote.HEART_TYPE)) {
//						details.setHeart(vote.getAmount());
//					} else if (vote.getType().equals(Vote.STAR_TYPE)) {
//						details.setStar(vote.getAmount());
//					}
//				}
//			} else {
//				details.setFlower(0);
//				details.setEgg(0);
//				details.setHeart(0);
//				details.setStar(0);
//			}
			resList.add(details);
		}

		return resList;
	}

	@Override
	public List<Gift> getExchangeableGifts() {
		List<Gift> result = dbVisitor.getExchangeableGifts();
		return result;
	}

	@Override
	public List<Event> getCommunityEvents() {
		String today = PintuUtils.getToday();
		List<Event> result = dbVisitor.getCommunityEvents(today);
		return result;
	}

	@Override
	public String publishExchangeableGift(Gift gift) {
		int i = dbVisitor.insertGift(gift);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public String publishCommunityEvent(Event event) {
		int i = dbVisitor.insertEvent(event);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public List<TPicDesc> getLatestPic() {
		List<TPicDesc> resultList = new ArrayList<TPicDesc>();
		List<TPicDesc> thumbnailList = new ArrayList<TPicDesc>();
		//获取最近一小时内发的品图
		int nowMinute = getMinutes(String.valueOf(new Date().getTime()));
		int hourBefore = nowMinute - Integer.parseInt(propertyConfigurer.getProperty("latestTimeSpan"));
		for (int i = nowMinute; i > hourBefore ; i--) {
			List<TPicDesc> cacheList = cacheVisitor.getCachedThumbnail(String
					.valueOf(i));
			thumbnailList.addAll(cacheList);
		}

		if (thumbnailList != null) {
			for (int j = thumbnailList.size() - 1; j >= 0; j--) {
				resultList.add(thumbnailList.get(j));
			}
		}

		return resultList;
	}

	@Override
	public String getExistUser(String account, String pwd) {
		String md5Pwd = Encrypt.encrypt(pwd);
		User user = dbVisitor.getExistUser(account);
		if(user != null && user.getId() != null){
			if(user.getPwd().equals(md5Pwd)){
				//用户登录成功后将用户信息放缓存
				user.setLastUpdateTime(System.currentTimeMillis());
				cacheVisitor.cacheUser(user);
				return user.getRole()+"@"+user.getId();
			}else{
				return systemConfigurer.getProperty("pwdError").toString();
			}
		}	
		return systemConfigurer.getProperty("userNotExist").toString();
	}
	
	@Override
	public int validateAccount(String account) {
		User user = dbVisitor.getExistUser(account);
		if(user != null && user.getId() != null){
			return 1;
		}
		return 0;
	}
	
	private User createUser(String userId, String account, String pwd) {
		User user = new User();
		user.setId(userId);
		user.setAccount(account);
		user.setPwd(Encrypt.encrypt(pwd));
		user.setRegisterTime(PintuUtils.getFormatNowTime());
		return user;
	}

	@Override
	public String registerUser(String account, String pwd, String code) {
		String userId = dbVisitor.getExistApplicant(account, code);
    	if(!"".equals(userId)){
	    		User user = createUser(userId,account,pwd);
	    		int i = dbVisitor.insertUser(user);
	        	if(i == 1){
	        		//注册成功用户放缓存
	        		user.setLastUpdateTime(System.currentTimeMillis());
	        		cacheVisitor.cacheUser(user);
	        		//删除临时
	        		int j = dbVisitor.deleteTempUser(userId);
	        		if(j ==1){
	        			log.info(" >>>Delete rgistered user."+userId);
	        		}
	        		return  systemConfigurer.getProperty("registerSuccess").toString();
	        	}else{
	        		return  systemConfigurer.getProperty("registerError").toString();
	        	}
	    }else{
	    	return systemConfigurer.getProperty("applyPrompt").toString();
	    }
	}
	

	@Override
	public String sendApply(String account, String reason) {
		User tempUser = this.createApplicant(account, reason);
		int m = dbVisitor.insertApplicant(tempUser);
		if(m ==1){
			return systemConfigurer.getProperty("applyProcess").toString();
		}
		return systemConfigurer.getProperty("applyError").toString();
	}
	
	private User createApplicant(String account, String reason) {
		User user = new User();
		user.setId(PintuUtils.generateUID());
		user.setAccount(account);
		user.setApplyReason(reason);
		return user;
	}

	/**
	 * 发邮件
	 * @param receiverMail
	 * @param content
	 */
	private void sendMail(String toAddress,String content){
		MailSenderInfo mailInfo = new MailSenderInfo();
//		mailInfo.setMailServerPort("25");
//		mailInfo.setValidate(true);
		String host = propertyConfigurer.getProperty("mailServiceHost").toString();
		String username = propertyConfigurer.getProperty("serviceMailUsername").toString();
		String password = propertyConfigurer.getProperty("serviceMailPassword").toString(); 
		String address = propertyConfigurer.getProperty("serviceMailAddress").toString(); 
		mailInfo.setMailServerHost(host);
		mailInfo.setUserName(username);
		mailInfo.setPassword(password);// 邮箱密码
		mailInfo.setFromAddress(address);
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject("申请注册爱品图通知");
		//邮件内容
		mailInfo.setContent(content);
		// 发送html格式
		SimpleMailSender.sendHtmlMail(mailInfo);
	}

	@Override
	public String acceptApply(String id, String account,String url,String opt) {
		//发邮件啊发邮件java实现发邮件
		String inviteCode = PintuUtils.generateInviteCode(); 
		if(opt.equals("refuse")){
			String content = propertyConfigurer.getProperty("templateNo").toString();
			sendMail(account,content);
			//拒绝加入系统将其从临时用户表中删除
			int j = dbVisitor.deleteTempUser(id);
    		if(j ==1){
    			log.info(">>>Delete refused temp user."+id);
    		}
			
		}else if(opt.equals("approve")){
			String content = propertyConfigurer.getProperty("templateYes").toString();
			String resContent = editTemplate(url,account,inviteCode,content);
			int i = dbVisitor.updateApplicant(inviteCode,id);
			if(i==1){
				//数据库用户临时表更新成功发邮件
				sendMail(account,resContent);
				return systemConfigurer.getProperty("applyEmailPrompt").toString();
				
			}
		}
		
		return systemConfigurer.getProperty("contactServicePrompt").toString();
	
	}

	//从porerties文件中取出邮件模板，并修改相应内容
	private String editTemplate(String url, String account, String inviteCode,
			String content) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("url", url);
		map.put("account", account);
		map.put("inviteCode", inviteCode);
		String result = EmailTemplate.convert(content, map);
		return result;
	}

	@Override
	public List<Applicant> getApplicant() {
		List<Applicant> list = dbVisitor.getApplicant();
		return list;
	}

	@Override
	public boolean examineUser(String userId) {
		User cacheUser = cacheVisitor.getUserById(userId);
		if(cacheUser.getId()!=null){
			return true;
		}else{
			User dbUser = dbVisitor.getUserById(userId);
			if(dbUser.getId()!=null){
				return true;
			}
		}
		return false;
	}

	@Override
	public List<TPicDetails> collectStatistics() {
		List<TPicDetails> picList = new ArrayList<TPicDetails>();
		if(MidnightTask.collectList.size() > 0){
			picList = MidnightTask.collectList;
		}else{
		   picList = dbVisitor.collectStatistics();
		}
		return calcPicDetailCount(picList);
	}

	@Override
	public List<TPicDetails> classicalStatistics() {
		List<TPicDetails> picList = new ArrayList<TPicDetails>();
		if(MidnightTask.classicalList.size() > 0){
			picList = MidnightTask.classicalList;
		}else{
			picList = dbVisitor.classicalStatistics();
		}
		return calcPicDetailCount(picList);
	}
	
	//用于处理所有查询图片的处理实时点击量问题
	private List<TPicDetails> calcPicDetailCount(List<TPicDetails> picList){
		List<TPicDetails> resultList = new ArrayList<TPicDetails>();
		if(picList != null && picList.size()>0){
			for (int i = 0; i < picList.size(); i++) {
				TPicDetails pic = picList.get(i);
				if(CacheAccessInterface.hotPicCacheIds.containsKey(pic.getId())){
					int newCount = pic.getBrowseCount() + CacheAccessInterface.hotPicCacheIds.get(pic.getId());
					pic.setBrowseCount(newCount);
				}
				resultList.add(pic);
			}
		}
		return resultList;
	}

	//用于处理所有查询图片的处理实时点击量问题
	private List<TPicItem> calcPicItemCount(List<TPicItem> picList){
		List<TPicItem> resultList = new ArrayList<TPicItem>();
		if(picList != null && picList.size()>0){
			for (int i = 0; i < picList.size(); i++) {
				TPicItem pic = picList.get(i);
				if(CacheAccessInterface.hotPicCacheIds.containsKey(pic.getId())){
					int newCount = pic.getBrowseCount() + CacheAccessInterface.hotPicCacheIds.get(pic.getId());
					pic.setBrowseCount(newCount);
				}
				resultList.add(pic);
			}
		}
		return resultList;
	}

	@Override
	public List<TPicDetails> getGalleryForWeb(int pageNum) {
		int pageSize = Integer.parseInt(propertyConfigurer
				.getProperty("pageSizeForWeb"));
		List<TPicDetails> picList = dbVisitor.getGalleryForWeb(pageNum,pageSize);
		return  calcPicDetailCount(picList);
	}

	@Override
	public List<TPicDetails> searchByTag(String tags) {
		List<TPicDetails> resList = new  ArrayList<TPicDetails>();
		List<TPicDetails> andList = new  ArrayList<TPicDetails>();
		List<TPicDetails> orList = new  ArrayList<TPicDetails>();
		StringBuffer tagOr = new StringBuffer();
		//在这里将用空格分隔的tags转变成sql可识别的'',''
		String[] tagArr = tags.split(" ");
		if(tagArr.length>0){
			for(int i=0;i<tagArr.length;i++){
				if(!"".equals(tagArr[i].trim())){
					if(tagOr.length()>0){
						tagOr.append(",");
					}
					tagOr.append("'");
					tagOr.append(tagArr[i]);
					tagOr.append("'");
				}
			}
			andList = dbVisitor.searchByTagAnd(tagArr);
			orList = dbVisitor.searchByTagOr(tagOr.toString());
		}
		
		resList = combineResult(andList,orList);
		
		return  calcPicDetailCount(resList);
	}

	//将and的结果与or的结果合并
	private List<TPicDetails> combineResult(List<TPicDetails> andList,
			List<TPicDetails> orList) {
		for(int m=0;m<andList.size();m++){
			TPicDetails tPic = andList.get(m);
			String id=tPic.getId();
			for(int n=0;n<orList.size();n++){
				TPicDetails pic=orList.get(n);
				if(id.equals(pic.getId())){
					orList.remove(n);
				}
			}
		}	
		andList.addAll(orList);
		return andList;
	}

	@Override
	public List<Tag> getHotTags() {
		int topNum = Integer.parseInt(propertyConfigurer
				.getProperty("pageSizeForWeb"));
		List<Tag> list=dbVisitor.getHotTags(topNum);
		return list;
	}

	@Override
	public List<Tag> geSystemTags() {
		List<Tag> list=dbVisitor.geSystemTags();
		return list;
	}
	
	@Override
	public String deleteOneComment(String sId) {
		int i = dbVisitor.deleteCmtById(sId);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public String deleteOnePicture(String pId) {
		int i = dbVisitor.deletePictureById(pId);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public List<TPicDesc> getThumbnailsByTag(String tagId, int pageNum) {
		int pageSize = Integer.parseInt(propertyConfigurer
				.getProperty("pageSizeForWeb"));
		List<TPicDesc> list = dbVisitor.getThumbnailByTag(tagId,pageNum,pageSize);
		return list;
	}

	@Override
	public List<User> getPicDaren() {
		if(MidnightTask.picDarenList.size() > 0){
			return MidnightTask.picDarenList;
		}else{
			List<User> list = dbVisitor.getPicDaren();
			return list;
		}
	}

	@Override
	public List<User> getCmtDaren() {
		if(MidnightTask.cmtDarenList.size() > 0){
			return MidnightTask.cmtDarenList;
		}else{
			List<User> list = dbVisitor.getCmtDaren();
			return list;
		}
	
	}


	// TODO, 实现其他接口方法
	

}
