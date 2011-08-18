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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
	

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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

			tpicItem.setPublishTime(sdf.format(new Date()));
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
			List<TPicDesc> cacheList = cacheVisitor.getCachedThumbnail(String.valueOf(i));
			 thumbnailList.addAll(cacheList);
		}

		System.out.println("取出缓存对象：" + thumbnailList.size());

		if(thumbnailList != null){
			for(int j=thumbnailList.size()-1;j>=0;j--){
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
	public TPicDetails getTPicDetailsById(String tpID) {
		TPicDetails details = new TPicDetails();
		//根据图片id到缓存中取图片的基本信息
		TPicItem item = cacheVisitor.getSpecificPic(tpID);
		if(item != null){
			String uerId = item.getOwner();
			//得到图片的所有者即userId,再到数据库里取出user的详细信息
			User user = dbVisitor.getUserById(uerId);
			int commentNum = this.getCommentsOfPic(tpID).size();
			int storyNum = this.getStoriesOfPic(tpID).size();
			details.setStoriesNum(String.valueOf(storyNum));
			details.setCommentsNum(String.valueOf(commentNum));
			if(user != null){
				details.setUserName(user.getAccount());
				details.setScore(user.getScore());
				details.setLevel(user.getLevel());
				details.setAvatarImgPath(user.getAvatar());
				details.setId(item.getId());
				details.setName(item.getName());
				details.setOwner(uerId);
				details.setMobImgId(item.getMobImgId());
				details.setRawImgId(item.getRawImgId());
				details.setPublishTime(item.getPublishTime());
				details.setDescription(item.getDescription());
				details.setTags(item.getTags());
				details.setAllowStory(item.getAllowStory());
			}
		
		}
		return details;
	}
	
	
	@Override
	public List<Story> getStoriesOfPic(String tpID) {
		List<Story> storyList = dbVisitor. getStoriesOfPic(tpID);
//		JSONArray jarray = JSONArray.fromCollection(storyList);
		return storyList;
	}
	

	@Override
	public List<Comment> getCommentsOfPic(String tpID) {
		List<Comment>  cmtList = dbVisitor.getCommentsOfPic(tpID);
//		JSONArray jarray = JSONArray.fromCollection(cmtList);
		return cmtList;
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
	public List<Message> getUserMessages(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean sendMessage(Message msg) {
		// TODO Auto-generated method stub
		return null;
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
			writeJPGImage(jpgFile,res);
		} else if (pngFile.exists()) {
			writePNGImage(pngFile,res);
		} else if (gifFile.exists()) {
			writeGIFImage(gifFile,res);
		} else{
			writeJPGImage(file,res);
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
		getOutInfo(file,  res);
	}
	

	private void writeGIFImage(File file, HttpServletResponse res) {
		res.setContentType(GIF);
		getOutInfo(file,  res);
	}
	
	private void getOutInfo(File file, HttpServletResponse res) {
		try {
			OutputStream out = res.getOutputStream();
			InputStream imageIn = new FileInputStream(file);
			BufferedInputStream bis=new BufferedInputStream(imageIn);
	        //输入缓冲流   
	        BufferedOutputStream bos=new BufferedOutputStream(out);
	        //输出缓冲流   
	        byte data[]=new byte[4096];
	        //缓冲字节数   
	        int size=0;    
	        size=bis.read(data);   
	        while (size!=-1){      
	            bos.write(data,0,size);           
	            size=bis.read(data);   
	        }   
	        bis.close();   
	        bos.flush();
	        //清空输出缓冲流        
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
		File defaultFile = new File( imagePath + File.separator +"avatarImg"+File.separator+"defaultAvatar.jpg");
		String type =  path.substring(path.lastIndexOf(".") + 1);
		File file = new File(path);
		if(file.exists()){
			if(type.toLowerCase().equals("jpg")){
				writeJPGImage(file,res);
			}else if(type.toLowerCase().equals("png")){
				writePNGImage(file,res);
			}else if(type.toLowerCase().equals("gif")){
				writeGIFImage(file,res);
			}else{
				writeJPGImage(defaultFile,res);
			}
		}
	
		
	}


	// TODO, 实现其他接口方法

}
