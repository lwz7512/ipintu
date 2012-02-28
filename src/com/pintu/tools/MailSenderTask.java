package com.pintu.tools;

import java.util.Properties;

import com.pintu.beans.Applicant;
import com.pintu.dao.DBAccessInterface;
import com.pintu.utils.MailSenderInfo;
import com.pintu.utils.PintuUtils;
import com.pintu.utils.SimpleMailSender;

public class MailSenderTask implements Runnable{

	private DBAccessInterface dbVisitor;
	
	private Properties propertyConfigurer;

	private Properties systemConfigurer;
	
	private String account;
	
	private String reason;
	
	private String result;
	
	

	@Override
	public void run() {
		Applicant tempUser = this.createApplicant(account, reason);
		int m = dbVisitor.insertApplicant(tempUser);
		if (m == 1) {
			//这里，申请成功发邮件给客服
			informService(account,reason);
			this.setResult(systemConfigurer.getProperty("applyProcess").toString());
		}else{
			this.setResult(systemConfigurer.getProperty("applyError").toString());
		}
	}

	private Applicant createApplicant(String account, String reason) {
		Applicant tempUser = new Applicant();
		tempUser.setId(PintuUtils.generateUID());
		tempUser.setAccount(account);
		tempUser.setApplyReason(reason);
		tempUser.setApplyTime(PintuUtils.getFormatNowTime());
		return tempUser;
	}
	
	private void informService(String account, String reason) {
		String content = "用户："+account+" 申请加入爱品图，申请理由为："+reason+"<br/>点击这里去登录授理吧<a href='http://ipintu.com/ipintu/html/login.html'>http://ipintu.com/ipintu/html/login.html</a>";
		String address = propertyConfigurer.getProperty("serviceMailAddress")
				.toString();
		try{
			sendMail(address, content);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void sendMail(String toAddress, String content) {
		MailSenderInfo mailInfo = new MailSenderInfo();
		// mailInfo.setMailServerPort("25");
		// mailInfo.setValidate(true);
		String host = propertyConfigurer.getProperty("mailServiceHost")
				.toString();
		String username = propertyConfigurer.getProperty("serviceMailUsername")
				.toString();
		String password = propertyConfigurer.getProperty("serviceMailPassword")
				.toString();
		String address = propertyConfigurer.getProperty("serviceMailAddress")
				.toString();
		mailInfo.setMailServerHost(host);
		mailInfo.setUserName(username);
		mailInfo.setPassword(password);// 邮箱密码
		mailInfo.setFromAddress(address);
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject("爱品图通知");
		// 邮件内容
		mailInfo.setContent(content);
		// 发送html格式
		SimpleMailSender.sendHtmlMail(mailInfo);
	}
	
	public void setDbVisitor(DBAccessInterface dbVisitor) {
		this.dbVisitor = dbVisitor;
	}


	public void setPropertyConfigurer(Properties propertyConfigurer) {
		this.propertyConfigurer = propertyConfigurer;
	}



	public void setSystemConfigurer(Properties systemConfigurer) {
		this.systemConfigurer = systemConfigurer;
	}



	public void setAccount(String account) {
		this.account = account;
	}



	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
