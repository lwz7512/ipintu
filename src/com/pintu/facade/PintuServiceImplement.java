package com.pintu.facade;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import weibo4j.Oauth;
import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.http.AccessToken;
import weibo4j.http.ImageItem;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import com.pintu.beans.AccessUser;
import com.pintu.beans.Applicant;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.ImageDesc;
import com.pintu.beans.Message;
import com.pintu.beans.Note;
import com.pintu.beans.Story;
import com.pintu.beans.StoryDetails;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
import com.pintu.beans.TPicReview;
import com.pintu.beans.Tag;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
import com.pintu.beans.UserDetail;
import com.pintu.beans.UserExtend;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;
import com.pintu.jobs.MidnightTask;
import com.pintu.tools.ImageHelper;
import com.pintu.tools.ImgDataProcessor;
import com.pintu.tools.MailSenderTask;
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
		log.debug("3 Construct obj: pintuservice createTastePic");
		log.debug("TastePic:" + pic.getFileType() + "   user:" + user);
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
			log.debug("publishTime:" + tpicItem.getPublishTime());

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

			// 更新用户的最后操作时间
			updateCacheUser(tpicItem.getOwner());
			
			// 3. 提交imgProcessor生成文件
			imgProcessor.createImageFile(pic.getRawImageData(), tpicItem);

			// 4. 入库的事情就交由同步工具CacheToDB来处理，这里就结束了！

		} else {
			// TODO
		}

	}

	// FIXME 在更新最后更新时间之前检查缓存里是否有用户存在
	private void updateCacheUser(String userId) {
		User user = cacheVisitor.getUserById(userId);
		if (user.getId() == null) {
			user = dbVisitor.getUserById(userId);
			cacheVisitor.cacheUser(user);
			log.info("Check the user cache before update user lastUpdateTime...");
		}
		Long updateTime = System.currentTimeMillis();
		cacheVisitor.updateCachedUser(userId, updateTime);

	}

	@Override
	public void addStoryToPintu(Story story) {
		cacheVisitor.cacheStory(story);
		// 更新用户的最后操作时间
		updateCacheUser(story.getOwner());
	}

	@Override
	public void addVoteToPic(Vote vote) {
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
			// 控制画廊图片数量，超过32就不查了~
			if (thumbnailList.size() > Integer.parseInt(propertyConfigurer
					.getProperty("galleryImgNum"))) {
				break;
			}
		}

		log.debug("get cache thumbnial size:" + thumbnailList.size());

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
		if (item.getId() == null) {
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
			// TODO这里要和缓存的加上
			details.setScore(user.getScore());
			details.setLevel(user.getLevel());
			details.setAvatarImgPath(user.getAvatar());
		}

		// 根据图片id要查出标签和被赞个数
		String picId = item.getId();

		// 查图片标签
		String tags = this.getTagsById(picId);
		details.setTags(tags);

		// 查图片被赞次数
		int coolCount = this.getPicCoolCount(picId);
		details.setCoolCount(coolCount);

		details.setId(picId);
		details.setName(item.getName());
		details.setOwner(userId);
		details.setMobImgId(item.getMobImgId());
		details.setRawImgId(item.getRawImgId());
		details.setPublishTime(item.getPublishTime());
		details.setDescription(item.getDescription());
		details.setSource(item.getSource());
		details.setIsOriginal(item.getIsOriginal());
		// details.setBrowseCount(item.getBrowseCount());
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

		// 要根据当天的点击量来累加从库里取出来的浏览次数~即得到最新的浏览次数
		details.setBrowseCount(item.getBrowseCount() + details.getCounter());
		return details;
	}

	private String getTagsById(String picId) {
		StringBuffer tags = new StringBuffer();
		List<Tag> tagList = dbVisitor.getPicTagsById(picId);
		if (tagList.size() > 0) {
			for (int i = 0; i < tagList.size(); i++) {
				Tag tag = tagList.get(i);
				if (tags.length() > 0) {
					tags.append(" ");
				}
				tags.append(tag.getName());
			}
		}
		return tags.toString().trim();
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
					//这里需要改一下，账户改成昵称
					storyDetail.setAuthor(user.getNickName());
				}

				// List<Vote> voteList = this.getVotesOfPic(picId);
				// if (voteList != null && voteList.size() > 0) {
				// for (int j = 0; j < voteList.size(); j++) {
				// Vote vote = voteList.get(j);
				// if (vote.getType().equals(Vote.FLOWER_TYPE)) {
				// storyDetail.setFlower(vote.getAmount());
				// } else if (vote.getType().equals(Vote.EGG_TYPE)) {
				// storyDetail.setEgg(vote.getAmount());
				// } else if (vote.getType().equals(Vote.HEART_TYPE)) {
				// storyDetail.setHeart(vote.getAmount());
				// } else if (vote.getType().equals(Vote.STAR_TYPE)) {
				// storyDetail.setStar(vote.getAmount());
				// }
				// }
				// } else {
				// storyDetail.setFlower(0);
				// storyDetail.setEgg(0);
				// storyDetail.setHeart(0);
				// storyDetail.setStar(0);
				// }
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
			cacheVisitor.cacheUser(user);
		}
		user.setPwd("");
		// FIXME 添加发图数和发故事统计
		user.setStoryNum(dbVisitor.getStoryCountByUser(userId));
		user.setTpicNum(dbVisitor.getTPicCountByUser(userId));
		return user;
	}

	@Override
	public UserDetail getUserEstate(String userId) {
		UserDetail uDetail = new UserDetail();
		User user = this.getUserInfo(userId);
		uDetail.setId(userId);
		uDetail.setAccount(user.getAccount());
		uDetail.setAvatar(user.getAvatar());
		uDetail.setLevel(user.getLevel());
		uDetail.setNickName(user.getNickName());
		uDetail.setScore(user.getScore());
		uDetail.setExchangeScore(user.getExchangeScore());
		uDetail.setStoryNum(user.getStoryNum());
		uDetail.setTpicNum(user.getTpicNum());

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
		for (int i = 0; i < msgList.size(); i++) {
			Message msg = msgList.get(i);
			// 为消息的接收者添加名字和肖像
			String receiverId = msg.getReceiver();
			User receiveUser = this.getUserInfo(receiverId);
			String receiverName = receiveUser.getNickName();
			String receiverAvatar = receiveUser.getAvatar();
			msg.setReceiverName(receiverName);
			msg.setReceiverAvatar(receiverAvatar);
			// 为消息发送者添加名字和肖像
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

	// FIXME TODO 先存缓存里取image对象，若没有再从文件系统里拿
	@Override
	public void getImageFile(String picId, HttpServletResponse res) {

		ImageDesc imgDesc = cacheVisitor.getCachedImage(picId);
		if (imgDesc != null) {
			// 从缓存里写图片
			writeImageFromCache(imgDesc.getImage(), imgDesc.getType(), res);
		} else {
			// 从文件系统中返回
			File defaultFile = new File(imagePath + File.separator
					+ "defaultImage.png");
			File pngFile = new File(imagePath + File.separator + picId + ".png");
			File jpgFile = new File(imagePath + File.separator + picId + ".jpg");
			File jpegFile = new File(imagePath + File.separator + picId + ".jpeg");
			File gifFile = new File(imagePath + File.separator + picId + ".gif");
			File imgFile = defaultFile;
			String fileType = "png";
			if (jpgFile.exists()) {
				imgFile = jpgFile;
				fileType="jpg";
			} else if (jpegFile.exists()) {
				imgFile = jpegFile;
				fileType="jpeg";
			} else if (gifFile.exists()) {
				imgFile = gifFile;
				fileType="gif";
			} else if (pngFile.exists()) {
				fileType="png";
				imgFile = pngFile;
			}
			
			writeImageFromSystem(imgFile, fileType, res);
		}
	}

	// 从文件系统中返回
	private void writeImageFromSystem(File imgFile, String fileType,
			HttpServletResponse res) {
		try {
			InputStream imageIn = new FileInputStream(imgFile);
			if ("jpg".equalsIgnoreCase(fileType) || "jpeg".equals(fileType)){
				writeJPGImage(imageIn, res);
			} else if ("png".equalsIgnoreCase(fileType)) {
				writePNGImage(imageIn, res);
			} else if ("gif".equalsIgnoreCase(fileType)) {
				writeGIFImage(imageIn, res);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		log.debug(">>>getImageFile From fileSystem");
	}

	// 从缓存里写图片
	private void writeImageFromCache(Image img, String fileType,
			HttpServletResponse res) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageOutputStream imOut = ImageIO.createImageOutputStream(bos);
			ImageIO.write((BufferedImage) img, fileType, imOut);
			InputStream imageIn = new ByteArrayInputStream(bos.toByteArray());
			if ("jpg".equalsIgnoreCase(fileType) || "jpeg".equals(fileType)){
				writeJPGImage(imageIn, res);
			} else if ("png".equalsIgnoreCase(fileType)) {
				writePNGImage(imageIn, res);
			} else if ("gif".equalsIgnoreCase(fileType)) {
				writeGIFImage(imageIn, res);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug(">>>getImageFile From imageCache");
	}

	private void writeJPGImage(InputStream imageIn, HttpServletResponse res) {
		try {
			res.setContentType(JPG);
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn);
			// 得到编码后的图片对象
			BufferedImage image = decoder.decodeAsBufferedImage();
			// 得到输出的编码器
			OutputStream out = res.getOutputStream();
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

	private void writePNGImage(InputStream imageIn, HttpServletResponse res) {
		res.setContentType(PNG);
		getOutInfo(imageIn, res);
	}

	private void writeGIFImage(InputStream imageIn, HttpServletResponse res) {
		res.setContentType(GIF);
		getOutInfo(imageIn, res);
	}

	private void getOutInfo(InputStream imageIn, HttpServletResponse res) {
		try {
			OutputStream out = res.getOutputStream();
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
	 * 根据图片的文件名得到系统路径下的文件信息
	 */
	@Override
	public File getThumbnailOrMobImage(String imgName) {
		String imgPath = imagePath + File.separator + imgName;
		File file = new File(imgPath);
		return file;
	}

	/**
	 * path里就包含了路径信息和图片名称
	 */
	@Override
	public void getImageByPath(String path, HttpServletResponse res) {
		try {
			//默认头像
			File defaultAvatar = new File(imagePath + File.separator
					+ "avatarImg" + File.separator + "defaultAvatar.png");
			InputStream defaultIn = new FileInputStream(defaultAvatar);
			
			String type = path.substring(path.lastIndexOf(".") + 1);
			File file = new File(path);
			if (file.exists()) {
				InputStream imageIn = new FileInputStream(file);
				if (type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg")) {
					writeJPGImage(imageIn, res);
				} else if (type.toLowerCase().equals("png")) {
					writePNGImage(imageIn, res);
				} else if (type.toLowerCase().equals("gif")) {
					writeGIFImage(imageIn, res);
				} else {
					writePNGImage(defaultIn, res);
				}
			} else {
				writePNGImage(defaultIn, res);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		// 判断如果有经典的就去数据库中取，没有不取
		if (strBuffer.length() > 0) {
			List<Story> list = dbVisitor
					.getClassicalPintuByIds(MidnightTask.newClassicalStoryIds
							.toString());
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Story story = list.get(i);
					String userId = story.getOwner();
					User user = this.getUserInfo(userId);
					StoryDetails sDetails = new StoryDetails();
					sDetails.setAuthor(user.getNickName());
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
		return calcPicItemCount(picList);
	}

	@Override
	public List<TPicItem> getTpicsByUser(String userId, int pageNum) {
		int pageSize = Integer.parseInt(propertyConfigurer
				.getProperty("pageSize"));
		List<TPicItem> list = dbVisitor.getTpicsByUser(userId, pageNum,
				pageSize);
		return calcPicItemCount(list);
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
				details.setAuthor(user.getNickName());
			}
			// List<Vote> voteList = this.getVotesOfPic(picId);
			// if (voteList != null && voteList.size() > 0) {
			// for (int j = 0; j < voteList.size(); j++) {
			// Vote vote = voteList.get(j);
			// if (vote.getType().equals(Vote.COOL_TYPE)) {
			// details.setCool(vote.getAmount());
			// }
			// if (vote.getType().equals(Vote.FLOWER_TYPE)) {
			// details.setFlower(vote.getAmount());
			// } else if (vote.getType().equals(Vote.EGG_TYPE)) {
			// details.setEgg(vote.getAmount());
			// } else if (vote.getType().equals(Vote.HEART_TYPE)) {
			// details.setHeart(vote.getAmount());
			// } else if (vote.getType().equals(Vote.STAR_TYPE)) {
			// details.setStar(vote.getAmount());
			// }
			// }
			// } else {
			// details.setFlower(0);
			// details.setEgg(0);
			// details.setHeart(0);
			// details.setStar(0);
			// }
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
	public List<TPicReview> getLatestTPicDesc() {
		List<TPicReview> resultList = new ArrayList<TPicReview>();
		List<TPicDesc> thumbnailList = new ArrayList<TPicDesc>();
		// 获取最近一小时内发的品图
		int nowMinute = getMinutes(String.valueOf(new Date().getTime()));
		int hourBefore = nowMinute
				- Integer.parseInt(propertyConfigurer
						.getProperty("latestTimeSpan"));
		for (int i = nowMinute; i > hourBefore; i--) {
			List<TPicDesc> cacheList = cacheVisitor.getCachedThumbnail(String
					.valueOf(i));
			thumbnailList.addAll(cacheList);
		}

		if (thumbnailList != null) {
			// for (int j = thumbnailList.size() - 1; j >= 0; j--) {
			// resultList.add(thumbnailList.get(j));
			// }
			for (int i = 0; i < thumbnailList.size(); i++) {
				TPicDesc tpicDesc = thumbnailList.get(i);
				String picId = tpicDesc.getTpId();
				TPicItem tpicItem = cacheVisitor.getSpecificPic(picId);
				TPicReview tpicReview = new TPicReview();
				String userId = tpicItem.getOwner();
				User user = this.getUserInfo(userId);
				tpicReview.setAuthor(user.getNickName());
				tpicReview.setUserId(userId);
				tpicReview.setDescription(tpicItem.getDescription());
				tpicReview.setTags(tpicItem.getTags());
				tpicReview.setPublishTime(tpicItem.getPublishTime());
				tpicReview.setThumbnailId(tpicDesc.getThumbnailId());
				tpicReview.setTpId(picId);
				tpicReview.setCreationTime(tpicDesc.getCreationTime());
				resultList.add(tpicReview);
			}
		}

		// cacheVisitor.getSpecificPic(pid)

		return resultList;
	}

	@Override
	public String getExistUser(String account, String pwd) {
		String md5Pwd = Encrypt.encrypt(pwd);
		User user = dbVisitor.getExistUser(account);
		if (user != null && user.getId() != null) {
			if (user.getPwd().equals(md5Pwd)) {
				// 用户登录成功后将用户信息放缓存
				user.setLastUpdateTime(System.currentTimeMillis());
				cacheVisitor.cacheUser(user);
				return user.getRole() + "@" + user.getId();
			} else {
				return systemConfigurer.getProperty("pwdError").toString();
			}
		}
		return systemConfigurer.getProperty("userNotExist").toString();
	}

	@Override
	public int validateAccount(String account) {
		User user = dbVisitor.getExistUser(account);
		if (user != null && user.getId() != null) {
			return 1;
		}
		return 0;
	}

	private User createUser(String userId, String account, String pwd,
			String nick) {
		User user = new User();
		user.setId(userId);
		user.setAccount(account);
		user.setNickName(nick);
		user.setPwd(Encrypt.encrypt(pwd));
		user.setRegisterTime(PintuUtils.getFormatNowTime());
		return user;
	}

	@Override
	public String registerUser(String account, String pwd, String code,
			String nick) {
		String userId = dbVisitor.getExistApplicant(account, code);
		if("".equals(userId)){
			userId = dbVisitor.getExistApplicant("", code);
		}
		if (!"".equals(userId)) {
			User user = createUser(userId, account, pwd, nick);
			int i = dbVisitor.insertUser(user);
			if (i == 1) {
				// 注册成功用户放缓存
				user.setLastUpdateTime(System.currentTimeMillis());
				cacheVisitor.cacheUser(user);
				// 删除已用邀请码
				int j = dbVisitor.deleteUsedCode(code);
				if (j == 1) {
					log.info(" >>>Delete rgistered user." + userId);
				}
				//FIXME 注册成功，返回 role@userId 字符串
				return user.getRole() + "@" + user.getId();
//				return systemConfigurer.getProperty("registerSuccess")
//						.toString();
			} else {
				return systemConfigurer.getProperty("registerError").toString();
			}
		} else {
			return systemConfigurer.getProperty("applyPrompt").toString();
		}
	}

	@Override
	public String sendApply(String account, String reason) {
		MailSenderTask mailTask = new MailSenderTask();
		mailTask.setAccount(account);
		mailTask.setReason(reason);
		mailTask.setDbVisitor(dbVisitor);
		mailTask.setPropertyConfigurer(propertyConfigurer);
		//用线程处理发邮件任务
		new Thread(mailTask).start();
		return systemConfigurer.getProperty("applyProcess").toString();
	}

	/**
	 * 发邮件
	 * 
	 * @param receiverMail
	 * @param content
	 */
	private void sendMail(String toAddress, String content) {
		MailSenderInfo mailInfo = new MailSenderInfo();
		// mailInfo.setMailServerPort("25");
		// mailInfo.setValidate(true);
		String host = propertyConfigurer.getProperty("mailServiceHost")
				.toString();
		String username = propertyConfigurer.getProperty("serviceMailUsername")
				.toString();
		String password = propertyConfigurer.getProperty("serviceMailPassword")
				.toString();
		String address = propertyConfigurer.getProperty("serviceMailAddress")
				.toString();
		mailInfo.setMailServerHost(host);
		mailInfo.setUserName(username);
		mailInfo.setPassword(password);// 邮箱密码
		mailInfo.setFromAddress(address);
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject("爱品图通知");
		// 邮件内容
		mailInfo.setContent(content);
		// 发送html格式
		SimpleMailSender.sendHtmlMail(mailInfo);
	}

	@Override
	public String acceptApply(String account, String url, String opt) {
		// 发邮件啊发邮件java实现发邮件
		String inviteCode = PintuUtils.generateInviteCode();
		if (opt.equals("refuse")) {
			String content = propertyConfigurer.getProperty("templateNo")
					.toString();
			try{
				sendMail(account, content);
			}catch(Exception e){
				e.printStackTrace();
				log.debug("Reject one's apply error......");
			}
		
			// 拒绝加入系统将其从临时用户表中删除
			int j = dbVisitor.deleteTempUser(account);
			if (j == 1) {
				log.info(">>>delete refused temp user." + account);
			}

		} else if (opt.equals("approve")) {
			String content = propertyConfigurer.getProperty("templateYes")
					.toString();
			String resContent = editTemplate(url, account, inviteCode, content);
			int i = dbVisitor.updateApplicant(inviteCode,account);
			if (i == 1) {
				// 数据库用户临时表更新成功发邮件
				try{
					sendMail(account, resContent);
				}catch(Exception e){
					e.printStackTrace();
					log.debug("Approve one's apply error......");
				}
				return systemConfigurer.getProperty("applyEmailPrompt")
						.toString();

			}
		}

		return systemConfigurer.getProperty("contactServicePrompt").toString();
	}

	// 从porerties文件中取出邮件模板，并修改相应内容
	private String editTemplate(String url, String account, String inviteCode,
			String content) {
		Map<String, String> map = new HashMap<String, String>();
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
		if (cacheUser.getId() != null) {
			return true;
		} else {
			User dbUser = dbVisitor.getUserById(userId);
			if (dbUser.getId() != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<TPicDetails> collectStatistics() {
		List<TPicDetails> picList = new ArrayList<TPicDetails>();
		if (MidnightTask.collectList.size() > 0) {
			picList = MidnightTask.collectList;
		} else {
			picList = dbVisitor.collectStatistics();
		}
		return calcPicDetailCount(picList);
	}

	@Override
	public List<TPicDetails> classicalStatistics() {
		int classicalNum = Integer.parseInt(propertyConfigurer
				.getProperty("classicalBrowseCount"));
		List<TPicDetails> picList = new ArrayList<TPicDetails>();
		if (MidnightTask.classicalList.size() > 0) {
			picList = MidnightTask.classicalList;
		} else {
			picList = dbVisitor.classicalStatistics(classicalNum);
		}
		return calcPicDetailCount(picList);
	}

	// 用于处理所有查询图片的浏览量，评论数，被赞数
	private List<TPicDetails> calcPicDetailCount(List<TPicDetails> picList) {
		List<TPicDetails> resultList = new ArrayList<TPicDetails>();
		if (picList != null && picList.size() > 0) {
			for (int i = 0; i < picList.size(); i++) {
				TPicDetails pic = picList.get(i);
				String picId = pic.getId();
				// 处理实时点击
				if (CacheAccessInterface.hotPicCacheIds.containsKey(picId)) {
					int newCount = pic.getBrowseCount()
							+ CacheAccessInterface.hotPicCacheIds.get(pic
									.getId());
					pic.setBrowseCount(newCount);
				}

				// 处理图片评论数
				int storyNum = cacheVisitor.getStoriesOfPic(picId).size();
				if (storyNum == 0) {
					storyNum = dbVisitor.getStoriesOfPic(picId).size();
				}
				pic.setStoriesNum(storyNum);

				// 处理喜欢数
				int coolCount = this.getPicCoolCount(picId);
				pic.setCoolCount(coolCount);

				// 加上tag图片标签
				String tags = this.getTagsById(picId);
				pic.setTags(tags);

				resultList.add(pic);
			}
		}
		return resultList;
	}

	// 用于处理所有查询图片的处理实时点击量问题
	private List<TPicItem> calcPicItemCount(List<TPicItem> picList) {
		List<TPicItem> resultList = new ArrayList<TPicItem>();
		if (picList != null && picList.size() > 0) {
			for (int i = 0; i < picList.size(); i++) {
				TPicItem pic = picList.get(i);
				if (CacheAccessInterface.hotPicCacheIds
						.containsKey(pic.getId())) {
					int newCount = pic.getBrowseCount()
							+ CacheAccessInterface.hotPicCacheIds.get(pic
									.getId());
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
		List<TPicDetails> picList = dbVisitor.getGalleryForWeb(pageNum,
				pageSize);
		return calcPicDetailCount(picList);
	}

	@Override
	public List<TPicDetails> searchByTag(String tags) {
		List<TPicDetails> resList = new ArrayList<TPicDetails>();
		List<TPicDetails> andList = new ArrayList<TPicDetails>();
		List<TPicDetails> orList = new ArrayList<TPicDetails>();
		StringBuffer tagOr = new StringBuffer();
		// 在这里将用空格分隔的tags转变成sql可识别的'',''
		String[] tagArr = tags.split(" ");
		if (tagArr.length > 0) {
			for (int i = 0; i < tagArr.length; i++) {
				if (!"".equals(tagArr[i].trim())) {
					if (tagOr.length() > 0) {
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

		resList = combineResult(andList, orList);

		return calcPicDetailCount(resList);
	}

	// 将and的结果与or的结果合并,并限制最多返回64条
	private List<TPicDetails> combineResult(List<TPicDetails> andList,
			List<TPicDetails> orList) {
		for (int m = 0; m < andList.size(); m++) {
			TPicDetails tPic = andList.get(m);
			String id = tPic.getId();
			for (int n = 0; n < orList.size(); n++) {
				TPicDetails pic = orList.get(n);
				if (id.equals(pic.getId())) {
					orList.remove(n);
				}
			}
		}
		andList.addAll(orList);
		//FIXME 限制条数返回
//		List<TPicDetails> resList = new ArrayList<TPicDetails>();
//		for(int i = 0; i<andList.size(); i++){
//			if(i==64){
//				break;
//			}
//			TPicDetails tPic = andList.get(i);
//			resList.add(tPic);
//		}
//		return resList;
		return andList;
	}

	@Override
	public List<Tag> getHotTags() {
		int topNum = Integer.parseInt(propertyConfigurer
				.getProperty("pageSizeForWeb"));
		List<Tag> list = dbVisitor.getHotTags(topNum);
		return list;
	}

	@Override
	public List<Tag> geSystemTags() {
		List<Tag> list = dbVisitor.geSystemTags();
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
				.getProperty("galleryImgNum"));
		List<TPicDesc> list = dbVisitor.getThumbnailByTag(tagId, pageNum,
				pageSize);
		return list;
	}

	@Override
	public List<User> getPicDaren() {
		if (MidnightTask.picDarenList.size() > 0) {
			return MidnightTask.picDarenList;
		} else {
			List<User> list = dbVisitor.getPicDaren();
			return list;
		}
	}

	@Override
	public List<User> getCmtDaren() {
		if (MidnightTask.cmtDarenList.size() > 0) {
			return MidnightTask.cmtDarenList;
		} else {
			List<User> list = dbVisitor.getCmtDaren();
			return list;
		}

	}

	@Override
	public int getPicCoolCount(String picId) {
		int coolCount = dbVisitor.getPicCoolCount(picId);
		return coolCount;
	}

	@Override
	public int examineNickname(String nickName) {
		int result = dbVisitor.getExistNickname(nickName);
		return result;
	}

	@Override
	public String modifyPasswordById(String userId, String newPwd) {
		String md5Pwd = Encrypt.encrypt(newPwd);
		int i = dbVisitor.updatePassword(md5Pwd, userId);
		if (i > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public int confirmPassword(String userId, String password) {
		String md5Pwd = Encrypt.encrypt(password);
		int result = dbVisitor.confirmPassword(userId, md5Pwd);
		return result;
	}

	@Override
	public String createAvatarImg(FileItem avatarData, String userId,
			String nickName) {
		String type = "";
		if (avatarData != null) {
			String fileName = avatarData.getName();
			int dotPos = fileName.lastIndexOf(".");
			type = fileName.substring(dotPos);
		}

		String path = imagePath + File.separator + "avatarImg" + File.separator
				+ userId + type;
		log.debug(path);

		try {
			ImageHelper.handleImage(avatarData, 64, 64, path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 修改用户资料（修改头像，昵称），改库的同时更新一下缓存
		cacheVisitor.updateUserInfo(userId, path, nickName);

		// 更新库
		int result = dbVisitor.updateAvatarAndNickname(path, nickName, userId);
		if (result > 0) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public List<TPicDesc> getRandGallery() {
		int size = Integer.parseInt(propertyConfigurer
				.getProperty("galleryImgNum"));
		List<TPicDesc> picList = dbVisitor.getRandGallery(size);
		return picList;
	}

	@Override
	public List<User> getActiveUserRandking() {
		int size = Integer.parseInt(propertyConfigurer
				.getProperty("activeUserNum"));
		List<User> list = dbVisitor.getActiveUserRandking(size);
		return list;
	}

	@Override
	public String reviewPictureById(String picId, String creationTime) {
		
		Long createTime = 0l;
		// 删除缓存中的缩略图
		if(creationTime.contains("-")){
			createTime = PintuUtils.parseToDate(creationTime).getTime();
		}else{
			createTime = Long.parseLong(creationTime);
		}
		
		boolean flag = cacheVisitor.removeThumbnail(
				createTime, picId + TPicDesc.THUMBNIAL);

		// 更新服务器
		int i = dbVisitor.reviewPictureById(picId);
		if (i > 0 && flag) {
			return systemConfigurer.getProperty("rightPrompt").toString();
		} else {
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public String createInviteCode() {
		StringBuffer result = new StringBuffer();
		int i=0;
		String oldCode = "";
		for(i=0;i<10;i++){
			Applicant temp = new Applicant();
			String newCode = PintuUtils.generateInviteCode();
			if(newCode.equals(oldCode)){
				newCode = PintuUtils.generateInviteCode();
			}
			result.append(newCode+",");
			String id = PintuUtils.generateUID();
			temp.setId(id);
			temp.setInviteCode(newCode);
			temp.setApplyReason("I like it");
			temp.setPassed(1);
			temp.setAccount("");
			temp.setApplyTime(PintuUtils.getFormatNowTime());
			int rows = dbVisitor.insertApplicant(temp);
			if(rows == 1){
				oldCode = newCode;
			}
		}
		return result.toString();
	}

	@Override
	public int checkApplicant(String account) {
		int res = 1;
		User user = dbVisitor.getExistUser(account);
		int applicant = dbVisitor.getExistApplicant(account);
		if ((user != null && user.getId() != null) || applicant >0) {
			res = 1;
		}else{
			res = 0;
		}
		return res;
	}

	@Override
	public String retrievePwd(String account) {
		//检查用户是否存在，若存在则重置密码为123，并给用户发邮件让其以123为密码登录再修改密码，已完成重置
		User user =dbVisitor.getExistUser(account);
		if(user != null && user.getId() != null){
			String password = Encrypt.encrypt("123");
			int rows = dbVisitor.updatePassword(password, user.getId());
			if(rows == 1){
				String content = propertyConfigurer.getProperty("templateRetrieve")
						.toString();
				try{
					sendMail(account,content);
				}catch(Exception e){
					e.printStackTrace();
					log.debug("Retrieve password error ......");
				}
			}
			return systemConfigurer.getProperty("rightPrompt").toString();
		}else{
			return systemConfigurer.getProperty("wrongPrompt").toString();
		}
	}

	@Override
	public int checkAcceptApplicant(String account) {
		int applicant = dbVisitor.getAcceptedApplicant(account);
		return applicant;
	}

	@Override
	public String createAdImg(FileItem adData) {
		String type = "";
		if (adData != null) {
			String fileName = adData.getName();
			int dotPos = fileName.lastIndexOf(".");
			type = fileName.substring(dotPos);
		}

		Date date = new Date();//获取当前时间
		SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMddHHmmss");
		String newfileName = sdfFileName.format(date);//文件名称
		
		String path = imagePath + File.separator + "adsImg" + File.separator
				+ newfileName + type;
		try {
			BufferedInputStream in = new BufferedInputStream(adData.getInputStream());
			// 获得文件输入流
			BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(new File(path)));// 获得文件输出流
			Streams.copy(in, outStream, true);// 开始把文件写到你指定的上传文件夹
		} catch (IOException e) {
			e.printStackTrace();
		}
		//上传成功，则插入数据库
		if (new File(path).exists()) {
			//保存到数据库
			System.out.println("保存成功"+path);
		}
		return path;
	}


	@Override
	public void getImgByRelativePath(String relativePath,
			HttpServletResponse res) {
		try {
			//默认广告图片
			File defaultImg = new File(imagePath + File.separator
					+ "adsImg" + File.separator + "defaultAdImg.png");
			InputStream defaultIn = new FileInputStream(defaultImg);
			
			String type = relativePath.substring(relativePath.lastIndexOf(".") + 1);
			File file = new File(imagePath+relativePath);
			if (file.exists()) {
				InputStream imageIn = new FileInputStream(file);
				if (type.toLowerCase().equals("jpg") || type.toLowerCase().equals("jpeg")) {
					writeJPGImage(imageIn, res);
				} else if (type.toLowerCase().equals("png")) {
					writePNGImage(imageIn, res);
				} else if (type.toLowerCase().equals("gif")) {
					writeGIFImage(imageIn, res);
				} else {
					writePNGImage(defaultIn, res);
				}
			} else {
				writePNGImage(defaultIn, res);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public AccessUser getAccessTokenByCode(String code) {
		AccessUser accessUser = new AccessUser();
		Oauth oauth = new Oauth();
		AccessToken token = null;
		try{
			token = oauth.getAccessTokenByCode(code);
		} catch (WeiboException e) {
			if(401 == e.getStatusCode()){
				log.info("Unable to get the access token.");
			}else{
				e.printStackTrace();
			}
		}
		
		if(token == null){
			log.info("code is out date");
			return accessUser;
		}
		
		//获取token后操作库（直接存或更新）
		String userId = this.optWeiboUser(token);
		
		accessUser.setUserId(userId);
		accessUser.setUid(token.getUid());
		accessUser.setAccessToken(token.getAccessToken());
		accessUser.setExpireIn(token.getExpireIn());
		
		return accessUser;
	}

	
	private String optWeiboUser(AccessToken token) {
		String userId = "";
		String uid = token.getUid();
		userId = dbVisitor.getExtendUser(uid);
		if(!"".equals(userId) && userId != null){
			//如果当前用微博登录的用户存在，更新表
			boolean flag = updateWeiboUser(token,uid,userId);
			if(flag){
				log.info("update weibo user success");
			}else{
				log.info("update weibo user error");
			}
		}else{
			//不存在，新插入记录到表
			userId =addWeiboUser(token);
			if(!"".equals(userId) && userId != null){
				log.info("new weibo user success");
			}else{
				log.info("new weibo user error");
			}
		}
		return userId;
	}
	
	//将微博登录用户更新库，原user表和扩展表
	private boolean updateWeiboUser(AccessToken token, String uid, String userId) {
		weibo4j.model.User wbUser = this.getUserByToken(token);
		UserExtend userExtend = generateUserExtend("",token ,wbUser);
		int rows = dbVisitor.updateExtendUser(userExtend,uid);
		
		String avatar = wbUser.getAvatarLarge();
		String nickName = wbUser.getName();
		int lines = dbVisitor.updateAvatarAndNickname(avatar, nickName, userId);
		
		if(rows == 1 && lines == 1){
			log.info("update user and extend success");
			return true;
		}else if(rows == 1 && lines == 0){
			log.info("update user error");
		}else if(rows == 0 && lines == 1){
			log.info("update extend error");
		}else{
			log.info("update user and extend error");
		}
		return false;
	}

	//将微博登录用户存库包括两部分，存原user表和扩展表
	private String addWeiboUser(AccessToken token) {
		String newId = PintuUtils.generateUID();
		weibo4j.model.User user = this.getUserByToken(token);
		
		UserExtend userExtend = generateUserExtend(newId,token ,user);
		int rows = dbVisitor.addExtendUser(userExtend);
		
		User iptUser = this.generateUser(newId,user);
		int lines = dbVisitor.insertUser(iptUser);
		
		if(rows == 1 && lines == 1){
			log.info("insert user and extend success");
		}else if(rows == 1 && lines == 0){
			log.info("insert user error");
		}else if(rows == 0 && lines == 1){
			log.info("insert extend error");
		}else{
			log.info("insert user and extend error");
		}
		return newId;
	}

	private User generateUser(String newId,weibo4j.model.User user) {
		User iptUser = new User();
		iptUser.setId(newId);
		iptUser.setAccount(user.getId()+"@ipintu.com");
		//FIXME 这里需要修改，若都用一样的邮箱和密码，会出现问题
		String pwd = user.getId().substring(0,6);
		iptUser.setPwd(Encrypt.encrypt(pwd));
		iptUser.setAvatar(user.getAvatarLarge());
		iptUser.setNickName(user.getName());
		iptUser.setRole("weibo");
		iptUser.setRegisterTime(PintuUtils.getFormatNowTime());
		return iptUser;
	}

	//根据token和uid获取用户信息
	private weibo4j.model.User  getUserByToken(AccessToken token) {
		weibo4j.model.User wbUser = null;
		String accessToken = token.getAccessToken();
		String uid = token.getUid();
		
		Weibo weibo = new Weibo();
		weibo.setToken(accessToken);
		Users um = new Users();
		try {
			wbUser = um.showUserById(uid);
			log.info(wbUser.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return wbUser;
	}

	private UserExtend generateUserExtend(String newId,AccessToken token, weibo4j.model.User user){
		UserExtend userExtend = new UserExtend();
		String timespan = token.getExpireIn();
		Long now = System.currentTimeMillis();
		String expiration = PintuUtils.formatLong(now + Long.parseLong(timespan)*1000);
		
		userExtend.setId(newId);
		userExtend.setUid(token.getUid());
		userExtend.setToken(token.getAccessToken());
		userExtend.setTokenExpiration(expiration);
		
		userExtend.setGender(user.getGender());
		userExtend.setLocation(user.getLocation());
		userExtend.setDescripttion(user.getDescription());
		userExtend.setPersonalUrl(user.getUrl());
		
		//联系方式这里没有
		userExtend.setContract("");
		
		return userExtend;
	}

	@Override
	public String forwardToWeibo(String userId, String picId) {
		// 这里主要分两步 1、根据userId去找token 2、根据picId取图片原图与描述+广告语@爱品图 3、转发内容到微博
		String token = dbVisitor.getTokenById(userId);
		
		TPicItem pic = dbVisitor.getPictureById(picId);
		String description = pic.getDescription();
		String imgPath = pic.getRawImgPath();
		
		//取标语模板
		String banner = propertyConfigurer.getProperty("bannerTemplate");
		String text = description +"　"+ banner;
		
		boolean flag = uploadToWeibo(token,imgPath,text);
		return String.valueOf(flag);
	}
	
	//转发内容到新浪微博
	private boolean uploadToWeibo(String token, String imgPath, String text) {
		
		try{
			Weibo weibo = new Weibo();
			weibo.setToken(token);
			try{
				byte[] content= readFileImage(imgPath);
				System.out.println("content length:" + content.length);
				ImageItem pic=new ImageItem("pic",content);

				String s=java.net.URLEncoder.encode( text,"utf-8");
				Timeline tl = new Timeline();
				Status status=tl.UploadStatus(s, pic);

				log.info("Successfully upload the status to ["
						+status.getText()+"].");
			}catch(Exception e1){
				e1.printStackTrace();
				log.info("WeiboException: invalid_access_token.");
				return false;
			}
		}catch(Exception ioe){
			ioe.printStackTrace();
			log.info("Failed to read the system input.");
			return false;
		}
		
		return true;
	}
	
	private static byte[] readFileImage(String filename)throws IOException{
		BufferedInputStream bufferedInputStream=new BufferedInputStream(
				new FileInputStream(filename));
		int len =bufferedInputStream.available();
		byte[] bytes=new byte[len];
		int r=bufferedInputStream.read(bytes);
		if(len !=r){
			bytes=null;
			throw new IOException("读取文件不正确");
		}
		bufferedInputStream.close();
		return bytes;
	}

	@Override
	public List<Note> getCommunityNotes(int pageNum) {
		List<Note> list = dbVisitor.getCommunityNotes(pageNum);
		return combinCount(list);
	}
	
	//将缓存中的关注数与感兴趣数累加到要返回的数据里
	private List<Note> combinCount(List<Note> list){
		List<Note> resList = new ArrayList<Note>();
		for(int i = 0 ; i < list.size() ; i++){
			Note note = list.get(i);
			//加关注数
			int attentionCount = note.getAttention() ; 
			if (CacheAccessInterface.noteAttentionMap.containsKey(note.getId())) {
				Integer value = CacheAccessInterface.noteAttentionMap.get(note
						.getId());
				note.setAttention(value+ attentionCount);
			}
			//加感兴趣数
			int interestCount = note.getInterest() ;
			if (CacheAccessInterface.noteInterestMap.containsKey(note.getId())) {
				Integer value = CacheAccessInterface.noteInterestMap.get(note
						.getId());
				note.setInterest(value + interestCount);
			}
			
			resList.add(note);
		}
		return resList;
	}

	@Override
	public String addNote(String userId, String type, String title,
			String content) {
		boolean flag = false;
		Note note = generateNote(userId,type,title,content);
		int rows = dbVisitor.addNote(note);
		if(rows == 1){
			flag = true;
		}
		return String.valueOf(flag);
	}
	
	private Note generateNote(String userId, String type, String title,
			String content){
		Note note = new Note();
		note.setId(PintuUtils.generateUID());
		note.setType(type);
		note.setTitle(title);
		note.setContent(content);
		note.setPublisher(userId);
		note.setPublishTime(PintuUtils.getFormatNowTime());
		return note;
	}

	@Override
	public String deleteNoteById(String noteId) {
		boolean flag = false;
		int rows = dbVisitor.deleteNoteById(noteId);
		if(rows == 1){
			flag = true;
		}
		return String.valueOf(flag);
	}

	@Override
	public String updateNoteById(String noteId, String type, String title,
			String content) {
		boolean flag = false;
		int rows = dbVisitor.updateNoteById(noteId,type,title,content);
		if(rows == 1){
			flag = true;
		}
		return String.valueOf(flag);
	}

	@Override
	public void addAttentionById(String noteId, int count) {
		// TODO Auto-generated method stub
		cacheVisitor.cacheNoteAttention(noteId,count);
	}

	@Override
	public void addInterestById(String noteId, int count) {
		// TODO Auto-generated method stub
		cacheVisitor.cacheNoteInterest(noteId,count);
	}

	@Override
	public List<Note> getUserNotes(String userId) {
		List<Note> list = dbVisitor.getUserNotes(userId);
		return combinCount(list);
	}

	@Override
	public Note getNoteById(String noteId) {
		Note note = dbVisitor.getNoteById(noteId);
		return note;
	}

	@Override
	public String updateWeiboUser(String userId, String account, String pwd) {
		boolean flag = false;
		String encryptPwd = Encrypt.encrypt(pwd);
		int rows = dbVisitor.updateWeiboUesr(userId,account,encryptPwd);
		if(rows == 1){
			flag = true;
		}
		return String.valueOf(flag);
	}

	
	// TODO, 实现其他接口方法

}
