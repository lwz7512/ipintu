package com.pintu.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImgDataProcessor {

	
	private ExecutorService pool;
	
	//�̶߳�����Spring����
	public ImgDataProcessor(int threadNumber) {
		pool = Executors.newFixedThreadPool(threadNumber);
	}

	//ֻ�����������ݾ����ˣ���������Զ����Ŷ�ִ��
	public void createImageFile(Byte[] imgData){
		
		//����ͼ���Բ���д�ļ����浽������
		ImageFileCreationTask thumbnailTask = new ImageFileCreationTask();
		thumbnailTask.setImgData(imgData);
		thumbnailTask.setImgType("thumbnail");
		pool.execute(thumbnailTask);
		
		//�����ֻ�����ͼƬ
		ImageFileCreationTask mobileTask = new ImageFileCreationTask();
		mobileTask.setImgData(imgData);
		mobileTask.setImgType("mobile");
		pool.execute(mobileTask);

		//����ԭʼ��ͼƬ
		ImageFileCreationTask rawTask = new ImageFileCreationTask();
		rawTask.setImgData(imgData);
		rawTask.setImgType("raw");
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
	 
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public void setImgType(String imgType) {
		this.imgType = imgType;
	}

	public void setImgData(Byte[] imgData) {
		this.imgData = imgData;
	}
	
	
	
}
