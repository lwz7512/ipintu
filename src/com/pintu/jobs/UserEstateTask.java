package com.pintu.jobs;
/**
 * 计算用户资产
 * @author liumingli
 *
 */
public class UserEstateTask extends CalculateTask {

	public UserEstateTask() {
		super();
	}

	protected void calculate(){
		//TODO, sub class to override this method to implement specific task!
		System.out.println(">>>>> calculate task executed...");
	}
	
}
