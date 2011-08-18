/**
 * 
 */
package com.pintu.facade;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDetails;
import com.pintu.beans.TastePic;
import com.pintu.beans.Vote;
import com.pintu.utils.PintuUtils;

/**
 * Servlet调用服务的参数转换器，用来封装客户端参数并实现服务调用；
 * @author lwz
 *
 */


public class ApiAdaptor {

	//由Spring注入
	private PintuServiceInterface pintuService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
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
					pic.setDescription(item.getString());
//					pic.setDescription(UTF8Formater.changeToWord(item.getString()));
					System.out.println("description:"+pic.getDescription());
				}
				if(item.getFieldName().equals("tags")){
					pic.setTags(item.getString());
//					pic.setTags(UTF8Formater.changeToWord(item.getString()));
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
		return JSONArray.fromCollection(pintuService.getStoriesOfPic(tpID)).toString();
	}
	
	public void createStory(String follow,String owner,String content,String classical){
		Story story = new Story();
		story.setId(PintuUtils.generateUID());
		story.setFollow(follow);
		story.setOwner(owner);
		story.setPublishTime(PintuUtils.getFormatNowTime());
		story.setContent(content);
		story.setClassical(Integer.parseInt(classical));
		this.addStoryToPicture(story);
	}


	/**
	 * 为一个品图添加故事
	 * @param story
	 */
	public void addStoryToPicture(Story story){
		 pintuService.addStoryToPintu(story);
	}
	
	
	public void createComment(String follow,String owner,String content ){
		Comment cmt = new Comment();
		cmt.setId(PintuUtils.generateUID());
		cmt.setFollow(follow);
		cmt.setOwner(owner);
		cmt.setPublishTime(PintuUtils.getFormatNowTime());
		cmt.setContent(content);
		this.addCommentToPicture(cmt);
	}
	
	/**
	 * 为一个品图添加评论
	 * @param cmt
	 * @return
	 */
	public void addCommentToPicture(Comment cmt){
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
	public void addVoteToStory(Vote vote) {
		pintuService.addVoteToStory(vote);
	}

} //end of class
