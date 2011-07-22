package com.pintu.beans;
/**
 * test git push, no origin and master by lwz7512
 */
import java.io.Serializable;

public class Comment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	//是否已经被同步入库，用于同步过程中遍历缓存时判断
	//如果为真表示已入库，如果为假表示为入库
	private Boolean saved;

	
	public Comment() {
		// TODO Auto-generated constructor stub
	}


	public Boolean getSaved() {
		return saved;
	}


	public void setSaved(Boolean saved) {
		this.saved = saved;
	}
	
	

}
