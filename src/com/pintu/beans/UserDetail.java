package com.pintu.beans;


public class UserDetail extends User{

	private static final long serialVersionUID = 7140021440044372397L;

	//用户资产Map 
	private int seaShell;
	
	private int copperShell;
	
	private int silverShell;
	
	private int goldShell;

	public int getSeaShell() {
		return seaShell;
	}

	public void setSeaShell(int seaShell) {
		this.seaShell = seaShell;
	}

	public int getCopperShell() {
		return copperShell;
	}

	public void setCopperShell(int copperShell) {
		this.copperShell = copperShell;
	}

	public int getSilverShell() {
		return silverShell;
	}

	public void setSilverShell(int silverShell) {
		this.silverShell = silverShell;
	}

	public int getGoldShell() {
		return goldShell;
	}

	public void setGoldShell(int goldShell) {
		this.goldShell = goldShell;
	}
	
}
