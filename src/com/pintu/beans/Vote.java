package com.pintu.beans;

import java.io.Serializable;

public class Vote implements Serializable{

	//是否已经被同步入库，用于同步过程中遍历缓存时判断
	//如果为真表示已入库，如果为假表示为入库
	private Boolean saved;

	
	public Vote() {
		// TODO Auto-generated constructor stub
	}


	public Boolean getSaved() {
		return saved;
	}


	public void setSaved(Boolean saved) {
		this.saved = saved;
	}
	
	

}
