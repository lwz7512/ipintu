package com.pintu.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pintu.beans.User;
import com.pintu.dao.DBAccessInterface;


public class PintuUnitTest {

	private DBAccessInterface dbAccess;
	
	@Before
	public void setUp() throws Exception {		
		ApplicationContext context = new ClassPathXmlApplicationContext("app-config.xml");	
//		ApplicationContext context = new FileSystemXmlApplicationContext("D:/workspace/ipintu/WebContent/WEB-INF/app-comfig.xml");
		//pintuDao = (PintuDao)context.getBean("pintuDao")
		dbAccess = (DBAccessInterface) context.getBean("dbAcess");
		//TODO, GET BEAN INSTANCE FROM CONTEXT
	}
	
	@Test
	public void getLatestPintus(){
		//From lwz7512@2011/07/22
		//Hi, xiaoming:
		//TODO, this is your method has implemented, now to test here!
		
	}


	@Test
	public void insertOneUser(){
		User u=new User();
		u.setAccount("liumingli@163.com");
		u.setPwd("flzx3qc");
		u.setAvatar("aa");
		u.setRole("admin");
		u.setLevel(1);
		u.setScore(0);
		u.setExchangeScore(0);
		dbAccess.insertOneUser(u);
		System.out.println("插入数据库成功！");
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
}
