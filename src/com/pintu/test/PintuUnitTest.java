package com.pintu.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.pintu.beans.Applicant;
import com.pintu.dao.DBAccessInterface;
import com.pintu.utils.PintuUtils;


public class PintuUnitTest {

	private DBAccessInterface dbAccess;
	
//	private PintuServiceInterface pintuService;
	
	@Before
	public void setUp() throws Exception {		
		
//		ApplicationContext context = new ClassPathXmlApplicationContext("app-config.xml");	
	    ApplicationContext  context = new FileSystemXmlApplicationContext("D:\\workspace\\ipintu\\WebContent\\WEB-INF\\app-config.xml");
		//pintuDao = (PintuDao)context.getBean("pintuDao")

		dbAccess = (DBAccessInterface) context.getBean("dbAcess");
//		pintuService = (PintuServiceInterface) context.getBean("pintuService");
		//TODO, GET BEAN INSTANCE FROM CONTEXT
	}
	
	@Test
	public void getLatestPintus(){
		//From lwz7512@2011/07/22
		//Hi, xiaoming:
		//TODO, this is your method has implemented, now to test here!
		
	}
	
	
	@Test
	public void volumeCreateInviteCode(){
		int i=0;
		String oldCode = "";
		for(i=0;i<10;i++){
			Applicant temp = new Applicant();
			String newCode = PintuUtils.generateInviteCode();
			if(newCode.equals(oldCode)){
				newCode = PintuUtils.generateInviteCode();
			}
			String id = PintuUtils.generateUID();
			temp.setId(id);
			temp.setInviteCode(newCode);
			temp.setApplyReason("I like it");
			temp.setPassed(1);
			temp.setAccount("");
			dbAccess.insertApplicant(temp);
			oldCode = newCode;
		}
		System.out.println("new applicant size is:"+i);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
}
