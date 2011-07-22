package com.pintu.jobs;

import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;



/**
 * Application Lifecycle Listener implementation class AppStarter
 * 用来启动应用后，开启自动计算任务
 */
public class TaskStarter  {

	private TaskTimer generalTimer;
	
	private TaskTimer fixRunTimer;
	
	//常规计算都是每隔1小时计算一次
	private int calculateInterval = 60;
	
	//由Spring注入
	private DBAccessInterface dbVisitor;
	//由Spring注入
	private CacheAccessInterface cacheVisitor;

	
    public TaskStarter() {
        // DO NOTHING...    	
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

    
	
    public void runAutoTasks() {
    	
    	String status = "AppStarer listener running...";
    	System.out.println(status);
    	
    	generalTimer = new TaskTimer();
    	generalTimer.setMin(calculateInterval);
    	//计算前先制定计算任务，可以是算积分和等级
    	generalTimer.setTask(new ScoreLevelTask());
    	generalTimer.start();
    	
    	//固定任务用来算礼物
    	fixRunTimer = new TaskTimer();
    	fixRunTimer.setTask(new UserEstateTask());
    	//每天0点运行
    	fixRunTimer.runAtFixTime("00:00:00");

    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void stopTask() {
    	
    	String status = "AppStarer listener stopped!";
    	System.out.println(status);
    	
    	generalTimer.stop();
    	fixRunTimer.stop();
    }
	
}
