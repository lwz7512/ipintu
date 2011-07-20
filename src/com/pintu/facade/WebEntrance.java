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

	//web.xml�����õĲ�������
	private String targetBean;

	//�˴�������app-config.xml�ж���:paramAnalyzer
	private Servlet proxy;

	public WebEntrance() {
		super();
		// TODO Auto-generated constructor stub
	}

	//����ʱ�����÷���
	public void init() throws ServletException {		
		this.targetBean = getInitParameter("targetBean");
		
		//ֻ����һ�Σ����ظ�����Bean�ͳ�ʼ��Servlet
		if(proxy==null){
			getServletBean();
			//Ϊ����Servlet��ʼ������
			proxy.init(getServletConfig());			
		}
		
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		//ͨ������Servlet���о����������Ӧ�߼�����
		proxy.service(req, res);
	}

	private void getServletBean() {
		WebApplicationContext wac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext());
		this.proxy = (Servlet) wac.getBean(targetBean);
	}

}
