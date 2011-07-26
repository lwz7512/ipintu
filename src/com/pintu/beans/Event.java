package com.pintu.beans;

import java.util.Date;

/**
 * 社区事件，或者行业事件类
 * @author lwz
 *
 */
public class Event {
	
	private int id;
	private String title;
	private String detail;
	private Date eventTime;
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDetail() {
		return detail;
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}


	public Date getEventTime() {
		return eventTime;
	}


	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}


	public Event() {
		
	}

}
