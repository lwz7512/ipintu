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
 * ��������ͻ��˴��ε�����˵��߼������������򽻵����������������򽻵���
 * @author lwz
 *
 */
public class ParamAnalyzer extends GenericServlet implements Servlet,ExtVisitorInterface {

	private Logger log = Logger.getLogger("** client param analyze: ");
	
	private static final long serialVersionUID = 1L;
	
	//��Springע��
	private ServiceAdaptor apiAdaptor;
	
	//�����Զ�������Springע��
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


	//���������WebEntrance�ĳ�ʼ����������
	public void init(ServletConfig config){
		try {
			super.init(config);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//��������ʱ��
		if(taskStarter!=null) taskStarter.runAutoTasks();
		
		//�������ݿ�ͬ������
		if(synchProcess!=null) synchProcess.start();
		
	}

	
	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		
		//TODO, ���ｫ�ͻ��˲���������������apiAdaptor
		//��apiAdaptor��װ����������
		String action = request.getParameter("method");
		
		if(action==null){
			log.warn(">>> �ͻ�û�д��� method �����������κβ��������أ�");
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
