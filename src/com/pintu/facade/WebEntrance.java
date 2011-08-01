package com.pintu.facade;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class ExtVisitorApi
 */
public class WebEntrance extends HttpServlet {

	private static final long serialVersionUID = 1L;

	//web.xml中配置的参数名称
	private String targetBean;
	//图片文件保存路径
	private String filePath;
	//图片文件暂存路径
	private String tempPath;

	//从Spring得到的Bean
	private AppStarter proxy;

	public WebEntrance() {
		super();
		// TODO Auto-generated constructor stub
	}

	//请求时触发该方法
	public void init() throws ServletException {		
		this.targetBean = getInitParameter("targetBean");
		
		//初始化图片保存路径
		initPicSavePath(getServletConfig());
		
		//只查找一次，不重复查找Bean和初始化Servlet
		if(proxy==null){
			getServletBean();
			//为代理Servlet初始化配置
			proxy.init(getServletConfig());	
			//将路径保存到图片处理器中
			proxy.setImagePath(filePath, tempPath);
		}
		
	}
	
	private void initPicSavePath(ServletConfig config) {
		// 从配置文件中获得初始化参数
		filePath = config.getInitParameter("filepath");
		tempPath = config.getInitParameter("temppath");

		ServletContext context = getServletContext();

		filePath = context.getRealPath(filePath);
		tempPath = context.getRealPath(tempPath);
		
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
	

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		//通过代理Servlet进行具体的请求响应逻辑处理
		proxy.service(req, res);
	}

	private void getServletBean() {
		WebApplicationContext wac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext());
		this.proxy = (AppStarter) wac.getBean(targetBean);
	}

}
