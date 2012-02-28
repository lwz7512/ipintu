package com.pintu.facade;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class AssistProcess {
	
	// 由Spring注入
	private ApiAdaptor apiAdaptor;


	private Logger log = Logger.getLogger(AssistProcess.class);
	
	/**
	 * 处理正常用户登录post的请求
	 * 
	 * @param action
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPostProcess(String action, HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {

		// 安全验证：如果不是上传文件请求，取用户id参数，判断是否为正常用户
		if(!GlobalController.isDebug){
			String user = req.getParameter("userId");
			if (!apiAdaptor.examineUser(user)) {
				return;
			}
		}

		if (action.equals(AppStarter.GETAPPLICANT)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result= apiAdaptor.getApplicant();
			log.debug(result);
			pw.println(result);

		} else if (action.equals(AppStarter.ACCEPT)) {
			// 管理员处理申请，审核后发带邀请码的链接的为内容的邮件~
			// "Email has been sent to please note to check!"
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			// 申请人，即消息的发送者
			String account = req.getParameter("account");
			String opt = req.getParameter("opt");

			// String url = req.getRequestURL().toString();

			String url = "";

			if (GlobalController.isDebug) {
				url = req.getServerName() + ":" + req.getServerPort()
						+ req.getContextPath();
			} else {
				url = "ipintu.com/ipintu";
			}
			
			if(apiAdaptor.isProcessed(account)){
				return;
			}else{
				String result = apiAdaptor.acceptApply(account, url, opt);
				log.debug(result);
				pw.println(result);
				pw.close();
			}
			

		} else if (action.equals(AppStarter.CREATEINVITECODE)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.createInviteCode();
			pw.print(result);
			pw.close();

		} else if (action.equals(AppStarter.PUBLISHEVENT)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String title = req.getParameter("title");
			String detail = req.getParameter("detail");
			String time = req.getParameter("time");
			String result = apiAdaptor
					.publishCommunityEvent(title, detail, time);
			pw.print(result);
			pw.close();

		} else if (action.equals(AppStarter.PUBLISHGIFT)) {
			res.setContentType("text/plain;charset=UTF-8");
			// PrintWriter pw = res.getWriter();
			// 这里发布礼物时候要有图片上传(之后做)
			apiAdaptor.publishExchangeableGift();

		} else if (action.equals(AppStarter.GETLATESTPIC)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getLatestTPicDesc();
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.REVIEWPICTURE)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String picId = req.getParameter("picId");
			String creationTime = req.getParameter("creationTime");
			String result = apiAdaptor.reviewPictureById(picId, creationTime);
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETGALLERYBYTIME)) {
			// 处理取长廊缩略图信息的请求
			res.setContentType("text/plain;charset=UTF-8");
			String startTime = req.getParameter("startTime");
			String endTime = req.getParameter("endTime");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getGalleryByTime(startTime, endTime);
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETPICDETAIL)) {
			// 取得一副图片的详情
			res.setContentType("text/plain;charset=UTF-8");
			String tpId = req.getParameter("tpId");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getTPicDetailsById(tpId);
			log.debug(result);
			pw.write(result);
			pw.close();

		} else if (action.equals(AppStarter.ADDSTORY)) {
			// 为图片添加故事
			res.setContentType("text/plain;charset=UTF-8");
			String follow = req.getParameter("follow");
			String owner = req.getParameter("owner");
			String content = req.getParameter("content");
			String source = req.getParameter("source");
			
			apiAdaptor.addStoryToPicture(follow, owner, content, source);

		} else if (action.equals(AppStarter.GETSTORIESOFPIC)) {
			// 得到某副图片的所有故事
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String tpId = req.getParameter("tpId");
			String result = apiAdaptor.getStoryDetailsOfPic(tpId);
			log.debug(result);
			pw.write(result);
			pw.close();

		} else if (action.equals(AppStarter.ADDVOTE)) {
			// 为故事添加投票
			String follow = req.getParameter("follow");
			String type = req.getParameter("type");
			String amount = req.getParameter("amount");
			String voter = req.getParameter("owner");
			String receiver = req.getParameter("receiver");
			apiAdaptor.addVoteToPic(follow, type, amount, voter, receiver);

		} else if (action.equals(AppStarter.GETUSERDETAIL)) {
			// 根据用户id得到该用户详情
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			String result = apiAdaptor.getUserDetail(userId);
			log.debug(result);
			pw.write(result);
			pw.close();

		} else if (action.equals(AppStarter.SENDMSG)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();

			String sender = req.getParameter("userId");
			String receiver = req.getParameter("receiver");
			String content = req.getParameter("content");
			String source = req.getParameter("source");

			String result = apiAdaptor.sendMessage(sender, receiver, content,source);
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETUSERMSG)) {
			// 得到收件箱详情
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();

			String userId = req.getParameter("userId");
			String result = apiAdaptor.getUserMsg(userId);
			log.debug(result);
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
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETUSERESTATE)) {
			// 取得用户基本信息和资产详情
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			String result = apiAdaptor.getUserEstate(userId);
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.MARKTHEPIC)) {
			// 收藏图片
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			String picId = req.getParameter("picId");
			String result = apiAdaptor.markFavoritePic(userId, picId);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.DELETEONEFAVOR)) {
			// 删除收藏的图片
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String fId = req.getParameter("fId");
			String result = apiAdaptor.deleteOneFavorite(fId);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETFAVORITEPICS)) {
			// 获取收藏列表
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			if(pageNum<=0){
				pageNum = 1;
			}
			String result = apiAdaptor.getFavorTpics(userId, pageNum);
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETTPICSBYUSER)) {
			// 获取指定用户图片列表
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			if(pageNum<=0){
				pageNum = 1;
			}
			String result = apiAdaptor.getTpicsByUser(userId, pageNum);
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETGIFTS)) {
			// 获取可换礼物
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getExchangeableGifts();
			log.debug(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETEVENTS)) {
			// 获取今日社区事件
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getCommunityEvents();
			log.debug(result);
			pw.println(result);
			pw.close();

		} else  if(action.equals(AppStarter.COLLECTSTATISTICS)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.collectStatistics();
			log.debug(result);
			pw.println(result);
			pw.close();

		} else  if(action.equals(AppStarter.CLASSICALSTATISTICS)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.classicalStatistics();
			log.debug(result);
			pw.println(result);
			pw.close();

		} else  if(action.equals(AppStarter.GETGALLERYFORWEB)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			if(pageNum<=0){
				pageNum = 1;
			}
			String result = apiAdaptor.getGalleryForWeb(pageNum);
			log.debug(result);
			pw.println(result);
			pw.close();
			
		}else if(action.equals(AppStarter.SEARCHBYTAG)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String tags = req.getParameter("tags");
			String result = apiAdaptor.searchByTag(tags);
			log.debug(result);
			pw.println(result);
			pw.close();
			
			//取得最热标签前三
		}else if(action.equals(AppStarter.GETHOTTAGS)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getHotTags();
			log.debug(result);
			pw.println(result);
			pw.close();
			
		}else if(action.equals(AppStarter.GETSYSTEMTAGS)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.geSystemTags();
			log.debug(result);
			pw.println(result);
			pw.close();
			
		}else if (action.equals(AppStarter.DELETEONECMT)) {
				// 删除收藏的图片
				res.setContentType("text/plain;charset=UTF-8");
				PrintWriter pw = res.getWriter();
				String sId = req.getParameter("sId");
				String result = apiAdaptor.deleteOneCmt(sId);
				pw.println(result);
				pw.close();

		}else if (action.equals(AppStarter.DELETEONEPIC)) {
			// 删除收藏的图片
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String pId = req.getParameter("pId");
			String result = apiAdaptor.deleteOnePic(pId);
			pw.println(result);
			pw.close();

		}else if(action.equals(AppStarter.GETTHUMBNAILSBYTAG)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String tagId = req.getParameter("tagId");
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			if(pageNum<=0){
				pageNum = 1;
			}
			String result = apiAdaptor.getThumbnailsByTag(tagId,pageNum);
			log.debug(result);
			pw.println(result);
			pw.close();
		
		}else if(action.equals(AppStarter.PICDARENSTATISTICS)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getPicDaren();
			log.debug(result);
			pw.println(result);
			pw.close();
			
		}else if(action.equals(AppStarter.CMTDARENSTATISTICS)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getCmtDaren();
			log.debug(result);
			pw.println(result);
			pw.close();
		}else if(action.equals(AppStarter.GETPICCOOLCOUNT)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String picId = req.getParameter("pId");
			String result = apiAdaptor.getPicCoolCount(picId);
			log.debug(result);
			pw.println(result);
			pw.close();
			
		}else if(action.equals(AppStarter.CONFIRM)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
		    String userId = req.getParameter("userId");
			String password = req.getParameter("password");
			int result = apiAdaptor.confirmPassword(userId,password);
			log.debug(result);
			pw.println(result);
			pw.close();
			
			
		}else if(action.equals(AppStarter.MODIFYPASSWORD)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
		    String userId = req.getParameter("userId");
			String newPwd = req.getParameter("newPwd");
			String result = apiAdaptor.modifyPasswordById(userId,newPwd);
			log.debug(result);
			pw.println(result);
			pw.close();
		
		}else if(action.equals(AppStarter.GETRANDGALLERY)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getRandGallery();
			log.debug(result);
			pw.println(result);
			pw.close(); 
			
		}else if(action.equals(AppStarter.ACTIVEUSERRANKING)){
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getActiveUserRanking();
			log.debug(result);
			pw.println(result);
			pw.close(); 
			
		}else{
			
		}

	}

	public void setApiAdaptor(ApiAdaptor apiAdaptor) {
		this.apiAdaptor = apiAdaptor;
	}

}
