package com.pintu.beans;


/**
 * 社区事件，或者行业事件类
 * @author lwz
 *
 */
public class Event {
	
	private String id;
	private String title;
	private String detail;
	private String eventTime;
	
	public String getEventTime() {
		return eventTime;
	}


	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
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




	public Event() {
		
	}

}
