package com.pintu.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.TPicItem;
import com.pintu.dao.CacheAccessInterface;

public class ImgDataProcessor {

	
	private ExecutorService pool;
	
	//只存放缩略图字节数组
	private CacheAccessInterface cacheAccess;
	
	 // 图片文件存放的绝对路径，由apiAdaptor.setImagePath()设置
	private String filePath;
	// 临时文件存放的绝对路径，由apiAdaptor.setImagePath()设置
	private String tempPath; 

	
	//线程多少由Spring配置
	public ImgDataProcessor(int threadNumber) {
		pool = Executors.newFixedThreadPool(threadNumber);
	}
		
	//Spring inject this
	public void setCacheAccess(CacheAccessInterface cacheAccess) {
		this.cacheAccess = cacheAccess;
	}

	public void setImagePath(String filePath, String tempPath) {
		this.filePath = filePath;
		this.tempPath = tempPath;
	}


	//只管往里扔数据就行了，任务队列自动会排队执行
	public void createImageFile(FileItem file, TPicItem picObj){
		
		//缩略图可以不用写文件，存到缓存中
		ImageFileCreationTask thumbnailTask = new ImageFileCreationTask();
		thumbnailTask.setFile(file);
		thumbnailTask.setImgType("thumbnail");
		thumbnailTask.setPicObj(picObj);
		pool.execute(thumbnailTask);
		
		//创建手机访问图片
		ImageFileCreationTask mobileTask = new ImageFileCreationTask();
		mobileTask.setFile(file);
		mobileTask.setImgType("mobile");
		mobileTask.setPicObj(picObj);
		pool.execute(mobileTask);

		//创建原始大图片
		ImageFileCreationTask rawTask = new ImageFileCreationTask();
		rawTask.setFile(file);
		rawTask.setImgType("raw");
		rawTask.setPicObj(picObj);
		pool.execute(rawTask);
		
		
	}
	
	public void shutdownProcess(){
		pool.shutdown();
	}

}

 class ImageFileCreationTask implements Runnable{

	 //生成缩略图thumbnail，还是手机浏览图mobile，还是原始大图raw
	 private String imgType;
	 
	 private FileItem file;
	 
	 //缓存的贴图对象，这时图片路径和大小还未知
	 //需要生成文件后后补充属性，然后才能由另外的线程入库
	 private TPicItem picObj;
	 
	@Override
	public void run() {
		// TODO 生成图片文件，并将路径存在picObj中
		
	}

	public void setImgType(String imgType) {
		this.imgType = imgType;
	}

	public void setFile(FileItem file) {
		this.file = file;
	}

	public void setPicObj(TPicItem picObj) {
		this.picObj = picObj;
	}
	
	
	
}
