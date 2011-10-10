package com.pintu.jobs;

/**
 * 用于每天零点来清除系统中的某些数据
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.pintu.beans.Story;
import com.pintu.beans.Vote;
import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class MidnightTask extends TimerTask {

	private DBAccessInterface dbAccess;
	private CacheAccessInterface cacheAccess;
	private Properties propertyConfigurer;
	
	// 新更新classical的故事id（'','',''）
	public static StringBuffer newClassicalStoryIds = new StringBuffer();

	private Logger log = Logger.getLogger(MidnightTask.class);

	public MidnightTask(DBAccessInterface dbVisitor,
			CacheAccessInterface cacheVisitor, Properties propertyConfigurer) {
		this.dbAccess = dbVisitor;
		this.cacheAccess = cacheVisitor;
		this.propertyConfigurer = propertyConfigurer;
	}

	@Override
	public void run() {
		System.out.println(">>> midnight task executed...");

		clearCounter();
		findAndSetClassical();
	}

	private void clearCounter() {
		// 将一天内查看过详情的图片点击量清零
		this.cacheAccess.clearHotPicCacheIds();
	}

	private void findAndSetClassical() {

		List<Story> classicalList = this.dbAccess.getClassicalPintu();
		// 根据故事id取得投票信息
		List<Vote> voteList = this.dbAccess.getAllVote();
		List<String> needUpdateStoryIds = new ArrayList<String>();
		if (voteList.size() > 0) {
			for (int i = 0; i < voteList.size(); i++) {
				Vote vote = voteList.get(i);
				// 查看并判断经典投票的数量
				if (vote.getType().equals(Vote.STAR_TYPE)
						&& vote.getAmount() > Integer
								.parseInt(propertyConfigurer
										.getProperty("classicalVoteNum"))) {

					needUpdateStoryIds.add(vote.getFollow());
				}
			}
		}

		//对比所有经典和库中已置为经典的故事，留下新的需要更新的经典
		for(int j=0;j<classicalList.size();j++){
			Story classicalStory = classicalList.get(j);
			if(needUpdateStoryIds.contains(classicalStory.getId())){
				needUpdateStoryIds.remove(classicalStory.getId());
			}
		}

		if (needUpdateStoryIds.size() > 0) {
			//为新的经典故事id存储为（'','',''）的形式
			for(int i=0;i<needUpdateStoryIds.size();i++){
				
				if(newClassicalStoryIds.length()>0){
					newClassicalStoryIds.append(",");
				}
				
				newClassicalStoryIds.append("'");
				newClassicalStoryIds.append(needUpdateStoryIds.get(i));
				newClassicalStoryIds.append("'");
			}
			
			// 更新数据库中的经典字段
			int res = this.dbAccess
					.updateStoryClassical(needUpdateStoryIds);
			if (res == needUpdateStoryIds.size()) {
				log.info("Update story classical success!");
			} else {
				log.info("Update story classical failed!");
			}
		}
		
	}

	
}
