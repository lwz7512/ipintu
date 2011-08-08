package com.pintu.test;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.dao.DBAccessInterface;
import com.pintu.facade.PintuServiceInterface;


public class PintuUnitTest {

	private DBAccessInterface dbAccess;
	
	private PintuServiceInterface pintuService;
	
	@Before
	public void setUp() throws Exception {		
		ApplicationContext context = new ClassPathXmlApplicationContext("app-config.xml");	
//		ApplicationContext context = new FileSystemXmlApplicationContext("D:/workspace/ipintu/WebContent/WEB-INF/app-comfig.xml");
		//pintuDao = (PintuDao)context.getBean("pintuDao")
		dbAccess = (DBAccessInterface) context.getBean("dbAcess");
		pintuService = (PintuServiceInterface) context.getBean("pintuService");
		//TODO, GET BEAN INSTANCE FROM CONTEXT
	}
	
	@Test
	public void getLatestPintus(){
		//From lwz7512@2011/07/22
		//Hi, xiaoming:
		//TODO, this is your method has implemented, now to test here!
		
	}
	
	@Test
	public void getThumbnailData(){
	String begin = new Date().getTime()-11000000+"";
	String end = new Date().getTime()+"";
	 System.out.println("测试结果："+pintuService.getTpicsByTime(begin,end));
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
	
	
	@Test
	public void insertPicture(){
		List<Object> list = new ArrayList<Object>();
		TPicItem pic = new TPicItem();
		pic.setId("12edddddddf");
		pic.setName("1234567890abcdef.jpg");
		pic.setOwner("aa");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 java.util.Date ud = new java.util.Date();
		 Date sd = new java.sql.Date(ud.getTime());
		pic.setPublishTime(sdf.format(sd));
		pic.setTags("33333333");
		pic.setDescription("中文问题");
		pic.setAllowStory(1);
		pic.setMobImgId("1");
		pic.setMobImgSize("121");
		pic.setMobImgPath("pajt");
		pic.setRawImgId("123");
		pic.setRawImgSize("32");
		pic.setRawImgPath("rawImgPath");
		pic.setPass(1);
		list.add(pic);
		int size=dbAccess.insertPicture(list);
		System.out.println("插入数据库成功！"+size);
	}
	

	@Test
	public void getThumbnail(){
		String startTime = "2011-08-01 15:28:12";
		String endTime =  "2011-08-09 15:28:12";
		List<String> list = dbAccess.getPicIdsByTime(startTime, endTime);
		if(list != null){
			for(int i = 0; i<list.size();i++){
				System.out.println(list.get(i));
			}
		}
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
}
