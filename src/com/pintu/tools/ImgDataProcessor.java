package com.pintu.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pintu.beans.TPicItem;

public class ImgDataProcessor {

	
	private ExecutorService pool;
	
	//�̶߳�����Spring����
	public ImgDataProcessor(int threadNumber) {
		pool = Executors.newFixedThreadPool(threadNumber);
	}

	//ֻ�����������ݾ����ˣ���������Զ����Ŷ�ִ��
	public void createImageFile(Byte[] imgData, TPicItem picObj){
		
		//����ͼ���Բ���д�ļ����浽������
		ImageFileCreationTask thumbnailTask = new ImageFileCreationTask();
		thumbnailTask.setImgData(imgData);
		thumbnailTask.setImgType("thumbnail");
		thumbnailTask.setPicObj(picObj);
		pool.execute(thumbnailTask);
		
		//�����ֻ�����ͼƬ
		ImageFileCreationTask mobileTask = new ImageFileCreationTask();
		mobileTask.setImgData(imgData);
		mobileTask.setImgType("mobile");
		mobileTask.setPicObj(picObj);
		pool.execute(mobileTask);

		//����ԭʼ��ͼƬ
		ImageFileCreationTask rawTask = new ImageFileCreationTask();
		rawTask.setImgData(imgData);
		rawTask.setImgType("raw");
		rawTask.setPicObj(picObj);
		pool.execute(rawTask);
		
		
	}
	
	public void shutdownProcess(){
		pool.shutdown();
	}

}

 class ImageFileCreationTask implements Runnable{

	 //��������ͼthumbnail�������ֻ����ͼmobile������ԭʼ��ͼraw
	 private String imgType;
	 
	 private Byte[] imgData;
	 
	 //�������ͼ������ʱͼƬ·���ʹ�С��δ֪
	 //��Ҫ�����ļ���󲹳����ԣ�Ȼ�������������߳����
	 private TPicItem picObj;
	 
	@Override
	public void run() {
		// TODO ����ͼƬ�ļ�������·������picObj��
		
	}

	public void setImgType(String imgType) {
		this.imgType = imgType;
	}

	public void setImgData(Byte[] imgData) {
		this.imgData = imgData;
	}

	public void setPicObj(TPicItem picObj) {
		this.picObj = picObj;
	}
	
	
	
}
