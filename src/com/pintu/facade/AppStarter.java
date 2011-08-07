package com.pintu.facade;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.pintu.jobs.TaskStarter;
import com.pintu.sync.CacheToDB;

/**
 * 真正处理客户端传参到服务端的逻辑，与适配器打交道，适配器再与服务打交道；
 * @author lwz
 *
 */
public class AppStarter extends HttpServlet implements ExtVisitorInterface {

	private Logger log = Logger.getLogger(AppStarter.class);
	
	private static final long serialVersionUID = 1L;
	
	//由Spring注入
	private ApiAdaptor apiAdaptor;
	
	//启动自动任务，由Spring注入
	private TaskStarter taskStarter;
	//同步任务，由Spring注入
	private CacheToDB synchProcess;
	
//	//图片文件保存路径
//	private String filePath;
//	//图片文件暂存路径
//	private String tempPath;
	
	// 最大文件上传尺寸设置
	private int fileMaxSize = 4 * 1024 * 1024;
	//上传组件
	private ServletFileUpload upload;
	
	
	public AppStarter() {
		// TODO Auto-generated constructor stub
	}	
	
	//由WebEntrance在init时设置
	public void setImagePath(String filePath, String tempPath) {
		//将磁盘文件保存路径传进来
		apiAdaptor.setImagePath(filePath);		
		//初始化文件上传组件参数
		initUploadComponent(tempPath);
		
	}

	public void setApiAdaptor(ApiAdaptor apiAdaptor) {
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
		System.out.println("初始化配置config:"+taskStarter.toString()+"--"+synchProcess.toString());
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
	
	private void initUploadComponent(String tempPath){
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();
		// threshold 极限、临界值，即内存缓存 空间大小
		diskFactory.setSizeThreshold(fileMaxSize);
		// repository 贮藏室，即临时文件目录
		diskFactory.setRepository(new File(tempPath));

		upload = new ServletFileUpload(diskFactory);
		// 设置允许上传的最大文件大小 4M
		upload.setSizeMax(fileMaxSize);
		
	}

	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		System.out.println("1 appstater 开始解析表单");
		res.setContentType("text/plain;charset=UTF-8");
		PrintWriter pw = res.getWriter();
		
		//这里将客户端参数解析出来传给apiAdaptor
		//由apiAdaptor组装参数给服务
		String action = req.getParameter("method");
		System.out.println("method:"+action);
		
	    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		System.out.println("isMultipart value is:"+isMultipart);
		
		
		if(action==null && isMultipart){
			//授理上传图片的请求
			processMultiPart(req,pw);	
			
		}else if(action.equals(AppStarter.APPLYFORUSER)){
			//TODO, ...
			
		}else if(action.equals(AppStarter.OTHERMETHOD)){
			//TODO, ...
			
		}
		
		pw.close();

	}
	
	
	@SuppressWarnings("unchecked")
	private void processMultiPart(HttpServletRequest req, PrintWriter pw){
		try {
			log.debug(">>> Starting uploading...");			
			List<FileItem> fileItems = (List<FileItem>)upload.parseRequest(req);
			log.debug("<<< Uploading complete!");
			//送由适配器解析参数
			apiAdaptor.createTastePic(fileItems);
		} catch (SizeLimitExceededException e) {
			
			System.out.println(">>> 文件尺寸超过限制，不能上传！");
			pw.println(">>> 文件尺寸超过限制，不能上传！");
			return;
			
		}catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void destroy(){
		if(taskStarter!=null) taskStarter.stopTask();
	}

}
