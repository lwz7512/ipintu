package com.pintu.jobs;

import java.util.TimerTask;

public class CalculateTask extends TimerTask {

	@Override
	public void run() {
		calculate();
	}
	
	protected void calculate(){
		//TODO, ������Ը������������ʵ�־��������
		System.out.println(">>>>> calculate task executed...");
	}
	
	

}
