package com.pintu.init;

import com.pintu.jobs.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Application Lifecycle Listener implementation class AppStarter
 *
 */
public class AppStarter implements ServletContextListener {

	private TaskTimer generalTimer;
	
	private TaskTimer fixRunTimer;
	
	//������㶼��ÿ��1Сʱ����һ��
	private int calculateInterval = 60;
	
    public AppStarter() {
        // DO NOTHING...
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	
    	String status = "AppStarer listener running...";
    	arg0.getServletContext().log(status);
    	System.out.println(status);
    	
    	generalTimer = new TaskTimer();
    	generalTimer.setMin(calculateInterval);
    	//����ǰ���ƶ���������
    	generalTimer.setTask(new CalculateTask());
    	generalTimer.start();
    	
    	
    	fixRunTimer = new TaskTimer();
    	fixRunTimer.setTask(new CalculateTask());
    	//ÿ��0������
    	fixRunTimer.runAtFixTime("00:00:00");

    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    	
    	String status = "AppStarer listener stopped!";
    	arg0.getServletContext().log(status);
    	System.out.println(status);
    	
    	generalTimer.stop();
    	fixRunTimer.stop();
    }
	
}
