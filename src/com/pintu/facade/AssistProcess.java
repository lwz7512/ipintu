package com.pintu.facade;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pintu.beans.User;

public class AssistProcess {
	
	// 由Spring注入
	private ApiAdaptor apiAdaptor;
	// FIXME debug测试某些代码(发布时修改为false)
	private boolean isDebug = true;

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
		String user = req.getParameter("userId");
		if (!apiAdaptor.examineUser(user)) {
			return;
		}

		if (action.equals(AppStarter.GETAPPLICANT)) {
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
			pw.close();

		} else if (action.equals(AppStarter.ADDEVENT)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String title = req.getParameter("title");
			String detail = req.getParameter("detail");
			String time = req.getParameter("time");
			boolean flag = apiAdaptor
					.publishCommunityEvent(title, detail, time);
			if (flag) {
				pw.println("发布社区事件成功！");
			} else {
				pw.println("发布社区事件失败！");
			}
			pw.close();

		} else if (action.equals(AppStarter.ADDGIFT)) {
			res.setContentType("text/plain;charset=UTF-8");
			// PrintWriter pw = res.getWriter();
			// TODO 这里发布礼物时候要有图片上传(之后做)
			apiAdaptor.publishExchangeableGift();

		} else if (action.equals(AppStarter.GETLATESTPIC)) {
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getLatestPic();
			System.out.println(result);
			pw.println(result);
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

		} else if (action.equals(AppStarter.GETPICDETAIL)) {
			// 取得一副图片的详情
			res.setContentType("text/plain;charset=UTF-8");
			String tpId = req.getParameter("tpId");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getTPicDetailsById(tpId);
			System.out.println(result);
			pw.write(result);
			pw.close();

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
			apiAdaptor.addVoteToStory(follow, type, amount, voter, receiver);

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

			boolean flag = apiAdaptor.sendMessage(sender, receiver, content);
			System.out.println(flag);

			if (flag) {
				pw.println("发送消息成功！");
			} else {
				pw.println("发送消息失败！");
			}
			pw.close();

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
			pw.close();

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
			pw.close();

		} else if (action.equals(AppStarter.GETFAVORITEPICS)) {
			// 获取收藏列表
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			String result = apiAdaptor.getFavorTpics(userId, pageNum);
			System.out.println(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETTPICSBYUSER)) {
			// 获取指定用户图片列表
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			String result = apiAdaptor.getTpicsByUser(userId, pageNum);
			System.out.println(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETSTORIESBYUSER)) {
			// 获取指定用户故事列表
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String userId = req.getParameter("userId");
			int pageNum = Integer.parseInt(req.getParameter("pageNum"));
			String result = apiAdaptor.getStoryiesByUser(userId, pageNum);
			System.out.println(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETGIFTS)) {
			// 获取可换礼物
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getExchangeableGifts();
			System.out.println(result);
			pw.println(result);
			pw.close();

		} else if (action.equals(AppStarter.GETEVENTS)) {
			// 获取今日社区
			res.setContentType("text/plain;charset=UTF-8");
			PrintWriter pw = res.getWriter();
			String result = apiAdaptor.getCommunityEvents();
			System.out.println(result);
			pw.println(result);
			pw.close();

		} else {

		}

	}

	public void setApiAdaptor(ApiAdaptor apiAdaptor) {
		this.apiAdaptor = apiAdaptor;
	}

}
