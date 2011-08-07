package com.pintu.tools;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.TPicItem;
import com.pintu.dao.CacheAccessInterface;

public class ImgDataProcessor {
	
	private ExecutorService pool;

	// 只存放缩略图字节数组
	private CacheAccessInterface cacheAccess;

	// 图片文件存放的绝对路径，由apiAdaptor.setImagePath()设置
	private String filePath;

	// 线程多少由Spring配置
	public ImgDataProcessor(int threadNumber) {
		pool = Executors.newFixedThreadPool(threadNumber);
	}

	// Spring inject this
	public void setCacheAccess(CacheAccessInterface cacheAccess) {
		this.cacheAccess = cacheAccess;
	}

	public void setImagePath(String filePath) {
		this.filePath = filePath;
	}

	// 只管往里扔数据就行了，任务队列自动会排队执行
	public void createImageFile(FileItem file, TPicItem picObj){
		System.out.println("5 生成图片文件保存（原图和手机图入库，缩略图放缓存）");
		
		
		ImageFileCreationTask thumbnailTask = new ImageFileCreationTask();
		thumbnailTask.setFile(file);
		//缩略图可以不用写文件
//		thumbnailTask.setPath(filePath);
		thumbnailTask.setImgType("thumbnail");
		thumbnailTask.setPicObj(picObj);
		//存到缓存中
		thumbnailTask.setCacheAccess(cacheAccess);
		pool.execute(thumbnailTask);
		
		//创建手机访问图片
		ImageFileCreationTask mobileTask = new ImageFileCreationTask();
		mobileTask.setFile(file);
		mobileTask.setPath(filePath);
		mobileTask.setImgType("mobile");
		mobileTask.setPicObj(picObj);
		pool.execute(mobileTask);

		//创建原始大图片
		ImageFileCreationTask rawTask = new ImageFileCreationTask();
		rawTask.setFile(file);
		rawTask.setPath(filePath);
		rawTask.setImgType("raw");
		rawTask.setPicObj(picObj);
		pool.execute(rawTask);
		
	}

	public void shutdownProcess() {
		pool.shutdown();
	}

}

