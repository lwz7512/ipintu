package com.pintu.sync;
/**
 * 在每次系统启动时，将当天从0点开始的内容同步的缓存中
 * @author liumingli
 *
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.pintu.beans.Comment;
import com.pintu.beans.Story;
import com.pintu.beans.TPicItem;
import com.pintu.beans.Vote;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class DailySync implements Runnable{
	    //由Spring注入
		private DBAccessInterface dbVisitor;
		//由Spring注入
		private CacheAccessInterface cacheVisitor;

		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		public void run() {
			
				
				Calendar c = Calendar.getInstance();
			    int   year=c.get(Calendar.YEAR); 
			    int   month=c.get(Calendar.MONTH); 
			    int   day=c.get(Calendar.DATE); 
				c.set(year, month, day, 0, 0, 0);
				Date date = c.getTime();
				//得到当天零点即 "2011-08-12 00:00:00"
				String today = sdf.format(date);
				
				//同步当日零点开始数据库图片到缓存
				syncPictureTask(today);
				
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

		
		private void syncVoteTask(String storyIds) {
			
				List<Vote> voteList=dbVisitor.getVoteForCache(storyIds);
				if(voteList != null){
					for(int i=0;i<voteList.size();i++){
						Vote vote = voteList.get(i);
						cacheVisitor.syncDBVoteToCache(vote);
					}
				}
		}


		private String syncStoryTask(String today) {
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
			return storyIds.toString();
		}


		private void syncCommentTask(String today) {
			List<Comment> commList=dbVisitor.getCommentForCache(today);
			if(commList != null){
				for(int i=0;i<commList.size();i++){
					Comment comm = commList.get(i);
					cacheVisitor.syncDBCommnetToCache(comm);
				}
			}
			
		}


		private void syncPictureTask(String today){
			List<TPicItem> picList=dbVisitor.getPictureForCache(today);
			if(picList != null){
				for(int i=0;i<picList.size();i++){
					TPicItem pic = picList.get(i);
					cacheVisitor.syncDBPictureToCache(pic);
				}
			}
		}
		
		public void setDbVisitor(DBAccessInterface dbVisitor) {
			this.dbVisitor = dbVisitor;
		}
	
		public void setCacheVisitor(CacheAccessInterface cacheVisitor) {
			this.cacheVisitor = cacheVisitor;
		}

}
