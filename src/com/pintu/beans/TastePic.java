package com.pintu.beans;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;


/**
 * 用户上传的品图内容对象，用来传给服务端处理
 * 
 * @author lwz
 *
 */
public class TastePic implements Serializable{
	
	private static final long serialVersionUID = -3991078371255576563L;
	//贴图作者
	private String user;
	//描述
	private String description;
	//标签
	private String tags;
	//来源
	private String source;
	//是否原创
	private int isOriginal;
	
	//原始上传图像数据，是图片文件生成的依据	
	private FileItem rawImageData;
	
	
	public TastePic() {
		
	}

	public String getFileType( ){
		String fileType = "";
		if(rawImageData != null){
			String fileName = rawImageData.getName();
			int dotPos = fileName.lastIndexOf(".");
			fileType = fileName.substring(dotPos+1);
		}
		return fileType;
	}

	public String getUser() {
		return user;
	}


	

	public void setUser(String user) {
		this.user = user;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getIsOriginal() {
		return isOriginal;
	}

	public void setIsOriginal(int isOriginal) {
		this.isOriginal = isOriginal;
	}

	public FileItem getRawImageData() {
		return rawImageData;
	}



	public void setRawImageData(FileItem rawImageData) {
		this.rawImageData = rawImageData;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
		

}
