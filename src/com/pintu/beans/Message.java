package com.pintu.beans;

/**
 * personal message, like SINA Weibo private message;
 * @author lwz
 *
 */
public class Message {

	private int id;
	private int sender;
	private int receiver;
	private String content;
	private String wirteTime;
	private int read;
	
	public Message() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWirteTime() {
		return wirteTime;
	}

	public void setWirteTime(String wirteTime) {
		this.wirteTime = wirteTime;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

}
