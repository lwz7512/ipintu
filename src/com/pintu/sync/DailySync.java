package com.pintu.sync;
/**
 * 在每次系统启动时，将当天从0点开始的内容同步的缓存中
 * @author liumingli
 *
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicItem;
import com.pintu.beans.Vote;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;
import com.pintu.facade.PintuServiceInterface;
import com.pintu.utils.PintuUtils;

public class DailySync implements Runnable{
	    //由Spring注入
		private DBAccessInterface dbVisitor;
		//由Spring注入
		private CacheAccessInterface cacheVisitor;
		//由Spring注入
		private PintuServiceInterface pintuService;
		
		private Logger log = Logger.getLogger(DailySync.class);
		
		// 同步开关
		private Boolean dailyFlag = true;
		
		public void run() {
			if(dailyFlag){
				Calendar c = Calendar.getInstance();
			    int   year=c.get(Calendar.YEAR); 
			    int   month=c.get(Calendar.MONTH); 
			    int   day=c.get(Calendar.DATE); 
				c.set(year, month, day, 0, 0, 0);
				Date date = c.getTime();
				//得到当天零点即 "2011-08-12 00:00:00"
				String today = PintuUtils.formatDate(date);
				
				//同步当日零点开始数据库图片到缓存
				List<String> picNames=syncPictureTask(today);
				
				//同步品图对应的缓存图片
				syncThumbnailTask(picNames);
				
				//同步当日零点开始数据库评论到缓存
				syncCommentTask(today);
				
				//同步当日零点开始数据库故事到缓存
				String storyIds = syncStoryTask(today);
				
				//同步当日零点开始数据库投票到缓存
				//若没有同步到故事的缓存，即storyId没有，就不用同步投票了吧
				if(storyIds.length()>0){
					syncVoteTask(storyIds);
				}
			}
		}

		/**
		 * 这里，因为系统设计图片名即为id.扩展名的格式，所以直接用name取缩略图
		 * @param picNames
		 */
		private void syncThumbnailTask(List<String> picNames) {
			log.info("将文件路径下保存的缩略图同步到缓存开始...");
			if(picNames != null && picNames.size() > 0){
				for(int i=0;i<picNames.size();i++){
					String picId = picNames.get(i).substring(0,picNames.get(i).lastIndexOf("."));
					String suffix = picNames.get(i).substring(picNames.get(i).lastIndexOf("."));
					String thumbnailName = picId+"_Thumbnail"+suffix;
					File file = pintuService.getThumbnail(thumbnailName);
					if(file.exists()){
						Long creationTime = file.lastModified();
						TPicDesc thumbnail = new TPicDesc();
						thumbnail.setThumbnailId( picId+"_Thumbnail");
						thumbnail.setTpId(picId);
						thumbnail.setCreationTime(String.valueOf(creationTime));
						thumbnail.setStatus("0");
						cacheVisitor.cacheThumbnail(thumbnail);
					} else{
						log.info("与原品图:"+picNames.get(i)+"对应的缩略图不存在！");
					}
				}
			}
			log.info("同步缩略图到缓存结束。。。");
		}


		private void syncVoteTask(String storyIds) {
				log.info("同步数据库投票到缓存开始...");
				List<Vote> voteList=dbVisitor.getVoteForCache(storyIds);
				if(voteList != null){
					for(int i=0;i<voteList.size();i++){
						Vote vote = voteList.get(i);
						cacheVisitor.syncDBVoteToCache(vote);
					}
				}
				log.info("同步投票到缓存结束，voteSize:"+voteList.size());
		}


		private String syncStoryTask(String today) {
			log.info("同步数据库故事到缓存开始...");
			// 这里因是要取出story的id给vote用，构造出一个类似('storyId','storyId')的串给sql语句用
			StringBuffer storyIds= new StringBuffer();
			List<Story> storyList=dbVisitor.getStoryForCache(today);
			if(storyList != null){
				for(int i=0;i<storyList.size();i++){
					Story story = storyList.get(i);
					if(storyIds.length()>0){
						storyIds.append(",");
					}
					storyIds.append("'");
					storyIds.append(story.getId());
					storyIds.append("'");
					
					cacheVisitor.syncDBStoryToCache(story);
				}
			}
			log.info("同步故事到缓存结束，storySize:"+storyList.size()+"  storyIds:"+storyIds.toString());
			return storyIds.toString();
		}


		private void syncCommentTask(String today) {
			log.info("同步数据库评论到缓存开始...");
			List<Comment> commList=dbVisitor.getCommentForCache(today);
			if(commList != null){
				for(int i=0;i<commList.size();i++){
					Comment comm = commList.get(i);
					cacheVisitor.syncDBCommnetToCache(comm);
				}
			}
			log.info("同步评论到缓存结束，commentSize:"+commList.size());
		}


		private List<String> syncPictureTask(String today){
			log.info("同步数据库图片到缓存开始...");
			List<String> names = new ArrayList<String>();
			List<TPicItem> picList=dbVisitor.getPictureForCache(today);
			if(picList != null){
				for(int i=0;i<picList.size();i++){
					TPicItem pic = picList.get(i);
					cacheVisitor.syncDBPictureToCache(pic);
					names.add(pic.getName());
				}
			}
			log.info("同步图片到缓存结束，pictureSize:"+picList.size());
			return names;
		}
		
		public void setDbVisitor(DBAccessInterface dbVisitor) {
			this.dbVisitor = dbVisitor;
		}

		public void setCacheVisitor(CacheAccessInterface cacheVisitor) {
			this.cacheVisitor = cacheVisitor;
		}

		public void setPintuService(PintuServiceInterface pintuService) {
			this.pintuService = pintuService;
		}

		public Boolean getDailyFlag() {
			return dailyFlag;
		}

		public void setDailyFlag(Boolean dailyFlag) {
			this.dailyFlag = dailyFlag;
		}

}
