package com.pintu.facade;

import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.GTStatics;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.News;
import com.pintu.beans.Note;
import com.pintu.beans.ShellDetails;
import com.pintu.beans.Story;
import com.pintu.beans.TPEvent;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TastePic;
import com.pintu.beans.UsrEstate;
import com.pintu.beans.Vote;
import com.pintu.beans.WeiboUsr;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class PintuServiceImplement implements PintuServiceInterface{

	//Inject by Spring
	private DBAccessInterface dbVisitor;
	//Inject by Spring
	private CacheAccessInterface cacheVisitor;
	
	
	//Constructor
	public PintuServiceImplement(){
		//DO NOTHING CURRENTLY
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
		// 2.0 ������ʱ��ʵ��
		return null;
	}

	@Override
	public List<TPicDesc> getClassicTpics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TPEvent> getCommunityEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GTStatics getCommunityGTs() {
		// 2.0 ������ʱ��ʵ��
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
		// 2.0�����ݲ�ʵ��
		return null;
	}

	@Override
	public List<TPicDesc> getInviteTpicsToday() {
		// ���������ʱ����1.0��ʵ�֣�������û�����
		return null;
	}

	@Override
	public List<TPicDesc> getLatestTpics(String timeLength) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Note> getMarketNotes() {
		// 2.0�����ݲ�ʵ��
		return null;
	}

	@Override
	public ShellDetails getShellDetails(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TPicDetails getTPicDetailsByID(String tpID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WeiboUsr getUsrBasInfo(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UsrEstate getUsrEstate(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean giveGifts(String user, String giftIds) {
		// 2.0�����ݲ�ʵ��
		return null;
	}

	@Override
	public Boolean pasteNote(String user, String content) {
		// 2.0�����ݲ�ʵ��
		return null;
	}

	@Override
	public Boolean publishAvailableGift(Gift gift) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean publishIndustryEvent(TPEvent tpEvent) {
		// 2.0�����ݲ�ʵ��
		return null;
	}

	@Override
	public Boolean publishTpEvent(TPEvent tpEvent) {
		// 2.0�����ݲ�ʵ��
		return null;
	}

	@Override
	public List<TPicDesc> searchTpicByTags(String tags) {
		// 2.0�����ݲ�ʵ��
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
	
	
	
	//TODO, ʵ�������ӿڷ���
	
}
