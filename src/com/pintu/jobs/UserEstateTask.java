package com.pintu.jobs;

public class UserEstateTask extends CalculateTask {

	public UserEstateTask() {
		super();
	}

	protected void calculate(){
		//TODO, sub class to override this method to implement specific task!
		System.out.println(">>>>> calculate task executed...");
	}
	
}
