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

import com.pintu.beans.User;
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

	// FIXME debug测试某些代码(发布时修改为false)
	private boolean isDebug = false;

	// 由Spring注入
	private ApiAdaptor apiAdaptor;

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

		// 这里将客户端参数解析出来传给apiAdaptor
		// 由apiAdaptor组装参数给服务
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

		} else if (action.equals(AppStarter.LOGON)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String pwd = req.getParameter("password");
			// 登录成功后，返回一个用户的id，否则返回错误信息
			String result = apiAdaptor.getExistUser(account, pwd);
			System.out.println(result);
			pw.println(result);
			
			if(!result.equals("0") && !result.equals("-1") && result.equals("a053beae20125b5b")){
				req.getRequestDispatcher("jsp/admin.jsp").forward(req, res);
			}else if(!result.equals("0") && !result.equals("-1") ){
				req.getRequestDispatcher("jsp/normal.jsp").forward(req, res);
			}

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

		} else if (action.equals(AppStarter.APPLY)) {
			// 申请，发送后由管理员授理
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			String reason = req.getParameter("reason");
			String result = apiAdaptor.sendApply(account, reason);
			System.out.println(result);
			pw.write(result);

		} else if (action.equals(AppStarter.GETAPPLICANT)) {
			List<User> list = apiAdaptor.getApplicant();
			req.setAttribute("tempUser", list);
			req.getRequestDispatcher("jsp/accept.jsp").forward(req, res);

		} else if (action.equals(AppStarter.ACCEPT)) {
			// 管理员处理申请，审核后发带邀请码的链接的为内容的邮件~
			// "Email has been sent to please note to check!"
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			// 申请人，即消息的发送者
			String id = req.getParameter("id");
			String account = req.getParameter("account");
			String opt = req.getParameter("opt");

			// String url = req.getRequestURL().toString();

			String url = "";

			if (isDebug) {
				url = req.getServerName() + ":" + req.getServerPort()
						+ req.getContextPath();
			} else {
				url = "ipintu.com/ipintu";
			}

			String result = apiAdaptor.acceptApply(id, account, url, opt);
			System.out.println(result);
			pw.println(result);

		} else if (action.equals(AppStarter.VALIDATE)) {
			// 验证注册的账户是否已被用
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String account = req.getParameter("account");
			int result = apiAdaptor.validateAccount(account);
			System.out.println(result);
			pw.println(result);

			
		} else if (!isMultipart) {
			// 安全验证：如果不是上传文件请求，取用户id参数，判断是否为正常用户
			String user = req.getParameter("user");
			if (!apiAdaptor.examineUser(user)) {
				return;
			} else {
				if (action.equals(AppStarter.GETGALLERYBYTIME)) {
					// 处理取长廊缩略图信息的请求
					res.setContentType("text/plain;charset=UTF-8");
					String startTime = req.getParameter("startTime");
					String endTime = req.getParameter("endTime");
					PrintWriter pw = res.getWriter();
					String result = apiAdaptor.getGalleryByTime(startTime,
							endTime);
					System.out.println(result);
					pw.println(result);
					pw.close();

				} else if (action.equals(AppStarter.GETIMAGEFILE)) {
					// 要所图片id得到相应图片
					String tpId = req.getParameter("tpId");
					apiAdaptor.getImageFile(tpId, res);

				} else if (action.equals(AppStarter.GETPICDETAIL)) {
					// 取得一副图片的详情
					res.setContentType("text/plain;charset=UTF-8");
					String tpId = req.getParameter("tpId");
					PrintWriter pw = res.getWriter();
					String result = apiAdaptor.getTPicDetailsById(tpId);
					System.out.println(result);
					pw.write(result);
					pw.close();

				} else if (action.equals(AppStarter.GETIMAGEBYPATH)) {
					// 根据路径得img(主要用于得到头像图)
					String path = req.getParameter("path");
					apiAdaptor.getImageByPath(path, res);

				} else if (action.equals(AppStarter.ADDSTORY)) {
					// 为图片添加故事
					res.setContentType("text/plain;charset=UTF-8");
					String follow = req.getParameter("follow");
					String owner = req.getParameter("owner");
					String content = req.getParameter("content");

					apiAdaptor.addStoryToPicture(follow, owner, content);

				} else if (action.equals(AppStarter.ADDCOMMENT)) {
					// 为图片添加评论
					String follow = req.getParameter("follow");
					String owner = req.getParameter("owner");
					String content = req.getParameter("content");

					apiAdaptor.addCommentToPicture(follow, owner, content);

				} else if (action.equals(AppStarter.GETSTORIESOFPIC)) {
					// 得到某副图片的所有故事
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String tpId = req.getParameter("tpId");
					String result = apiAdaptor.getStoryDetailsOfPic(tpId);
					System.out.println(result);
					pw.write(result);
					pw.close();

				} else if (action.equals(AppStarter.GETCOMMENTSOFPIC)) {
					// 得到某副图片的所有评论
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String tpId = req.getParameter("tpId");
					String result = apiAdaptor.getCommentsOfPic(tpId);
					System.out.println(result);
					pw.write(result);
					pw.close();

				} else if (action.equals(AppStarter.ADDVOTE)) {
					// 为故事添加投票
					String follow = req.getParameter("follow");
					String type = req.getParameter("type");
					String amount = req.getParameter("amount");
					String voter = req.getParameter("owner");
					String receiver = req.getParameter("receiver");
					apiAdaptor.addVoteToStory(follow, type, amount, voter,
							receiver);

				} else if (action.equals(AppStarter.GETUSERDETAIL)) {
					// 根据用户id得到该用户详情
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String userId = req.getParameter("userId");
					String result = apiAdaptor.getUserDetail(userId);
					System.out.println(result);
					pw.write(result);
					pw.close();

				} else if (action.equals(AppStarter.SENDMSG)) {
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();

					String sender = req.getParameter("userId");
					String receiver = req.getParameter("receiver");
					String content = req.getParameter("content");

					boolean flag = apiAdaptor.sendMessage(sender, receiver,
							content);
					System.out.println(flag);

					if (flag) {
						pw.println("发送消息成功！");
					} else {
						pw.println("发送消息失败！");
					}

				} else if (action.equals(AppStarter.GETUSERMSG)) {
					// 得到收件箱详情
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();

					String userId = req.getParameter("userId");
					String result = apiAdaptor.getUserMsg(userId);
					System.out.println(result);
					pw.println(result);
					pw.close();

				} else if (action.equals(AppStarter.CHANGEMSGSTATE)) {

					String msgIds = req.getParameter("msgIds");
					apiAdaptor.changeMsgState(msgIds);

				} else if (action.equals(AppStarter.GETHOTPICTURE)) {
					// 取得热图
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String result = apiAdaptor.getHotPicture();
					System.out.println(result);
					pw.println(result);
					pw.close();
				} else if (action.equals(AppStarter.GETClASSICALPINTU)) {
					// 取得经典品图
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String result = apiAdaptor.getClassicalStory();
					System.out.println(result);
					pw.println(result);
					pw.close();
				} else if (action.equals(AppStarter.GETUSERESTATE)) {
					// 取得用户基本信息和资产详情
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String userId = req.getParameter("userId");
					String result = apiAdaptor.getUserEstate(userId);
					System.out.println(result);
					pw.println(result);
					pw.close();
				} else if (action.equals(AppStarter.MARKTHEPIC)) {
					// 收藏图片
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String userId = req.getParameter("userId");
					String picId = req.getParameter("picId");
					boolean flag = apiAdaptor.markFavoritePic(userId, picId);
					if (flag) {
						pw.println("收藏图片成功！");
					} else {
						pw.println("收藏图片失败！");
					}

				} else if (action.equals(AppStarter.DELETEONEFAVOR)) {
					// 删除收藏的图片
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String fId = req.getParameter("fId");
					boolean flag = apiAdaptor.deleteOneFavorite(fId);
					if (flag) {
						pw.println("删除收藏成功！");
					} else {
						pw.println("删除收藏失败！");
					}

				} else if (action.equals(AppStarter.GETFAVORITEPICS)) {
					// 获取收藏列表
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String userId = req.getParameter("userId");
					int pageNum = Integer.parseInt(req.getParameter("pageNum"));
					String result = apiAdaptor.getFavorTpics(userId, pageNum);
					System.out.println(result);
					pw.println(result);

				} else if (action.equals(AppStarter.GETTPICSBYUSER)) {
					// 获取指定用户图片列表
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String userId = req.getParameter("userId");
					int pageNum = Integer.parseInt(req.getParameter("pageNum"));
					String result = apiAdaptor.getTpicsByUser(userId, pageNum);
					System.out.println(result);
					pw.println(result);

				} else if (action.equals(AppStarter.GETSTORIESBYUSER)) {
					// 获取指定用户故事列表
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String userId = req.getParameter("userId");
					int pageNum = Integer.parseInt(req.getParameter("pageNum"));
					String result = apiAdaptor.getStoryiesByUser(userId,
							pageNum);
					System.out.println(result);
					pw.println(result);

				} else if (action.equals(AppStarter.GETGIFTS)) {
					// 获取可换礼物
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String result = apiAdaptor.getExchangeableGifts();
					System.out.println(result);
					pw.println(result);

				} else if (action.equals(AppStarter.GETEVENTS)) {
					// 获取今日社区
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String result = apiAdaptor.getCommunityEvents();
					System.out.println(result);
					pw.println(result);

				} else if (action.equals(AppStarter.ADDEVENT)) {
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String title = req.getParameter("title");
					String detail = req.getParameter("detail");
					String time = req.getParameter("time");
					boolean flag = apiAdaptor.publishCommunityEvent(title,
							detail, time);
					if (flag) {
						pw.println("发布社区事件成功！");
					} else {
						pw.println("发布社区事件失败！");
					}

				} else if (action.equals(AppStarter.ADDGIFT)) {
					res.setContentType("text/plain;charset=UTF-8");
					// PrintWriter pw = res.getWriter();
					// TODO 这里发布礼物时候要有图片上传
					apiAdaptor.publishExchangeableGift();

				} else if (action.equals(AppStarter.GETLATESTPIC)) {
					res.setContentType("text/plain;charset=UTF-8");
					PrintWriter pw = res.getWriter();
					String result = apiAdaptor.getLatestPic();
					System.out.println(result);
					pw.println(result);

				} else {

				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void processMultiPart(HttpServletRequest req, PrintWriter pw) {
		try {
			log.debug(">>> Starting uploading...");
			List<FileItem> fileItems = (List<FileItem>) upload
					.parseRequest(req);
			log.debug("<<< Uploading complete!");
			// 送由适配器解析参数前，先检查一下是否是正常用户
			boolean flag = examine(fileItems);
			if (flag) {
				apiAdaptor.createTastePic(fileItems);
			} else {
				return;
			}
		} catch (SizeLimitExceededException e) {

			System.out.println(">>> 文件尺寸超过限制，不能上传！");
			pw.println(">>> 文件尺寸超过限制，不能上传！");
			return;

		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean examine(List<FileItem> fileItems) {
		boolean flag = false;
		Iterator<FileItem> iter = fileItems.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();
			if (item.isFormField()) {
				if (item.getFieldName().equals("user")) {
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

			System.out.println(">>>>>>>> Server 启动完成， 开始启动自动任务 <<<<<<<");

			// 上传文件保存路径
			String filePath = System.getProperty("filePath");
			// 初始化文件上传组件参数
			String tempPath = System.getProperty("tempPath");
			if (tempPath != null) {
				System.out.println(">>>>> init file upload component...");
				initUploadComponent(tempPath);
			} else {
				log.warn(">>>>> !!! 文件上传路径tempPath环境变量为null，不能初始化上传组件!");
			}

			if (apiAdaptor != null) {
				System.out.println(">>> apiAdaptor is ready to use...");
				// 将磁盘文件保存路径传进来
				if (filePath != null) {
					apiAdaptor.setImagePath(filePath);
				} else {
					log.warn(">>>>> !!! 文件上传路径filePath环境变量为null，不能初始化上传路径!");
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
