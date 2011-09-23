package com.pintu.test;

import com.pintu.utils.Encrypt;
import com.pintu.utils.MailSenderInfo;
import com.pintu.utils.SimpleMailSender;

public class TestSendMail {

	public static void main(String[] args) {
		// 这个类主要是设置邮件　　
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
//		mailInfo.setMailServerPort("25");
//		mailInfo.setValidate(true);
		mailInfo.setUserName("liu3385ml");
		mailInfo.setPassword("15100357236");// 邮箱密码
		mailInfo.setFromAddress("liu3385ml@163.com");
		mailInfo.setToAddress("826299671@qq.com");
		mailInfo.setSubject("测试邮件hmtl格式");
		
		String inviteCode = Encrypt.encrypt(String.valueOf(System.currentTimeMillis())).substring(0, 6); 
		System.out.println(inviteCode);
		String content = "<a href=http://localhost:8080/ipintu/pintuapi?method=register&inviteCode="+inviteCode+"' target='_blank'></a>";
		mailInfo.setContent(content);
//		mailInfo.setContent("设置邮箱内容 <li style='width:150px;'><a href='http://www.baidu.com/' target='_blank'>百度一下，你就知道</a></li>");
		// 这个类主要来发送邮件　　
//		SimpleMailSender sms = new SimpleMailSender();
//	    sms.sendTextMail(mailInfo);//发送文体格式
		SimpleMailSender.sendHtmlMail(mailInfo);// 发送html格式　
	}

}