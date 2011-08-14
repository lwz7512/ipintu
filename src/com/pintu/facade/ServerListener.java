package com.pintu.facade;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerListener implements ServletContextListener {

	//文件上传路径
	private String filePath;
	private String tempPath;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// 从context param中获取相对路径
		filePath = event.getServletContext().getInitParameter("filepath");
		tempPath = event.getServletContext().getInitParameter("temppath");

		//这时得到绝对路径
		filePath = event.getServletContext().getRealPath(filePath);
		tempPath = event.getServletContext().getRealPath(tempPath);
		
		//并保持在环境变量中
		System.setProperty("filePath", filePath);
		System.out.println(">>> Environment variable, filePath: "+filePath);
		//并保持在环境变量中
		System.setProperty("tempPath", tempPath);		
		System.out.println(">>> Environment variable, tempPath: "+tempPath);
		
		//初始化上传文件保存路径
		File fp = new File(filePath);
		if (!fp.exists())
			fp.mkdir();

		File tp = new File(tempPath);
		if (!tp.exists())
			tp.mkdir();

		System.out.println("文件存放目录、临时文件目录准备完毕 ...");

	}
	
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println(">>>>>> SERVER IS DOWN <<<<<<");		
	}


}
