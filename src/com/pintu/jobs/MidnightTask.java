package com.pintu.jobs;
/**
 * 用于每天零点来清除系统中的某些数据
 */
import java.util.TimerTask;

import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;

public class MidnightTask extends TimerTask{

	private DBAccessInterface dbAccess;
	private CacheAccessInterface cacheAccess;
	


	public MidnightTask(DBAccessInterface dbVisitor,
			CacheAccessInterface cacheVisitor) {
		this.dbAccess=dbVisitor;
		this.cacheAccess = cacheVisitor;
	}

	@Override
	public void run() {
		clearCounter();
	}
	
	private void clearCounter(){
		System.out.println(">>> midnight task executed...");
		//将一天内查看过详情的图片点击量清零
		this.cacheAccess.clearHotPicCacheIds();
	}
	

}
