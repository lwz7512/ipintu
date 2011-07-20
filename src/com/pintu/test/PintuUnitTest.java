package com.pintu.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PintuUnitTest {

	
	@Before
	public void setUp() throws Exception {		
		ApplicationContext context = new ClassPathXmlApplicationContext("app-config.xml");		
		//pintuDao = (PintuDao)context.getBean("pintuDao");
		//TODO, GET BEAN INSTANCE FROM CONTEXT
	}
	
	@Test
	public void getLatestPintus(){
	}
	

	@After
	public void tearDown() throws Exception {
	}

	
}
