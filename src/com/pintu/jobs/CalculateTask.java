package com.pintu.jobs;

import java.util.TimerTask;

public class CalculateTask extends TimerTask {

	@Override
	public void run() {
		calculate();
	}
	
	protected void calculate(){
		//TODO, 子类可以覆盖这个方法来实现具体的任务
		System.out.println(">>>>> calculate task executed...");
	}
	
	

}
