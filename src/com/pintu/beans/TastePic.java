package com.pintu.beans;


/**
 * �û��ϴ���Ʒͼ���ݶ���������������˴���
 * 
 * @author lwz
 *
 */
public class TastePic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//��ͼ����
	private String user;
	//����
	private String description;
	//��ǩ
	private String tags;
	//�Ƿ�����Ʒͼ
	private String allowStory;
	
	//ԭʼ�ϴ�ͼ�����ݣ���ͼƬ�ļ����ɵ�����
	//FIXME, �������Ҫ��һ���Ե���ǰ��̨�ӿ�
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
