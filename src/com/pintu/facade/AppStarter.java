package com.pintu.facade;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.pintu.jobs.TaskStarter;
import com.pintu.sync.CacheToDB;
import com.pintu.sync.DBToCache;

/**
 * 真正处理客户端传参到服务端的逻辑，与适配器打交道，适配器再与服务打交道；
 * 
 * @author lwz
 * 
 */
@SuppressWarnings("rawtypes")
public class AppStarter extends HttpServlet implements  ApplicationListener,ExtVisitorInterface  {

	private Logger log = Logger.getLogger(AppStarter.class);

	private static final long serialVersionUID = 1L;

	// 由Spring注入
	private ApiAdaptor apiAdaptor;

	// 启动自动任务，由Spring注入
	private TaskStarter taskStarter;
	// 同步任务，由Spring注入
	private CacheToDB synchProcess;
	//同步任务，由Spring注入
	private DBToCache dailySync;

	// 最大文件上传尺寸设置
	private int fileMaxSize = 4 * 1024 * 1024;
	// 上传组件
	private ServletFileUpload upload;
	
	

	public AppStarter() {
		//do nothing...
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

	public void setDailySync(DBToCache dailySync) {
		this.dailySync = dailySync;
	}


	@Override
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		System.out.println(">>> appstater 开始解析表单");

		// 这里将客户端参数解析出来传给apiAdaptor
		// 由apiAdaptor组装参数给服务
		String action = req.getParameter("method");
		System.out.println("method:" + action);

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		System.out.println("isMultipart value is:" + isMultipart);
		
		if(action==null && isMultipart==false){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			pw.write("请求无效！");	
			pw.close();
			return;
		}

		if (action == null && isMultipart) {
			// 因上传文件enctype的特殊处理，所以得不到参数，故只判断isMultipart
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			// 授理上传图片的请求
			processMultiPart(req, pw);
			pw.close();
		} else if (action.equals(AppStarter.GETGALLERYBYTIME)) {
			// 处理取长廊缩略图信息的请求
			res.setContentType("text/plain;charset=UTF-8");
			String startTime = req.getParameter("startTime");
			String endTime = req.getParameter("endTime");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getGalleryByTime(startTime, endTime);
			System.out.println(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETIMAGEFILE)) {
			//要所图片id得到相应图片
			String tpId = req.getParameter("tpId");
			apiAdaptor.getImageFile(tpId, res);

		}else if (action.equals(AppStarter.GETPICDETAIL)) {
			//取得一副图片的详情
			res.setContentType("text/plain;charset=UTF-8");
			String tpId = req.getParameter("tpId");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getTPicDetailsById(tpId);
			System.out.println(result);
			pw.write(result);	
			pw.close();

		}else if(action.equals(AppStarter.GETIMAGEBYPATH)) {
			//根据路径得img(主要用于得到头像图)
			String path = req.getParameter("path");
			apiAdaptor.getImageByPath(path, res);
			
		}else if (action.equals(AppStarter.ADDSTORY)) {
			//为图片添加故事
			res.setContentType("text/plain;charset=UTF-8");
			String follow = req.getParameter("follow");
			String owner = req.getParameter("owner");
			String content = req.getParameter("content");
			String classical = req.getParameter("classical");
			
			apiAdaptor.createStory(follow, owner, content, classical);
			
		} else if (action.equals(AppStarter.ADDCOMMENT)) {
			//为图片添加评论
			String follow = req.getParameter("follow");
			String owner = req.getParameter("owner");
			String content = req.getParameter("content");
			
			apiAdaptor.createComment(follow,owner,content);

		}else if (action.equals(AppStarter.GETSTORIESOFPIC)) {
			//得到某副图片的所有故事
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String tpId = req.getParameter("tpId");
			String result = apiAdaptor.getStoriesOfPic(tpId);
			System.out.println(result);
			pw.write(result);
			pw.close();
			
		} else if (action.equals(AppStarter.GETCOMMENTSOFPIC)) {
			//得到某副图片的所有评论
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String tpId = req.getParameter("tpId");
			String result = apiAdaptor.getCommentsOfPic(tpId);
			System.out.println(result);
			pw.write(result);
			pw.close();
			
		} else if (action.equals(AppStarter.ADDVOTE)) {
			//为故事添加投票
			String follow = req.getParameter("follow");
			String type = req.getParameter("type");
			String amount =req.getParameter("amount");
			
			apiAdaptor.createVote(follow, type, amount);
			
		}else if (action.equals(AppStarter.GETUSERDETAIL)) {
			//根据用户id得到该用户详情
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			String result = apiAdaptor.getUserDetail(userId);
			System.out.println(result);
			pw.write(result);
			pw.close();
			
		}  else if (action.equals(AppStarter.OTHERMETHOD)) {
			// TODO, ...

		} else {

		}
	}

	@SuppressWarnings("unchecked")
	private void processMultiPart(HttpServletRequest req, PrintWriter pw) {
		try {
			log.debug(">>> Starting uploading...");
			List<FileItem> fileItems = (List<FileItem>) upload
					.parseRequest(req);
			log.debug("<<< Uploading complete!");
			// 送由适配器解析参数
			apiAdaptor.createTastePic(fileItems);
		} catch (SizeLimitExceededException e) {

			System.out.println(">>> 文件尺寸超过限制，不能上传！");
			pw.println(">>> 文件尺寸超过限制，不能上传！");
			return;

		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		//ApplicationContext 已经准备好，Spring配置初始化完成，可以启动任务了
		if(event instanceof ContextRefreshedEvent){
			
			System.out.println(">>>>>>>> Server 启动完成， 开始启动自动任务 <<<<<<<");
			
			//上传文件保存路径
			String filePath = System.getProperty("filePath");
			// 初始化文件上传组件参数
			String tempPath = System.getProperty("tempPath");
			if(tempPath!=null){
				System.out.println(">>>>> init file upload component...");
				initUploadComponent(tempPath);				
			}else{
				log.warn(">>>>> !!! 文件上传路径tempPath环境变量为空，不能初始化上传组件!");
			}
			
			if(apiAdaptor!=null){
				System.out.println(">>> apiAdaptor is ready to use...");
				// 将磁盘文件保存路径传进来
				if(filePath!=null){
					apiAdaptor.setImagePath(filePath);					
				}else{
					log.warn(">>>>> !!! 文件上传路径filePath环境变量为空，不能初始化上传路径!");
				}
			}
			if(taskStarter!=null){
				System.out.println(">>> taskStarter is ready to start...");
				taskStarter.runAutoTasks();
			}
			if(synchProcess!=null){
				System.out.println(">>> synchProcess is ready to start...");
				synchProcess.start();
			}
			if(dailySync!=null){
				System.out.println(">>> dailySync is ready to start...");
				dailySync.start();
			}

		}
		
	} //end of onApplicationEvent
	
	private void initUploadComponent(String tempPath) {
		DiskFileItemFactory diskFactory = new DiskFileItemFactory();
		// threshold 极限、临界值，即内存缓存 空间大小
		diskFactory.setSizeThreshold(fileMaxSize);
		// repository 贮藏室，即临时文件目录
		diskFactory.setRepository(new File(tempPath));

		upload = new ServletFileUpload(diskFactory);
		// 设置允许上传的最大文件大小 4M
		upload.setSizeMax(fileMaxSize);

	}
	

}
