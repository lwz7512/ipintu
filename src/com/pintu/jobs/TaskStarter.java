package com.pintu.jobs;

import com.pintu.dao.CacheAccessInterface;
import com.pintu.dao.DBAccessInterface;
import com.pintu.facade.PintuServiceInterface;



/**
 * Application Lifecycle Listener implementation class AppStarter
 * ��������Ӧ�ú󣬿����Զ���������
 */
public class TaskStarter  {

	private TaskTimer generalTimer;
	
	private TaskTimer fixRunTimer;
	
	//������㶼��ÿ��1Сʱ����һ��
	private int calculateInterval = 60;
	
	//��Springע��
	private DBAccessInterface dbVisitor;
	//��Springע��
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
    	//����ǰ���ƶ��������񣬿���������ֺ͵ȼ�
    	generalTimer.setTask(new ScoreLevelTask());
    	generalTimer.start();
    	
    	//�̶���������������
    	fixRunTimer = new TaskTimer();
    	fixRunTimer.setTask(new UserEstateTask());
    	//ÿ��0������
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
