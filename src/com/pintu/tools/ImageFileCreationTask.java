package com.pintu.tools;

import java.io.File;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.TPicItem;

public class ImageFileCreationTask implements Runnable {
	// 生成缩略图thumbnail，还是手机浏览图mobile，还是原始大图raw
		private String imgType;

		private FileItem fileItem;

		// 缓存的贴图对象，这时图片路径和大小还未知
		// 需要生成文件后后补充属性，然后才能由另外的线程入库
		private TPicItem picObj;

		private String path;

		@Override
		public void run() {
			// TODO 生成图片文件，并将路径存在picObj中
			String route =path + File.separator + picObj.getName();
			File uploadFile = new File(route);
			if(imgType.equals("raw")){
				picObj.setRawImgId(UUID.randomUUID().toString().replace("-", "").substring(16));
				picObj.setRawImgSize(fileItem.getSize()/1024+"");
				picObj.setRawImgPath(route);
				// 文件写入到系统中
				try {
					fileItem.write(uploadFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(imgType.equals("mobile")){
				picObj.setMobImgId(UUID.randomUUID().toString().replace("-", "").substring(16));
				picObj.setMobImgPath(route);
				// 文件写入到系统中
				try {
//					先生成小图片后写文件再给size赋值
//					picObj.setMobImgSize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			System.out.println(picObj.getName() + " 文件保存完毕 ...");
		}

		public void setImgType(String imgType) {
			this.imgType = imgType;
		}

		public void setFile(FileItem fileItem) {
			this.fileItem = fileItem;
		}

		public void setPicObj(TPicItem picObj) {
			this.picObj = picObj;
		}

		public void setPath(String path) {
			this.path = path;
		}

}
