package com.pintu.beans;

import java.io.Serializable;

public class Vote implements Serializable{

	//�Ƿ��Ѿ���ͬ����⣬����ͬ�������б�������ʱ�ж�
	//���Ϊ���ʾ����⣬���Ϊ�ٱ�ʾΪ���
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
