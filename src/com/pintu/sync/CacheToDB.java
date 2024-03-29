package com.pintu.sync;


public class CacheToDB {	
	
	private Thread synchronizer;
	
	private SyncExecute executor;
	
	
	public CacheToDB() {
		
	}
	
	public void start(){
		if(synchronizer!=null  && !synchronizer.isAlive()) {
			synchronizer.start();
		}
	}
	
	public void stop(){
		if(synchronizer==null) executor.setSyncFlag(false);
	}

	public SyncExecute getExecutor() {
		return executor;
	}

	//由Spring注入
	public void setExecutor(SyncExecute executor) {
		this.executor = executor;		
		executor.setSyncFlag(true);
		synchronizer = new Thread(executor);

	}
	
	

}
