package com.pintu.jobs;
/**
 * 计算用户积分
 * @author liumingli
 *
 */
public class ScoreLevelTask extends CalculateTask {

	public ScoreLevelTask() {
		super();
	}

	protected void calculate(){
		//TODO, sub class to override this method to implement specific task!
		System.out.println(">>>>> calculate task executed...");
	}
	
}
