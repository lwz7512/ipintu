package com.pintu.facade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

public class PintuServiceImplement implements PintuServiceInterface{

	//由Spring注入
	private DBAccessInterface dbVisitor;
	//由Spring注入
	private CacheAccessInterface cacheVisitor;
	
	//由Spring注入
	private ImgDataProcessor imgProcessor;
	
	
	//Constructor
	public PintuServiceImplement(){
		//DO NOTHING CURRENTLY
	}		

	
	public void setImgProcessor(ImgDataProcessor imgProcessor) {
		this.imgProcessor = imgProcessor;
	}


	//TODO, Spring injection
	public void setDbVisitor(DBAccessInterface dbVisitor) {
		this.dbVisitor = dbVisitor;
	}
	
	//TODO, Spring injection
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
	public Boolean createTastePic(TastePic pic, String user) {
		System.out.println("3 构造对象 pintuservice createTastePic");
		System.out.println("TastePic:"+pic.getFileType()+"   user:"+user);
		if(pic !=null && user!=null){
			// 1. 构造TPicItem对象
			TPicItem  tpicItem = new TPicItem();
			String pid = UUID.randomUUID().toString().replace("-", "").substring(16);
			tpicItem.setId(pid);
			tpicItem.setName(pid+"."+pic.getFileType());
			tpicItem.setOwner(user);
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tpicItem.setPublishTime(sdf.format(new Date()));
			System.out.println("publishTime:"+tpicItem.getPublishTime());
			
			tpicItem.setDescription(pic.getDescription());
			tpicItem.setTags(pic.getTags());
			
			if(pic.getAllowStory() != null){
				tpicItem.setAllowStory(Integer.parseInt(pic.getAllowStory()));
			}else{
				tpicItem.setAllowStory(1);
			}
			tpicItem.setPass(1);
			
			//2. 放入缓存
			cacheVisitor.cachePicture(tpicItem);
			
			//3. 提交imgProcessor生成文件
			imgProcessor.createImageFile(pic.getRawImageData(), tpicItem);
			
			//4. 入库的事情就交由同步工具CacheToDB来处理，这里就结束了！
			
		}else{
		     //TODO
		}
		
		return null;
	}

	private Date TO_DATE(String format, String string) {
		// TODO Auto-generated method stub
		return null;
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
	public Boolean addStoryToTpic(Story story) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean applyForUser(String realname, String email, String intro) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean commentPintu(Comment cmt) {
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
	public TPicDetails getTPicDetailsByID(String tpID) {
		// TODO Auto-generated method stub
		return null;
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
	public List<Comment> getCommentsOfPic(String tpID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Story> getStoriesOfPic(String tpID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Byte[] getTPicBig(String tpID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Byte[] getTPicThumbnail(String thumbnailId) {
		// TODO Auto-generated method stub
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
	public Byte[] getTPicMoile(String tpID) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void saveImagePathToProcessor(String filePath) {
		this.imgProcessor.setImagePath(filePath);
	}	
	
	
	//TODO, 实现其他接口方法
	
}
