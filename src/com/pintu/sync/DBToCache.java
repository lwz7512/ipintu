package com.pintu.sync;


public class DBToCache {
	private Thread threader;
	
	private DailySync worker;
	
	public DBToCache() {
		
	}
	
	public void start(){
		if(threader!=null  && !threader.isAlive()) {
			threader.start();
		}
	}
	
	public void stop(){
		if(threader==null) worker.setDailyFlag(false);
	}

	public DailySync getExecutor() {
		return worker;
	}

	//由Spring注入
	public void setExecutor(DailySync worker) {
		this.worker = worker;	
		threader = new Thread(worker);
	}

}
