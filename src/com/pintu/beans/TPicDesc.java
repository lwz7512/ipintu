package com.pintu.beans;

/**
 * Ʒͼ�������ݣ�����ԭͼ������ͼ��ָ��һ��������Ʒͼ
 * ������ҳ������
 * @author lwz
 *
 */
public class TPicDesc {

	//ƷͼID
	private String tpId;
	
	//Ʒͼ����ͼ������ͼ���ͼһ�����ɣ���������ͼֻ������ڴ��У���д��������
	private String thumbnailId;
	
	//Ʒͼ״̬���·��������۶ࡢ���¶ࡢ��ͼ�����߶��ࣩ
	//���״̬�����ڶ�ʱ��������
	private String status;
	
	public TPicDesc() {
		
	}

	public String getTpId() {
		return tpId;
	}

	public void setTpId(String tpId) {
		this.tpId = tpId;
	}

	public String getThumbnailId() {
		return thumbnailId;
	}

	public void setThumbnailId(String thumbnailId) {
		this.thumbnailId = thumbnailId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
