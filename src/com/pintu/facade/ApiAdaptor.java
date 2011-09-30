/**
 * 
 */
package com.pintu.facade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.Comment;
import com.pintu.beans.Event;
import com.pintu.beans.Favorite;
import com.pintu.beans.Gift;
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.StoryDetails;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TPicItem;
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
				if(item.getFieldName().equals("description")){
				    pic.setDescription(UTF8Formater.changeToWord(item.getString()));
					System.out.println("description:"+pic.getDescription());
				}
				if(item.getFieldName().equals("tags")){
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
		System.out.println(">>> query time span: "+queryTimeSpan/(60*1000)+" minutes;");
		
		long oneDayMiliSeconds = 24*60*60*1000;
		if(queryTimeSpan>oneDayMiliSeconds){
			//如果跨越了1天，就只给返回一天的数据
			startTime = String.valueOf(Long.valueOf(endTime)-oneDayMiliSeconds);
		}
		List<TPicDesc> list= pintuService.getTpicsByTime(startTime, endTime);
		
		return JSONArray.fromCollection(list).toString();
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
		JSONObject json =JSONObject.fromBean(tpicDetails);
		json.remove("mobImgPath");
		json.remove("rawImgPath");
		return json.toString() ;
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

	
	private Vote createVote(String follow,String type,String amount,String voter, String receiver){
		Vote vote = new Vote();
		vote.setId(PintuUtils.generateUID());
		vote.setFollow(follow);
		vote.setType(type);
		vote.setAmount(Integer.parseInt(amount));
		vote.setVoter(voter);
		vote.setReceiver(receiver);
		return vote;
	}
	
	/**
	 * 为品图故事投票
	 * @param voter 
	 * @param receiver 
	 * @param vote
	 */
	public void addVoteToStory(String follow,String type,String amount, String voter, String receiver) {
		Vote vote = this.createVote(follow, type, amount,voter,receiver);
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
	public boolean changeMsgState(String msgIds) {
		List<String> msgIdList = new ArrayList<String>();
		String[] idArray = msgIds.split(",");
		for(int i = 0;i < idArray.length;i++){
			msgIdList.add(idArray[i]);
		}
		return pintuService.changeMsgState(msgIdList);
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
		return JSONObject.fromBean(userDetail).toString();
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
		boolean flag = pintuService.checkExistFavorite(userId,picId);
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

	private void removeJsonKey(JSONArray jsonArray){
		for(int i = 0;i<jsonArray.length();i++){
			jsonArray.getJSONObject(i).remove("mobImgPath");
			jsonArray.getJSONObject(i).remove("rawImgPath");
		}
	}
	
	/**
	 * 得到热图列表
	 * @return
	 */
	public String getHotPicture() {
		List<TPicDetails> hotList = pintuService.getHotPicture();
		JSONArray jsonArray = JSONArray.fromCollection(hotList);
		removeJsonKey(jsonArray);
		return jsonArray.toString();
	}
	
	public String getFavorTpics(String userId, int pageNum) {
		List<TPicItem> favorList = pintuService.getFavoriteTpics(userId,pageNum);
		JSONArray jsonArray = JSONArray.fromCollection(favorList);
		removeJsonKey(jsonArray);
		return jsonArray.toString();
	}
	
	public String getTpicsByUser(String userId, int pageNum) {
		List<TPicItem> userPicList = pintuService.getTpicsByUser(userId,pageNum);
		JSONArray jsonArray = JSONArray.fromCollection(userPicList);
		removeJsonKey(jsonArray);
		return jsonArray.toString();
	}

	public String getStoryiesByUser(String userId, int pageNum) {
		List<StoryDetails> userStoryList = pintuService.getStroiesByUser(userId,pageNum);
		return JSONArray.fromCollection(userStoryList).toString();
	}

	public String getExchangeableGifts() {
		List<Gift> list = pintuService.getExchangeableGifts();
		return JSONArray.fromCollection(list).toString();
	}

	public String getCommunityEvents() {
		List<Event> list =pintuService.getCommunityEvents();
		return JSONArray.fromCollection(list).toString();
	}

	public boolean publishCommunityEvent(String title, String detail,
			String time) {
		Event eve = new Event();
		eve.setId(PintuUtils.generateUID());
		eve.setTitle(title);
		eve.setDetail(detail);
		eve.setEventTime(time);
		return  pintuService.publishCommunityEvent(eve);
	}

	public boolean publishExchangeableGift() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getLatestPic() {
		List<TPicDesc> list= pintuService.getLatestPic();
		return JSONArray.fromCollection(list).toString();
	}

	public String getExistUser(String account, String pwd) {
		String result = pintuService.getExistUser(account,pwd);
		return result;
	}

	public String registerUser(String account, String pwd, String code) {
		String prompt = pintuService.registerUser(account,pwd,code);
		return prompt;
	}
	
	public int validateAccount(String account) {
		int result = pintuService.validateAccount(account);
		return result;
	}

	public String sendApply(String account, String reason) {
		String prompt = pintuService.sendApply(account,reason);
		return prompt;
	}

	public String acceptApply(String id, String account,String url, String opt) {
		String prompt = pintuService.acceptApply(id,account,url,opt);
		return prompt;
	}

	public List<User> getApplicant() {
		List<User> list = pintuService.getApplicant();
		return list;
	}

	public boolean examineUser(String userId) {
		boolean flag = pintuService.examineUser(userId);
		return flag;
	}



	
} //end of class
