package com.pintu.beans;

import java.io.Serializable;

/**
 * Ʒͼ�������ϣ�����Ʒͼ���ߡ�ʱ�䡢�ֻ����ͼλ�á�������Ŀ��������Ŀ�ȵ�
 * ������ͼ�б�Ԫ������ʷ�����б�Ԫ��չʾ
 * @author lwz
 *
 */
public class TPicItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//��ͼID
	private String id;
	//��ͼ���ƣ������ʵ�ò��ţ����Ű�
	private String name;
	//��ͼ����
	private String owner;
	
	//����ʱ�䣬���Ա���Ϊ������
	//����ͻ���ʱ���ٸ�ʽ��Ϊxx���ӻ���xxСʱǰ
	private String publishTime;
	
	//��ͼ����
	private String description;
	//��ͼ��ǩ
	private String tags;
	//�Ƿ�����Ʒͼ
	private String allowStory;
	//���ɵ��ƶ�ͼID�����ID����pId+"_Mob"����
	private String mobImgId;
	//���ɵ��ƶ�ͼ�ļ���С����λ��KB
	private String mobImgSize;
	//���ɵ��ƶ�ͼ���̱���·��
	private String mobImgPath;
	//���ɵ�ԭʼͼID�����ID����pId+"_Raw"����
	private String rawImgId;
	//���ɵ�ԭʼͼ��С����λ��KB
	private String rwoImgSize;
	//���ɵ�ԭʼͼ���̱���·��
	private String rawImgPath;
	//�Ƿ����ͨ����Ĭ�϶���1����ͨ������ͨ����Ϊ0
	private String pass;
	
	//�Ƿ��Ѿ���ͬ����⣬����ͬ�������б�������ʱ�ж�
	//���Ϊ���ʾ����⣬���Ϊ�ٱ�ʾΪ���
	private Boolean saved;
	
	
	public TPicItem(){
		
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getPublishTime() {
		return publishTime;
	}


	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
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


	public String getMobImgId() {
		return mobImgId;
	}


	public void setMobImgId(String mobImgId) {
		this.mobImgId = mobImgId;
	}


	public String getMobImgSize() {
		return mobImgSize;
	}


	public void setMobImgSize(String mobImgSize) {
		this.mobImgSize = mobImgSize;
	}


	public String getMobImgPath() {
		return mobImgPath;
	}


	public void setMobImgPath(String mobImgPath) {
		this.mobImgPath = mobImgPath;
	}


	public String getRawImgId() {
		return rawImgId;
	}


	public void setRawImgId(String rawImgId) {
		this.rawImgId = rawImgId;
	}


	public String getRwoImgSize() {
		return rwoImgSize;
	}


	public void setRwoImgSize(String rwoImgSize) {
		this.rwoImgSize = rwoImgSize;
	}


	public String getRawImgPath() {
		return rawImgPath;
	}


	public void setRawImgPath(String rawImgPath) {
		this.rawImgPath = rawImgPath;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public Boolean getSaved() {
		return saved;
	}


	public void setSaved(Boolean saved) {
		this.saved = saved;
	}
	
	
	
}
