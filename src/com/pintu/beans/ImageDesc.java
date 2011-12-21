package com.pintu.beans;

import java.awt.Image;

/**
 * 放入缓存中的图片对象
 * @author lml
 *
 */
public class ImageDesc {

	private String id;
	private String type;
	private Image image;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
}
