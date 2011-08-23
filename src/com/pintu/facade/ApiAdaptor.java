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
import com.pintu.beans.Message;
import com.pintu.beans.Story;
import com.pintu.beans.StoryDetails;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TastePic;
import com.pintu.beans.User;
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
		// TODO Auto-generated constructor stub
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
	public String getStoriesOfPic(String tpID){
		List<StoryDetails> storyDeatilList = new ArrayList<StoryDetails>();
		List<Story> storyList = pintuService.getStoriesOfPic(tpID);
		if(storyList != null && storyList.size() > 0){
			for(int i=0;i<storyList.size();i++){
				StoryDetails storyDetail = new StoryDetails();
				String storyId = storyList.get(i).getId();
				String userId = storyList.get(i).getOwner();
				storyDetail.setId(storyId);
				storyDetail.setFollow( storyList.get(i).getFollow());
				storyDetail.setOwner(userId);
				storyDetail.setPublishTime(storyList.get(i).getPublishTime());
				storyDetail.setContent(storyList.get(i).getContent());
				storyDetail.setClassical(storyList.get(i).getClassical());
				User user = pintuService.getUserInfo(userId);
				if(user != null){
					storyDetail.setAuthor(user.getAccount());
				}
				List<Vote> voteList = pintuService.getVotesOfStory(storyId);
				if(voteList != null && voteList.size()>0){
					for(int j=0;j<voteList.size();j++){
						Vote vote = voteList.get(j);
						if(vote.getType().equals("flower")){
							storyDetail.setFlower(vote.getAmount());
						}else if(vote.getType().equals("egg")){
							storyDetail.setEgg(vote.getAmount());
						}else if(vote.getType().equals("heart")){
							storyDetail.setHeart(vote.getAmount());
						}else if(vote.getType().equals("star")){
							storyDetail.setStar(vote.getAmount());
						}
					}
				}else{
					storyDetail.setFlower(0);
					storyDetail.setEgg(0);
					storyDetail.setHeart(0);
					storyDetail.setStar(0);
				}
				storyDeatilList.add(storyDetail);
			}
		}
		return JSONArray.fromCollection(storyDeatilList).toString();
	}
	
	public void createStory(String follow,String owner,String content){
		Story story = new Story();
		story.setId(PintuUtils.generateUID());
		story.setFollow(follow);
		story.setOwner(owner);
		story.setPublishTime(PintuUtils.getFormatNowTime());
		story.setContent(UTF8Formater.changeToWord(content));
		story.setClassical(0);
		this.addStoryToPicture(story);
	}


	/**
	 * 为一个品图添加故事
	 * @param story
	 */
	private void addStoryToPicture(Story story){
		 pintuService.addStoryToPintu(story);
	}
	
	
	public void createComment(String follow,String owner,String content ){
		Comment cmt = new Comment();
		cmt.setId(PintuUtils.generateUID());
		cmt.setFollow(follow);
		cmt.setOwner(owner);
		cmt.setPublishTime(PintuUtils.getFormatNowTime());
		cmt.setContent(UTF8Formater.changeToWord(content));
		this.addCommentToPicture(cmt);
	}
	
	/**
	 * 为一个品图添加评论
	 * @param cmt
	 * @return
	 */
	private void addCommentToPicture(Comment cmt){
		 pintuService.addCommentToPintu(cmt);
	}

	
	public void createVote(String follow,String type,String amount){
		Vote vote = new Vote();
		vote.setId(PintuUtils.generateUID());
		vote.setFollow(follow);
		vote.setType(type);
		vote.setAmount(Integer.parseInt(amount));
		this.addVoteToStory(vote);
	}
	
	/**
	 * 为品图故事投票
	 * @param vote
	 */
	private void addVoteToStory(Vote vote) {
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

	/**
	 * 发消息
	 * @param sender
	 * @param receiver
	 * @param content
	 */
	public boolean sendMessage(String sender,String receiver,String content){
		Message msg =  new Message();
		msg.setId(PintuUtils.generateUID());
		msg.setSender(sender);
		msg.setReceiver(receiver);
		msg.setContent(UTF8Formater.changeToWord(content));
		msg.setWriteTime(PintuUtils.getFormatNowTime());
		msg.setRead(0);
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
	
} //end of class
