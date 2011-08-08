package com.pintu.tools;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.TPicDesc;
import com.pintu.beans.TPicItem;
import com.pintu.dao.CacheAccessInterface;

public class ImageFileCreationTask implements Runnable {
	
		private CacheAccessInterface cacAccess;
	
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
			//route值：ipintu/WebContent/WEB-INF/uploadFile/
			String route =path + File.separator ;
			File uploadFile = new File(route+picObj.getName());
			if(imgType.equals("raw")){
				// 文件写入到系统中
				try {
					fileItem.write(uploadFile);
					picObj.setRawImgId(picObj.getId()+"_Raw");
					picObj.setRawImgSize(fileItem.getSize()/1024+"");
					picObj.setRawImgPath(route+picObj.getRawImgId()+getFileType());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(imgType.equals("mobile")){
				picObj.setMobImgId(picObj.getId()+"_Mob");
				picObj.setMobImgPath(route+picObj.getMobImgId()+getFileType());
				try {
					 ImageHelper.thumbnailHandler(fileItem, 440, 300, true,imgType,picObj.getMobImgPath());
//					先生成小图片后写文件再给size赋值
					 File file= new File(picObj.getMobImgPath());
					 picObj.setMobImgSize(file.length()/1024+"");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else if(imgType.equals("thumbnail")){
				//因缩略图不用入库，所以id可以不遵循数据库中设计的长度
				String thumbnailId=picObj.getId()+"_Thumbnail";
				//函数设计传参需要，因不写文件，帮用不到
				String thumbnailPath=route+thumbnailId+getFileType();
				BufferedImage  buffer =  (BufferedImage) ImageHelper.thumbnailHandler(fileItem, 100, 100, true,imgType,thumbnailPath);
				
				//构造出缩略图对象，用于放到缓存中
				TPicDesc tpicDesc = new TPicDesc();
				//缩略图对象中存放贴图对象ID，方便对应
				tpicDesc.setTpId(picObj.getId());
				tpicDesc.setThumbnailId(thumbnailId);
				tpicDesc.setBufferedImage(buffer);
				tpicDesc.setStatus("0");
				
				System.out.println(thumbnailId);
			   
				//将缩略图放到缓存中
				cacAccess.cacheThumbnail(tpicDesc);
				
			}
			
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

		public void setCacheAccess(CacheAccessInterface cacAccess) {
			this.cacAccess = cacAccess;
		}
		
		public String getFileType( ){
			String fileType = "";
			if(fileItem != null){
				String fileName = fileItem.getName();
				int dotPos = fileName.lastIndexOf(".");
				fileType = fileName.substring(dotPos);
			}
			return fileType;
		}
		
}
