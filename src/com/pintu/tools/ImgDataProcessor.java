package com.pintu.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImgDataProcessor {

	
	private ExecutorService pool;
	
	//线程多少由Spring配置
	public ImgDataProcessor(int threadNumber) {
		pool = Executors.newFixedThreadPool(threadNumber);
	}

	//只管往里扔数据就行了，任务队列自动会排队执行
	public void createImageFile(Byte[] imgData){
		
		//缩略图可以不用写文件，存到缓存中
		ImageFileCreationTask thumbnailTask = new ImageFileCreationTask();
		thumbnailTask.setImgData(imgData);
		thumbnailTask.setImgType("thumbnail");
		pool.execute(thumbnailTask);
		
		//创建手机访问图片
		ImageFileCreationTask mobileTask = new ImageFileCreationTask();
		mobileTask.setImgData(imgData);
		mobileTask.setImgType("mobile");
		pool.execute(mobileTask);

		//创建原始大图片
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

	 //生成缩略图thumbnail，还是手机浏览图mobile，还是原始大图raw
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
