package com.pintu.facade;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
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
import org.springframework.context.event.ContextClosedEvent;
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
public class AppStarter extends HttpServlet implements ApplicationListener,
		ExtVisitorInterface {

	private Logger log = Logger.getLogger(AppStarter.class);

	private static final long serialVersionUID = 1L;
	
	// 由Spring注入
	private ApiAdaptor apiAdaptor;
	
	private AssistProcess assistProcess;

	// 启动自动任务，由Spring注入
	private TaskStarter taskStarter;
	// 同步任务，由Spring注入
	private CacheToDB synchProcess;
	// 同步任务，由Spring注入
	private DBToCache dailySync;

	// 最大文件上传尺寸设置
	private int fileMaxSize = 4 * 1024 * 1024;
	// 上传组件
	private ServletFileUpload upload;

	public AppStarter() {
		// do nothing...
	}

	public void setApiAdaptor(ApiAdaptor apiAdaptor) {
		this.apiAdaptor = apiAdaptor;
	}

	public void setAssistProcess(AssistProcess assistProcess) {
		this.assistProcess = assistProcess;
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

		System.out.println(">>> appstater start to analyze form...");

		// 这里将客户端参数解析出来传给apiAdaptor,由apiAdaptor组装参数给服务
		String action = req.getParameter("method");
		System.out.println("method:" + action);

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		System.out.println("isMultipart value is:" + isMultipart);

		if (action == null && isMultipart == false) {
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
			return;
		}

		if (action.equals(AppStarter.LOGON)
						|| action.equals(AppStarter.REGISTER)
						|| action.equals(AppStarter.APPLY)
						|| action.equals(AppStarter.EXAMINE)
						|| action.equals(AppStarter.VALIDATE)) {
			
			demandProcess(action, req, res);
			return;
		} 
		
		if (action.equals(AppStarter.GETIMAGEFILE) || action
						.equals(AppStarter.GETIMAGEBYPATH)) {
			
			doGetProcess(action, req, res);
		} else {
			assistProcess.doPostProcess(action,req,res);
		}
		
	}
	
	
	/**
	 * 处理 登录、 注册、申请、验证用户
	 * 
	 * @param action
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	private void demandProcess(String action, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		if (action.equals(AppStarter.LOGON)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String pwd = req.getParameter("password");
			// 登录成功后，返回一个用户的id，否则返回错误信息
			String result = apiAdaptor.getExistUser(account, pwd);
			System.out.println(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.REGISTER)) {
			// 注册，验证用户输入的验证码是否与发给他的一致，比较后完成注册返回相应信息
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String pwd = req.getParameter("password");
			String code = req.getParameter("inviteCode");
			String result = apiAdaptor.registerUser(account, pwd, code);
			System.out.println(result);
			pw.write(result);
			pw.close();

		} else if (action.equals(AppStarter.APPLY)) {
			// 申请，发送后由管理员授理
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String reason = req.getParameter("reason");
			String result = apiAdaptor.sendApply(account, reason);
			System.out.println(result);
			pw.write(result);
			pw.close();

		} else if (action.equals(AppStarter.VALIDATE)) {
			// 验证注册的账户是否已被用
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			int result = apiAdaptor.validateAccount(account);
			System.out.println(result);
			pw.println(result);
			pw.close();
		} else if (action.equals(AppStarter.EXAMINE)) {
				// 验证注册的账户是否已被用
				res.setContentType("text/plain;charset=UTF-8");
				PrintWriter pw = res.getWriter();
				String nickName = req.getParameter("nickName");
				int result = apiAdaptor.examineNickname(nickName);
				System.out.println(result);
				pw.println(result);
				pw.close();
			}
	}

	/**
	 * 处理get方式的用户请求
	 * 
	 * @param action
	 * @param req
	 * @param res
	 */
	private void doGetProcess(String action, HttpServletRequest req,
			HttpServletResponse res) {
		if (action.equals(AppStarter.GETIMAGEFILE)) {
			// 要所图片id得到相应图片
			String tpId = req.getParameter("tpId");
			apiAdaptor.getImageFile(tpId, res);

		} else if (action.equals(AppStarter.GETIMAGEBYPATH)) {
			// 根据路径得img(主要用于得到头像图)
			String path = req.getParameter("path");
			apiAdaptor.getImageByPath(path, res);
		}
	}

	
	@SuppressWarnings("unchecked")
	private void processMultiPart(HttpServletRequest req, PrintWriter pw) {
		try {
			log.debug(">>> Starting uploading...");
			List<FileItem> fileItems = (List<FileItem>) upload
					.parseRequest(req);
			log.debug("<<< Uploading complete!");
			
			if(GlobalController.isDebug){
				apiAdaptor.createTastePic(fileItems);
			}else{
				// 送由适配器解析参数前，先检查一下是否是正常用户
				boolean flag = examine(fileItems);
				if (flag) {
					apiAdaptor.createTastePic(fileItems);
				} else {
					return;
				}
			}
			
		} catch (SizeLimitExceededException e) {

			System.out.println(">>> File size exceeds the limit, can not upload!");
			pw.println(">>> File size exceeds the limit, can not upload!");
			return;

		} catch (FileUploadException e) {
			e.printStackTrace();
		}

	}

	private boolean examine(List<FileItem> fileItems) {
		boolean flag = false;
		Iterator<FileItem> iter = fileItems.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();
			if (item.isFormField()) {
				if (item.getFieldName().equals("userId")) {
					String userId = item.getString();
					flag = apiAdaptor.examineUser(userId);
					break;
				}
			}
		}
		return flag;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		// ApplicationContext 已经准备好，Spring配置初始化完成，可以启动任务了
		if (event instanceof ContextRefreshedEvent) {

			System.out.println(">>>>>>>> Server startup complete, automatic task started <<<<<<<");

			// 上传文件保存路径
			String filePath = System.getProperty("filePath");
			// 初始化文件上传组件参数
			String tempPath = System.getProperty("tempPath");
			if (tempPath != null) {
				System.out.println(">>>>> init file upload component...");
				initUploadComponent(tempPath);
			} else {
				log.warn(">>>>> !!! File upload path tempPath environment variable is null, can not initialize the upload component!");
			}

			if (apiAdaptor != null) {
				System.out.println(">>> apiAdaptor is ready to use...");
				// 将磁盘文件保存路径传进来
				if (filePath != null) {
					apiAdaptor.setImagePath(filePath);
				} else {
					log.warn(">>>>> !!! File upload path filePath environment variable is null, can not initialize the upload component!");
				}
			}
			if (taskStarter != null) {
				System.out.println(">>> taskStarter is ready to start...");
				taskStarter.runAutoTasks();
			}
			if (synchProcess != null) {
				System.out.println(">>> synchProcess is ready to start...");
				synchProcess.start();
			}
			if (dailySync != null) {
				System.out.println(">>> dailySync is ready to start...");
				dailySync.start();
			}

		}

		// 处理关闭时发布的事件，停止所有的任务
		if (event instanceof ContextClosedEvent) {
			taskStarter.stopTask();
			synchProcess.stop();
			dailySync.stop();
		}

		if(GlobalController.isDebug){
			log.info(">>>Pintu app running in debug mode!!!");
		}else{
			log.info(">>>pintu app running in release mode!!!");
		}
		
	} // end of onApplicationEvent

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
