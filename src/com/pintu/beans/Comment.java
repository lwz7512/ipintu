package com.pintu.beans;

import java.io.Serializable;

public class Comment implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	//�Ƿ��Ѿ���ͬ����⣬����ͬ�������б�������ʱ�ж�
	//���Ϊ���ʾ����⣬���Ϊ�ٱ�ʾΪ���
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
