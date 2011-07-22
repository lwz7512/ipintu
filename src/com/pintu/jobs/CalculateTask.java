package com.pintu.jobs;

import java.util.TimerTask;

public class CalculateTask extends TimerTask {

	@Override
	public void run() {
		calculate();
	}
	
	protected void calculate(){
		//TODO, sub class to override this method to implement specific task!
		System.out.println(">>>>> calculate task executed...");
	}
	
	

}
