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
import com.pintu.socket.ServerFlex;
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
	
	private AdProcess adProcess;

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

	public void setAdProcess(AdProcess adProcess) {
		this.adProcess = adProcess;
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

		log.debug(">>> appstater start to analyze form...");

		// 这里将客户端参数解析出来传给apiAdaptor,由apiAdaptor组装参数给服务
		String action = req.getParameter("method");
		log.debug("method:" + action);

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		
		log.debug("isMultipart value is:" + isMultipart);

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
				|| action.equals(AppStarter.RETRIEVE)
				|| action.equals(AppStarter.REGISTER)
				|| action.equals(AppStarter.APPLY)
				|| action.equals(AppStarter.EXAMINE)
				|| action.equals(AppStarter.VALIDATE)
				|| action.equals(AppStarter.CHECKOUT)) {
			
			demandProcess(action, req, res);
			return;
		} 
		
		//微博接入有关api
		if(action.equals(AppStarter.GETACCESSTOKENBYCODE) 
				||(action.equals(AppStarter.FORWARDTOWEIBO)) 
				||(action.equals(AppStarter.IMPROVEWEIBOUSER))){
			
			weiboProcess(action,req,res);
		}
		
		//有关微广告系统的所有api
		if (action.equals(AppStarter.GETTODAYADS)
				|| action.equals(AppStarter.SEARCHADS)
				|| action.equals(AppStarter.DELETEADSBYID)
				|| action.equals(AppStarter.CREATEADS)
				|| action.equals(AppStarter.GETADSBYID)
				|| action.equals(AppStarter.UPDATEADSBYID)
				|| action.equals(AppStarter.LOGINAD)
				|| action.equals(AppStarter.CHANGEPWD)
				|| action.equals(AppStarter.SEARCHVENDERS)
				|| action.equals(AppStarter.GETVENDERSBYID)
				|| action.equals(AppStarter.CREATEVENDERS)
				|| action.equals(AppStarter.REGISTVENDERS)
				|| action.equals(AppStarter.UPDATEVENDERSBYID)
				|| action.equals(AppStarter.VALIDATEVENDER)
				|| action.equals(AppStarter.CHECKOUTREGISTER)) {
			
			adProcess.doPostProcess(action, req, res);
			return;
		} 
		
		if (action.equals(AppStarter.GETIMAGEFILE)
				|| action.equals(AppStarter.GETIMAGEBYPATH) 
				|| action.equals(AppStarter.GETIMGBYRELATIVEPATH)) {
			
				doGetProcess(action, req, res);
				return;
		} else {
			assistProcess.doPostProcess(action,req,res);
			return;
		}
		
	}
	
	/**
	 * 处理微博接入相关api
	 * @param action
	 * @param req
	 * @param res
	 * @throws IOException 
	 */
	private void weiboProcess(String action, HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		
		if(action.equals(AppStarter.GETACCESSTOKENBYCODE)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String code = req.getParameter("code");
			String result = apiAdaptor.getAccessTokenByCode(code);
			log.debug(result);
			pw.print(result);
			pw.close();
			
		}else if(action.equals(AppStarter.FORWARDTOWEIBO)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			String picId = req.getParameter("picId");
			String result = apiAdaptor.forwardToWeibo(userId,picId);
			log.debug(result);
			pw.print(result);
			pw.close();
			
		}else if(action.equals(AppStarter.IMPROVEWEIBOUSER)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			String account = req.getParameter("account");
			String pwd = req.getParameter("password");
			String result = apiAdaptor.updateWeiboUser(userId,account,pwd);
			log.debug(result);
			pw.print(result);
			pw.close();
			
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
			log.debug(result);
			pw.print(result);
			pw.close();

		} else if (action.equals(AppStarter.REGISTER)) {
			// 注册，验证用户输入的验证码是否与发给他的一致，比较后完成注册返回相应信息
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String pwd = req.getParameter("password");
			String code = req.getParameter("inviteCode");
			String nick = req.getParameter("nickName");
			String result = apiAdaptor.registerUser(account, pwd, code,nick);
			log.debug(result);
			pw.write(result);
			pw.close();
			
		} else if (action.equals(AppStarter.RETRIEVE)) {
			// 申请，发送后由管理员授理
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String result = apiAdaptor.retrievePwd(account);
			log.debug(result);
			pw.write(result);
			pw.close();


		} else if (action.equals(AppStarter.APPLY)) {
			// 申请，发送后由管理员授理
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String reason = req.getParameter("reason");
			String result = apiAdaptor.sendApply(account, reason);
			log.debug(result);
			pw.write(result);
			pw.close();

		} else if (action.equals(AppStarter.VALIDATE)) {
			// 验证注册的账户是否已被注册
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			int result = apiAdaptor.validateAccount(account);
			log.debug(result);
			pw.print(result);
			pw.close();
			
		} else if (action.equals(AppStarter.CHECKOUT)) {
			// 验证注册的账户是否已被用（包括申请和注册）
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			int result = apiAdaptor.checkApplicant(account);
			log.debug(result);
			pw.print(result);
			pw.close();
			
		} else if (action.equals(AppStarter.EXAMINE)) {
				// 验证注册的账户是否已被用
				res.setContentType("text/plain;charset=UTF-8");
				PrintWriter pw = res.getWriter();
				String nickName = req.getParameter("nickName");
				int result = apiAdaptor.examineNickname(nickName);
				log.debug(result);
				pw.print(result);
				pw.close();
			}
	}

	/**
	 * 处理get方式的用户请求
	 * 
	 * @param action
	 * @param req
	 * @param res
	 * @throws IOException 
	 * @throws FileUploadException 
	 */
	private void doGetProcess(String action, HttpServletRequest req,
			HttpServletResponse res) throws IOException{
		if (action.equals(AppStarter.GETIMAGEFILE)) {
			// 要所图片id得到相应图片
			String tpId = req.getParameter("tpId");
			apiAdaptor.getImageFile(tpId, res);

		} else if (action.equals(AppStarter.GETIMAGEBYPATH)) {
			// 根据绝对路径得img(主要用于得到头像图)
			String path = req.getParameter("path");
			apiAdaptor.getImageByPath(path, res);
			
		} else if (action.equals(AppStarter.GETIMGBYRELATIVEPATH)){
			//根据相对路径取图片（即uploadFile的下一级，如：/adsImg/xxx.jpg ）
			String relativePath = req.getParameter("relativePath");
			apiAdaptor.getImgByRelativePath(relativePath,res);
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
				String method = getMethod(fileItems);
				if(method.equals("upload")){
					apiAdaptor.createTastePic(fileItems);
				}else if(method.equals("uploadAvatar")){
					apiAdaptor.createAvatar(fileItems);
				}else{
					String adResult = apiAdaptor.createAdImg(fileItems);
					if(!"".equals(adResult)){
						pw.write(adResult);
					}
				}
			}else{
				// 送由适配器解析参数前，先检查一下是否是正常用户
				boolean flag = examine(fileItems);
				if (flag) {
					//获取是要做什么操作，这里区分上传头像和贴图
					String method = getMethod(fileItems);
					if(method.equals("upload")){
						apiAdaptor.createTastePic(fileItems);
					}else if(method.equals("uploadAvatar")){
						apiAdaptor.createAvatar(fileItems);
					}
				} else {
					//上传广告图片
					String adResult = apiAdaptor.createAdImg(fileItems);
					if(!"".equals(adResult)){
						pw.write(adResult);
					}
				}
			}
			
		} catch (SizeLimitExceededException e) {

			log.debug(">>> File size exceeds the limit, can not upload!");
			pw.print(">>> File size exceeds the limit, can not upload!");
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
	
	private String getMethod(List<FileItem> fileItems) {
		String method = "";
		Iterator<FileItem> iter = fileItems.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();
			if (item.isFormField()) {
				if (item.getFieldName().equals("method")) {
					method = item.getString();
				}
			}
		}
		return method;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		// ApplicationContext 已经准备好，Spring配置初始化完成，可以启动任务了
		if (event instanceof ContextRefreshedEvent) {

			log.debug(">>>>>>>> Server startup complete, automatic task started <<<<<<<");

			// 上传文件保存路径
			String filePath = System.getProperty("filePath");
			// 初始化文件上传组件参数
			String tempPath = System.getProperty("tempPath");
			if (tempPath != null) {
				log.debug(">>>>> init file upload component...");
				initUploadComponent(tempPath);
			} else {
				log.warn(">>>>> !!! File upload path tempPath environment variable is null, can not initialize the upload component!");
			}

			if (apiAdaptor != null) {
				log.debug(">>> apiAdaptor is ready to use...");
				// 将磁盘文件保存路径传进来
				if (filePath != null) {
					apiAdaptor.setImagePath(filePath);
				} else {
					log.warn(">>>>> !!! File upload path filePath environment variable is null, can not initialize the upload component!");
				}
			}
			if (taskStarter != null) {
				log.debug(">>> taskStarter is ready to start...");
				taskStarter.runAutoTasks();
			}
			if (synchProcess != null) {
				log.debug(">>> synchProcess is ready to start...");
				synchProcess.start();
			}
			if (dailySync != null) {
				log.debug(">>> dailySync is ready to start...");
				dailySync.start();
			}
			
			//为flash增加843的socket端口
			ServerFlex serverFlex = new ServerFlex();
			serverFlex.runServerFlex();

		}

		// 处理关闭时发布的事件，停止所有的任务
		if (event instanceof ContextClosedEvent) {
			taskStarter.stopTask();
			synchProcess.stop();
			dailySync.stop();
		}

		if(GlobalController.isDebug){
			log.info("*********************************************");
			log.info(">>>Pintu app running in debug mode!!!");
			log.info("*********************************************");
		}else{
			log.info("*********************************************");
			log.info(">>>Pintu app running in release mode!!!");
			log.info("*********************************************");
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
