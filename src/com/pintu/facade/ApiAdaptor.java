/**
 * 
 */
package com.pintu.facade;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import com.pintu.beans.TastePic;

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
		TastePic pic = new TastePic();
		Iterator<FileItem> iter = fileItems.iterator();
		while(iter.hasNext()){
			FileItem item = iter.next();
			if(item.isFormField()){
				
				if(item.getFieldName().equals("user")){
					pic.setUser(item.getString());
				}
				if(item.getFieldName().equals("description")){
					pic.setDescription(item.getString());
				}
				if(item.getFieldName().equals("tags")){
					pic.setTags(item.getString());
				}
				if(item.getFieldName().equals("allowStory")){
					pic.setAllowStory(item.getString());
				}				
				
			}else{
				//图片数据
				pic.setRawImageData(item.get());
			}
		}
		this.pintuService.createTastePic(pic, pic.getUser());
	}

}
