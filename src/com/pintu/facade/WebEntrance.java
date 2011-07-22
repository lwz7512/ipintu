package com.pintu.facade;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class ExtVisitorApi
 */
public class WebEntrance extends GenericServlet {

	private static final long serialVersionUID = 1L;

	//web.xml中配置的参数名称
	private String targetBean;

	//此代理类在app-config.xml中定义:paramAnalyzer
	private Servlet proxy;

	public WebEntrance() {
		super();
		// TODO Auto-generated constructor stub
	}

	//请求时触发该方法
	public void init() throws ServletException {		
		this.targetBean = getInitParameter("targetBean");
		
		//只查找一次，不重复查找Bean和初始化Servlet
		if(proxy==null){
			getServletBean();
			//为代理Servlet初始化配置
			proxy.init(getServletConfig());			
		}
		
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		//通过代理Servlet进行具体的请求响应逻辑处理
		proxy.service(req, res);
	}

	private void getServletBean() {
		WebApplicationContext wac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext());
		this.proxy = (Servlet) wac.getBean(targetBean);
	}

}
