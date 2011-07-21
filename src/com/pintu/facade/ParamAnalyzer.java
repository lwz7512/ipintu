package com.pintu.facade;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.pintu.jobs.TaskStarter;
import com.pintu.sync.CacheToDB;

/**
 * 真正处理客户端传参到服务端的逻辑，与适配器打交道，适配器再与服务打交道；
 * @author lwz
 *
 */
public class ParamAnalyzer extends GenericServlet implements Servlet,ExtVisitorInterface {

	private Logger log = Logger.getLogger("** client param analyze: ");
	
	private static final long serialVersionUID = 1L;
	
	//由Spring注入
	private ServiceAdaptor apiAdaptor;
	
	//启动自动任务，由Spring注入
	private TaskStarter taskStarter;
	
	private CacheToDB synchProcess;
	
	public ParamAnalyzer() {
		// TODO Auto-generated constructor stub
	}	
	

	public void setApiAdaptor(ServiceAdaptor apiAdaptor) {
		this.apiAdaptor = apiAdaptor;
	}
		
	public void setTaskStarter(TaskStarter taskStarter) {
		this.taskStarter = taskStarter;
	}

	public void setSynchProcess(CacheToDB synchProcess) {
		this.synchProcess = synchProcess;
	}


	//这个方法被WebEntrance的初始化方法调用
	public void init(ServletConfig config){
		try {
			super.init(config);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//启动任务定时器
		if(taskStarter!=null) taskStarter.runAutoTasks();
		
		//启动数据库同步任务
		if(synchProcess!=null) synchProcess.start();
		
	}

	
	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		
		//TODO, 这里将客户端参数解析出来传给apiAdaptor
		//由apiAdaptor组装参数给服务
		String action = request.getParameter("method");
		
		if(action==null){
			log.warn(">>> 客户没有传递 method 参数，不做任何操作，返回！");
			return;
		}
		
		if(action.equals(this.APPLYFORUSER)){
			//TODO, ...
		}else if(action.equals(this.OTHERMETHOD)){
			//TODO, ...
		}
		
		//TODO, MORE...		

	}
	
	public void destroy(){
		if(taskStarter!=null) taskStarter.stopTask();
	}

}
