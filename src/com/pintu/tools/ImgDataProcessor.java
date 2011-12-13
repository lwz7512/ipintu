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
		System.out.println("5 create Image File（rawImage and mobileImage to DB，thumbnailImage to cache）");
		
		ImageFileCreationTask imgTask = new ImageFileCreationTask();
		imgTask.setFile(file);
		imgTask.setPath(filePath);
		imgTask.setPicObj(picObj);
		imgTask.setCacheAccess(cacheAccess);
		pool.execute(imgTask);
		
	}

	public void shutdownProcess() {
		pool.shutdown();
	}

}

