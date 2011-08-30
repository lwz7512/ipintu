package com.pintu.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.pintu.beans.Comment;
import com.pintu.beans.TPicItem;
import com.pintu.beans.User;
import com.pintu.beans.Vote;
import com.pintu.dao.DBAccessInterface;
import com.pintu.facade.PintuServiceInterface;
import com.pintu.utils.PintuUtils;


public class PintuUnitTest {

	private DBAccessInterface dbAccess;
	
	private PintuServiceInterface pintuService;
	
	@Before
	public void setUp() throws Exception {		
		
//		ApplicationContext context = new ClassPathXmlApplicationContext("app-config.xml");	
	    ApplicationContext  context = new FileSystemXmlApplicationContext("D:\\workspace\\ipintu\\WebContent\\WEB-INF\\app-config.xml");
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
		u.setId(PintuUtils.generateUID());
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
		pic.setPublishTime(PintuUtils.getFormatNowTime());
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
	public void getDBPicture(){
		String today = "2011-08-09 00:00:00";
//		List<TPicItem> list = dbAccess.getPictureForCache(today);
//			List<Story> list=dbAccess.getStoryForCache(today);
	List<Comment> list = dbAccess.getCommentForCache(today);
		if(list != null){
			for(int i = 0; i<list.size();i++){
				System.out.println(list.get(i));
			}
		}
	}
	
	@Test
	public void testInsertComment(){
		List<Object> objList = new ArrayList<Object>();
		Comment cmt1 = new Comment();
		String id = UUID.randomUUID().toString().replace("-", "").substring(16);
		cmt1.setId(id);
		cmt1.setFollow("b4f48a485bc6cece");
		cmt1.setOwner("a053beae20125b5b");
		cmt1.setPublishTime(PintuUtils.getFormatNowTime());
		cmt1.setContent("junitTestComment");
		objList.add(cmt1);
		Comment cmt2= new Comment();
		String id2 = UUID.randomUUID().toString().replace("-", "").substring(16);
		cmt2.setId(id2);
		cmt2.setFollow("b4f48a485bc6cece");
		cmt2.setOwner("a053beae20125b5b");
		cmt2.setPublishTime(PintuUtils.getFormatNowTime());
		cmt2.setContent("junitTestComment");
		objList.add(cmt2);
		int i = dbAccess.insertComment(objList);
		if(i>0){
			System.out.println("插入数据库成功"+i);
		}
		
	}
	
	@Test
	public void insertVote(){
		Vote v=new Vote();
		v.setId("1");
		v.setFollow("2");
		v.setType(Vote.HEART_TYPE);
		v.setAmount(3);
		System.out.println("测试插入投票："+dbAccess.insertVote(v));
	}
	@Test
	public void updateVote(){
		Vote v=new Vote();
		v.setId("1");
		v.setFollow("9ab9a9a19396bcfd");
		v.setType("egg");
		v.setAmount(3);
		int i = dbAccess.updateVote(v);
		System.out.println("更新投票条数为："+i);
	}
	
	@Test
	public void getVoteOfStory(){
		System.out.println("测试根据故事id得投票"+dbAccess.getVoteOfStory("9ab9a9a19396bcfd"));
	}
	
	@Test
	public void getVoteByFollowAndType(){
		System.out.println("测试根据故事id和投票type得投票"+dbAccess.getVoteByFollowAndType("9ab9a9a19396bcfd","egg").size());
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
}
