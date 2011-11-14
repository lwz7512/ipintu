package com.pintu.sync;
/**
 * 在每次系统启动时,将当天从0点开始的内容同步的缓存中
 * @author liumingli
 *
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

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
		
		private Properties propertyConfigurer;
		
		private Logger log = Logger.getLogger(DailySync.class);
		
		// 同步开关
		private Boolean dailyFlag = true;
		
		public void run() {
			
			if(dailyFlag){

				//当前时间
				Date date = new Date();
				String endTime = PintuUtils.formatDate(date);
				Long before = date.getTime() - 24*60*60*1000* Integer.parseInt(propertyConfigurer
						.getProperty("syncInterval"));
				String startTime= PintuUtils.formatLong(before);
				
				//同步现在到syncInterval（7）天前的数据库图片到缓存
				Map<String,String> picIdNameMap=syncPictureTask(startTime,endTime);
				
				List<String> picNameList = new ArrayList<String>();
				StringBuffer picIds = new StringBuffer();
				
				for(String picId:picIdNameMap.keySet()){
					if(picIds.length()>0){
						picIds.append(",");
					}
					picIds.append("'");
					picIds.append(picId);
					picIds.append("'");
					picNameList.add(picIdNameMap.get(picId));
				}
				
				
				//同步图片对应的缓存图片
				if(picNameList.size() > 0){
					syncThumbnailTask(picNameList);
				}
				
				if(picIds.length() > 0){
					//同步图片对应的数据库故事到缓存
				    syncStoryTask(picIds.toString());
					
					//同步图片对应的数据库投票到缓存
					//若没有同步到图片的缓存,即picId没有,就不用同步投票了吧
					syncVoteTask(picIds.toString());
				}
			}
			
		}

		/**
		 * 这里,因为系统设计图片名即为id.扩展名的格式,所以直接用name取缩略图
		 * @param picNames
		 */
		private void syncThumbnailTask(List<String> picNames) {
			log.info(">>>Will save file path of the thumbnail synchronization to cache is begin...");
			int thumbnialCount = 0;
			if(picNames != null && picNames.size() > 0){
				for(int i=0;i<picNames.size();i++){
					String picId = picNames.get(i).substring(0,picNames.get(i).lastIndexOf("."));
					String suffix = picNames.get(i).substring(picNames.get(i).lastIndexOf("."));
					String thumbnailName = picId+TPicDesc.THUMBNIAL+suffix;
					File file = pintuService.getThumbnail(thumbnailName);
					if(file.exists()){
						Long creationTime = file.lastModified();
						TPicDesc thumbnail = new TPicDesc();
						thumbnail.setThumbnailId( picId+TPicDesc.THUMBNIAL);
						thumbnail.setTpId(picId);
						thumbnail.setCreationTime(String.valueOf(creationTime));
						thumbnail.setStatus("0");
						cacheVisitor.cacheThumbnail(thumbnail);
						thumbnialCount++;
					} else{
						log.info(">>>Thumbnail is missing!");
					}
				}
			}
			log.info(">>>Synchronous thumbnail to cache is over, thumbnialSize:"+thumbnialCount);
		}


		private void syncVoteTask(String picIds) {
				log.info(">>>Synchronous vote to cache is begin...");
				List<Vote> voteList=dbVisitor.getVoteForCache(picIds);
				if(voteList != null){
					for(int i=0;i<voteList.size();i++){
						Vote vote = voteList.get(i);
						cacheVisitor.syncDBVoteToCache(vote);
					}
				}
				log.info(">>>Synchronous vote to cache is over, voteSize:"+voteList.size());
		}


		private String syncStoryTask(String picIds) {
			log.info(">>>Synchronous stroy to cache is begin..");
			// 这里因是要取出story的id给vote用,构造出一个类似('storyId','storyId')的串给sql语句用
			StringBuffer storyIds= new StringBuffer();
//			List<Story> storyList=dbVisitor.getStoryForCache(today);
			List<Story> storyList=dbVisitor.getStoryForCache(picIds);
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
			log.info(">>>Synchronous story to cache is over,storySize:"+storyList.size()+"  storyIds:"+storyIds.toString());
			return storyIds.toString();
		}


		private Map<String,String> syncPictureTask(String startTime,String endTime){
			log.info(">>>Synchronous picture to cache is begin...");
			Map<String,String> resMap = new HashMap<String,String>();
			List<TPicItem> picList=dbVisitor.getPictureForCache(startTime,endTime);
			if(picList != null){
				for(int i=0;i<picList.size();i++){
					TPicItem pic = picList.get(i);
					cacheVisitor.syncDBPictureToCache(pic);
					resMap.put(pic.getId(), pic.getName());
				}
			}
			log.info(">>>Synchronous picture to cache is over,pictureSize:"+picList.size());
			return resMap;
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

		public void setPropertyConfigurer(Properties propertyConfigurer) {
			this.propertyConfigurer = propertyConfigurer;
		}

}
