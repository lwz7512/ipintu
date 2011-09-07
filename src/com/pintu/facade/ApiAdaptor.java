/**
 * 
 */
package com.pintu.facade;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.Comment;
import com.pintu.beans.Favorite;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.StoryDetails;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
import com.pintu.beans.UserDetail;
import com.pintu.beans.Vote;
import com.pintu.utils.PintuUtils;
import com.pintu.utils.UTF8Formater;

/**
 * Servlet调用服务的参数转换器，用来封装客户端参数并实现服务调用；
 * @author lwz
 *
 */


public class ApiAdaptor {

	//由Spring注入
	private PintuServiceInterface pintuService;
	
	public ApiAdaptor() {
		
	}

	public PintuServiceInterface getPintuService() {
		return pintuService;
	}

	public void setPintuService(PintuServiceInterface pintuService) {
		this.pintuService = pintuService;
	}
	
	//由AppStarter调用
	public void setImagePath(String filePath) {
		this.pintuService.saveImagePathToProcessor(filePath);
	}
	
	public void createTastePic(List<FileItem> fileItems) {		
		System.out.println("2 分析图片对象apiadaptor createTastePic");
		TastePic pic = new TastePic();
		Iterator<FileItem> iter = fileItems.iterator();
		while(iter.hasNext()){
			FileItem item = iter.next();
			if(item.isFormField()){
				
				if(item.getFieldName().equals("user")){
					pic.setUser(item.getString());
				}
				//FIXME 这里注意，应用手机时需要用到 UTF8Formater
				if(item.getFieldName().equals("description")){
//					pic.setDescription(item.getString());
				    pic.setDescription(UTF8Formater.changeToWord(item.getString()));
					System.out.println("description:"+pic.getDescription());
				}
				if(item.getFieldName().equals("tags")){
//					pic.setTags(item.getString());
					pic.setTags(UTF8Formater.changeToWord(item.getString()));
					System.out.println("tags:"+pic.getTags());
				}
				if(item.getFieldName().equals("allowStory")){
					pic.setAllowStory(item.getString());
				}				
				
			}else{
				//图片数据
				pic.setRawImageData(item);
			}
		} // 参数解析完成
		
		//将新发送的贴图对象放入服务处理		
		this.pintuService.createTastePic(pic, pic.getUser());
		//贴图处理完成OYEAR!		
	}
	
	
	/**
	 * 获取社区长廊的缓存图片信息
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String getGalleryByTime(String startTime,String endTime){
		long queryTimeSpan = Long.valueOf(endTime)-Long.valueOf(startTime);
		System.out.println(">>> query time span: "+queryTimeSpan/60*1000+" minutes;");
		
		long oneDayMiliSeconds = 24*60*60*1000;
		if(queryTimeSpan>oneDayMiliSeconds){
			//如果跨越了1天，就只给返回一天的数据
			startTime = String.valueOf(Long.valueOf(endTime)-oneDayMiliSeconds);
		}
		return pintuService.getTpicsByTime(startTime, endTime);
	}
	
	/**
	 * 获取特定的图片
	 * @param picId
	 * @param res
	 */
	public void getImageFile(String picId, HttpServletResponse res){
		 pintuService.getImageFile(picId,res);
	}
	
	
	/**
	 * 根据图片文件路径返回图片
	 * @param path
	 * @param res
	 */
	public void getImageByPath(String path, HttpServletResponse res){
		pintuService.getImageByPath(path, res);
	}
	/**
	 * 根据图片id获得详情
	 * @param tpId
	 * @return
	 */
	public String getTPicDetailsById(String tpId){
		TPicDetails tpicDetails = pintuService.getTPicDetailsById(tpId);
		return JSONObject.fromBean(tpicDetails).toString() ;
	}
	
	/**
	 * 得到品图评论
	 * @param story
	 * @return
	 */
	
	public String getCommentsOfPic(String tpID){
		return JSONArray.fromCollection(pintuService.getCommentsOfPic(tpID)).toString();
	}
	
	/**
	 * 	得到品图故事
	 * @param tpID
	 * @return
	 */
	public String getStoryDetailsOfPic(String tpId){
		return JSONArray.fromCollection(pintuService.getStroyDetailsOfPic(tpId)).toString();
	}
	
	private Story createStory(String follow,String owner,String content){
		Story story = new Story();
		story.setId(PintuUtils.generateUID());
		story.setFollow(follow);
		story.setOwner(owner);
		story.setPublishTime(PintuUtils.getFormatNowTime());
		story.setContent(UTF8Formater.changeToWord(content));
		story.setClassical(0);
		return story;
	}


	/**
	 * 为一个品图添加故事
	 * @param story
	 */
	public void addStoryToPicture(String follow,String owner,String content){
		Story story = this.createStory(follow, owner, content);
		 pintuService.addStoryToPintu(story);
	}
	
	
	private Comment createComment(String follow,String owner,String content ){
		Comment cmt = new Comment();
		cmt.setId(PintuUtils.generateUID());
		cmt.setFollow(follow);
		cmt.setOwner(owner);
		cmt.setPublishTime(PintuUtils.getFormatNowTime());
		cmt.setContent(UTF8Formater.changeToWord(content));
		return cmt;
	}
	
	/**
	 * 为一个品图添加评论
	 * @param cmt
	 * @return
	 */
	public void addCommentToPicture(String follow,String owner,String content){
		Comment cmt = this.createComment(follow, owner, content);
		 pintuService.addCommentToPintu(cmt);
	}

	
	private Vote createVote(String follow,String type,String amount){
		Vote vote = new Vote();
		vote.setId(PintuUtils.generateUID());
		vote.setFollow(follow);
		vote.setType(type);
		vote.setAmount(Integer.parseInt(amount));
		return vote;
	}
	
	/**
	 * 为品图故事投票
	 * @param vote
	 */
	public void addVoteToStory(String follow,String type,String amount) {
		Vote vote = this.createVote(follow, type, amount);
		pintuService.addVoteToStory(vote);
	}
	
	/**
	 * 获取用户详细信息
	 * @param userId
	 * @return
	 */
	public String getUserDetail(String userId){
		User user = pintuService.getUserInfo(userId);
		return JSONObject.fromObject(user).toString();
	}
	
	private Message createMessage(String sender,String receiver,String content){
		Message msg =  new Message();
		msg.setId(PintuUtils.generateUID());
		msg.setSender(sender);
		msg.setReceiver(receiver);
		msg.setContent(UTF8Formater.changeToWord(content));
		msg.setWriteTime(PintuUtils.getFormatNowTime());
		msg.setRead(0);
		return msg;
	}
	

	/**
	 * 发消息
	 * @param sender
	 * @param receiver
	 * @param content
	 */
	public boolean sendMessage(String sender,String receiver,String content){
		Message msg = this.createMessage(sender, receiver, content);
		return pintuService.sendMessage(msg);
	}
	
	/**
	 * 得到该用户的所有消息信息
	 * @param userId
	 * @return
	 */
	public String getUserMsg(String userId){
		List<Message> msgList = pintuService.getUserMessages(userId);
		return JSONArray.fromCollection(msgList).toString();
	}

	/**
	 * 改变消息状态
	 * @param read
	 */
	public boolean changeMsgState(String msgId) {
		return pintuService.changeMsgState(msgId);
	}

	/**
	 * 得到热图列表
	 * @return
	 */
	public String getHotPicture() {
		List<TPicDetails> hotList = pintuService.getHotPicture();
		return JSONArray.fromCollection(hotList).toString();
	}

	/**
	 * 得到经典品图信息
	 * @return
	 */
	public String getClassicalStory() {
		List<StoryDetails> classicalList = pintuService.getClassicalPintu();
		return JSONArray.fromCollection(classicalList).toString();
	}

	public String getUserEstate(String userId) {
		UserDetail userDetail = pintuService.getUserEstate(userId);
		return JSONArray.fromObject(userDetail).toString();
	}

	
	private Favorite createFavorite(String userId, String picId) {
		Favorite fav = new Favorite();
		fav.setId(PintuUtils.generateUID());
		fav.setCollectTime(PintuUtils.getFormatNowTime());
		fav.setOwner(userId);
		fav.setPicture(picId);
		return fav;
	}
	
	public boolean markFavoritePic(String userId, String picId) {
		boolean flag = pintuService.getOneFavorite(userId,picId);
		if(flag){//图片已收藏，禁止重复收藏
			return false;
		}else{
			Favorite fav = this.createFavorite(userId,picId);
			return pintuService.markFavoritePic(fav);
		}
	}

	public boolean deleteOneFavorite(String fId){
		return pintuService.deleteOnesFavorite(fId);
	}

	
	public String getFavorTpics(String userId, int pageNum) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getTpicsByUser(String userId, int pageNum) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStoryiesByUser(String userId, int pageNum) {
		// TODO Auto-generated method stub
		return null;
	}

	
} //end of class
