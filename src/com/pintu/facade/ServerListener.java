package com.pintu.facade;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerListener implements ServletContextListener {

	private String filePath;
	private String tempPath;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// 从context param中获取路径，并保持在环境变量中
		filePath = event.getServletContext().getInitParameter("filepath");
		tempPath = event.getServletContext().getInitParameter("temppath");
		//这时得到全路径
		String realFilePath =	event.getServletContext().getRealPath(filePath);
		String realTempPath = event.getServletContext().getRealPath(tempPath);
		
		System.setProperty("filePath", realFilePath);
		System.out.println(">>> Environment variable, filePath: "+realFilePath);
		
		System.setProperty("tempPath", realTempPath);		
		System.out.println(">>> Environment variable, tempPath: "+realTempPath);
		//初始化上传文件保存路径
		initPicSavePath(event.getServletContext());
	}
	
	private void initPicSavePath(ServletContext scxt) {

		filePath = scxt.getRealPath(filePath);
		tempPath = scxt.getRealPath(tempPath);
		
		File fp = new File(filePath);
		if (!fp.exists())
			fp.mkdir();

		File tp = new File(tempPath);
		if (!tp.exists())
			tp.mkdir();

		System.out.println("文件存放目录、临时文件目录准备完毕 ...");
		System.out.println("filePah: " + filePath);
		System.out.println("tempPah: " + tempPath);
		
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println(">>>>>> SERVER IS DOWN <<<<<<");		
	}


}
