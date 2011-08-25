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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.pintu.beans.Comment;
import com.pintu.beans.Event;
import com.pintu.beans.GTStatics;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.News;
import com.pintu.beans.Note;
import com.pintu.beans.Story;
import com.pintu.beans.StoryDetails;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
import com.pintu.beans.Vote;
import com.pintu.beans.Wealth;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;
import com.pintu.tools.ImgDataProcessor;
import com.pintu.utils.PintuUtils;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class PintuServiceImplement implements PintuServiceInterface {

	// 由Spring注入
	private DBAccessInterface dbVisitor;
	// 由Spring注入
	private CacheAccessInterface cacheVisitor;

	// 由Spring注入
	private ImgDataProcessor imgProcessor;

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
	public void createTastePic(TastePic pic, String user) {
		System.out.println("3 构造对象 pintuservice createTastePic");
		System.out.println("TastePic:" + pic.getFileType() + "   user:" + user);
		if (pic != null && user != null) {
			// 1. 构造TPicItem对象
			TPicItem tpicItem = new TPicItem();
			String pid = UUID.randomUUID().toString().replace("-", "")
					.substring(16);
			tpicItem.setId(pid);
			tpicItem.setName(pid + "." + pic.getFileType());
			tpicItem.setOwner(user);

			tpicItem.setPublishTime(PintuUtils.getFormatNowTime());
			System.out.println("publishTime:" + tpicItem.getPublishTime());

			tpicItem.setDescription(pic.getDescription());
			tpicItem.setTags(pic.getTags());

			if (pic.getAllowStory() != null) {
				tpicItem.setAllowStory(Integer.parseInt(pic.getAllowStory()));
			} else {
				tpicItem.setAllowStory(1);
			}
			tpicItem.setPass(1);

			// 2. 放入缓存
			cacheVisitor.cachePicture(tpicItem);
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
	}

	@Override
	public void addCommentToPintu(Comment cmt) {
		cacheVisitor.cacheComment(cmt);
	}

	@Override
	public void addVoteToStory(Vote vote) {
		cacheVisitor.cacheVote(vote);
	}

	@Override
	public List<TPicDesc> getTpicsByUser(String user, String pageNum) {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public Boolean loginByWeibo(String user, String pswd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean loginSys(String user, String pswd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean registerUser(String user, String pswd, String inviteCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean addPollToTpic(Vote vote) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean applyForUser(String realname, String email, String intro) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteStoryOfPic(String storyId, String tpicID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteTasetPic(String tpID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean exchangeGifts(String user, String giftIds) {
		// 2.0 功能暂时不实现
		return null;
	}

	@Override
	public List<TPicDesc> getClassicTpics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Event> getCommunityEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GTStatics getCommunityGTs() {
		// 2.0 功能暂时不实现
		return null;
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
	public int getMinutes(String longTime) {
		Long lTime = Long.parseLong(longTime);
		long minlong = lTime / (60 * 1000);
		int min = Math.round(minlong);
		return min;
	}

	@Override
	public String getTpicsByTime(String startTime, String endTime) {
		List<TPicDesc> resultList = new ArrayList<TPicDesc>();
		List<TPicDesc> thumbnailList = new ArrayList<TPicDesc>();
		int start = getMinutes(startTime);
		int end = getMinutes(endTime);

		for (int i = start; i <= end; i++) {
			List<TPicDesc> cacheList = cacheVisitor.getCachedThumbnail(String
					.valueOf(i));
			thumbnailList.addAll(cacheList);
		}

		System.out.println("取出缓存对象：" + thumbnailList.size());

		if (thumbnailList != null) {
			for (int j = thumbnailList.size() - 1; j >= 0; j--) {
				resultList.add(thumbnailList.get(j));
			}
		}

		JSONArray ja = JSONArray.fromCollection(resultList);

		return ja.toString();
	}

	@Override
	public List<TPicDesc> getFavoriteTpics(String user, String pageNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gift> getGiftsToday() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TPicDesc> getHotTpics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<News> getIndustryNews() {
		// 2.0功能暂不实现
		return null;
	}

	@Override
	public List<TPicDesc> getInviteTpicsToday() {
		// 这个功能暂时不在1.0中实现，界面中没有设计
		return null;
	}

	@Override
	public List<TPicDesc> getLatestTpics(String timeLength) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Note> getMarketNotes() {
		// 2.0功能暂不实现
		return null;
	}

	@Override
	public List<Wealth> getShellDetails(String user) {
		// TODO Auto-generated method stub
		return null;
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
		User user = dbVisitor.getUserById(userId);
		int commentNum = this.getCommentsOfPic(tpId).size();
		int storyNum = this.getStoriesOfPic(tpId).size();
		details.setStoriesNum(storyNum);
		details.setCommentsNum(commentNum);
		if (user != null) {
			details.setAuthor(user.getAccount());
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
		details.setTags(item.getTags());
		details.setAllowStory(item.getAllowStory());
		// 取一个图片详情时，我们认为这张图获得了一个点击量
		int counter = 1;

		// 在缓存的热图id里查看是否存在这个id,若存在直接给其点击量累加，否则将新的放到里面
		if (PintuServiceInterface.hotPicCacheIds.containsKey(item.getId())) {
			Integer value = PintuServiceInterface.hotPicCacheIds.get(item
					.getId());
			PintuServiceInterface.hotPicCacheIds.put(item.getId(), value
					+ counter);
		} else {
			PintuServiceInterface.hotPicCacheIds.put(item.getId(), counter);
		}

		details.setCounter(PintuServiceInterface.hotPicCacheIds.get(item
				.getId()));

		return details;
	}

	@Override
	public List<Story> getStoriesOfPic(String tpId) {
		List<Story> storyList = dbVisitor.getStoriesOfPic(tpId);
		return storyList;
	}

	@Override
	public List<Comment> getCommentsOfPic(String tpId) {
		List<Comment> resList = new ArrayList<Comment>();
		List<Comment> cmtList = dbVisitor.getCommentsOfPic(tpId);
		if(cmtList.size()>0){
			for(int i=0;i<cmtList.size();i++){
				Comment comt = new Comment();
				Comment cmt = cmtList.get(i);
				String userId = cmt.getOwner();
				User user = dbVisitor.getUserById(userId);
				comt.setId(cmt.getId());
				comt.setAuthor(user.getAccount());
				comt.setFollow(cmt.getFollow());
				comt.setOwner(cmt.getOwner());
				comt.setContent(cmt.getContent());
				comt.setPublishTime(cmt.getPublishTime());
				resList.add(comt);
			}
		}
		return resList;
	}

	@Override
	public List<Vote> getVotesOfStory(String storyId) {
		List<Vote> voteList = dbVisitor.getVoteOfStory(storyId);
		return voteList;
	}

	@Override
	public User getUserInfo(String userId) {
		User user = dbVisitor.getUserById(userId);
		return user;
	}

	@Override
	public User getUsrBasInfo(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Wealth getUsrEstate(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean giveGifts(String user, String giftIds) {
		// 2.0功能暂不实现
		return null;
	}

	@Override
	public Boolean pasteNote(String user, String content) {
		// 2.0功能暂不实现
		return null;
	}

	@Override
	public Boolean publishAvailableGift(Gift gift) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean publishIndustryEvent(Event tpEvent) {
		// 2.0功能暂不实现
		return null;
	}

	@Override
	public Boolean publishTpEvent(Event tpEvent) {
		// 2.0功能暂不实现
		return null;
	}

	@Override
	public List<TPicDesc> searchTpicByTags(String tags) {
		// 2.0功能暂不实现
		return null;
	}

	@Override
	public List<Message> getUserMessages(String userId) {
		List<Message> msgList = dbVisitor.getUserMessages(userId);
		return msgList;
	}

	@Override
	public Boolean sendMessage(Message msg) {
		int i = dbVisitor.insertMessage(msg);
		if (i > 0) {
			return true;
		} else {
			return false;
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
	public boolean changeMsgState(String msgId) {
		int rows = dbVisitor.updateMsg(msgId);
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<TPicDetails> getHotPicture() {
		List<TPicDetails> hotList = new ArrayList<TPicDetails>();
		Map<String, Integer> map = PintuServiceInterface.hotPicCacheIds;
		// for(String tpId:map.keySet()){
		// 遍历被点击过的图片存储map,判断一下，如果点击次数超过十次则认定为热图
		// if(map.get(tpId) > 10){
		// TPicDetails tpic = this.getTPicDetailsById(tpId);
		// hotList.add(tpic);
		// }

		// 这里假设每天有十个热图
		int[] counterArray = new int[10];
		int i = 0;
		if (i < 10) {
			for (Integer counter : map.values()) {
				if (counter > 10) {
					Array.set(counterArray, i, counter);
					i++;
				}
			}
		}

		if (counterArray != null && counterArray.length > 1) {
			sortArray(counterArray);
		}

		//根据排序后的结果从大到小，取得缓存中的tpId，并查出详情
		for (int j = 0; j < counterArray.length; j++) {
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

			//后移排序码小于R[i]的记录
            for( j = i-1;j >= left;j-- )
            {
                array[j+1] = array[j];
            }
			// 插入
			array[left] = num;
		}


	}

	@Override
	public List<StoryDetails> getClassicalPintu() {
		List<StoryDetails> classicalList = new ArrayList<StoryDetails>();
		List<Story> list = dbVisitor.getClassicalPintu();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Story story = list.get(i);
				String userId = story.getOwner();
				User user = this.getUserInfo(userId);
				StoryDetails sDetails = new StoryDetails();
				sDetails.setAuthor(user.getAccount());
				sDetails.setId(story.getId());
				sDetails.setOwner(userId);
				sDetails.setPublishTime(story.getPublishTime());
				sDetails.setContent(story.getContent());
				sDetails.setClassical(story.getClassical());
				sDetails.setFollow(story.getFollow());
				classicalList.add(sDetails);
			}
		}
		return classicalList;
	}

	// TODO, 实现其他接口方法

}
