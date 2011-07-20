package com.pintu.beans;


/**
 * 用户上传的品图内容对象，用来传给服务端处理
 * 
 * @author lwz
 *
 */
public class TastePic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//贴图作者
	private String user;
	//描述
	private String description;
	//标签
	private String tags;
	//是否允许品图
	private String allowStory;
	
	//原始上传图像数据，是图片文件生成的依据
	//FIXME, 这个后面要测一下以调整前后台接口
	private Byte[] rawImageData;
	

	
	public TastePic() {
		
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



	public String getTags() {
		return tags;
	}



	public void setTags(String tags) {
		this.tags = tags;
	}



	public String getAllowStory() {
		return allowStory;
	}



	public void setAllowStory(String allowStory) {
		this.allowStory = allowStory;
	}



	public Byte[] getRawImageData() {
		return rawImageData;
	}



	public void setRawImageData(Byte[] rawImageData) {
		this.rawImageData = rawImageData;
	}
	
	
		

}
