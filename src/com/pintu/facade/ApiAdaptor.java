/**
 * 
 */
package com.pintu.facade;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.TastePic;
import com.pintu.utils.UTF8Formater;

/**
 * Servlet调用服务的参数转换器，用来封装客户端参数并实现服务调用；
 * @author lwz
 *
 */
public class ApiAdaptor {

	//由Spring注入
	private PintuServiceInterface pintuService;
	
	public ApiAdaptor() {
		// TODO Auto-generated constructor stub
	}

	public PintuServiceInterface getPintuService() {
		return pintuService;
	}

	public void setPintuService(PintuServiceInterface pintuService) {
		this.pintuService = pintuService;
	}
	
	public void setImagePath(String filePath, String tempPath) {
		this.pintuService.saveImagePathToProcessor(filePath,tempPath);
	}
	
	public void createTastePic(List<FileItem> fileItems) {		
		System.out.println("2 分析图片对象apiadaptor createTastePic");
		TastePic pic = new TastePic();
		Iterator<FileItem> iter = fileItems.iterator();
		while(iter.hasNext()){
			FileItem item = iter.next();
			if(item.isFormField()){
				
				if(item.getFieldName().equals("user")){
					pic.setUser(item.getString());
				}
				if(item.getFieldName().equals("description")){
					pic.setDescription(UTF8Formater.changeToWord(item.getString()));
					System.out.println("description:"+pic.getDescription());
				}
				if(item.getFieldName().equals("tags")){
					pic.setTags(UTF8Formater.changeToWord(item.getString()));
					System.out.println("tags:"+pic.getTags());
				}
				if(item.getFieldName().equals("allowStory")){
					pic.setAllowStory(item.getString());
				}				
				
			}else{
				//图片数据
				pic.setRawImageData(item);
			}
		}
		this.pintuService.createTastePic(pic, pic.getUser());
	}

}
