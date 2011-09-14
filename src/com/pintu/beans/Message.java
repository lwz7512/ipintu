package com.pintu.beans;

/**
 * personal message, like SINA Weibo private message;
 * @author lwz
 *
 */
public class Message {

	private String id;
	private String sender;
	private String receiver;
	private String content;
	private String writeTime;
	private int read;
	
	private String senderName;
	private String senderAvatar;
	private String receiverName;
	private String receiverAvatar;
	
	public Message() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(String wirteTime) {
		this.writeTime = wirteTime;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderAvatar() {
		return senderAvatar;
	}

	public void setSenderAvatar(String senderAvatar) {
		this.senderAvatar = senderAvatar;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverAvatar() {
		return receiverAvatar;
	}

	public void setReceiverAvatar(String receiverAvatar) {
		this.receiverAvatar = receiverAvatar;
	}

}
