/**
 * 
 */
package com.pintu.facade;

/**
 * Servlet调用服务的参数转换器，用来封装客户端参数并实现服务调用；
 * @author lwz
 *
 */
public class ApiAdaptor {

	//由Spring注入
	private PintuServiceInterface pintuService;
	
	public ApiAdaptor() {
		// TODO Auto-generated constructor stub
	}

	public PintuServiceInterface getPintuService() {
		return pintuService;
	}

	public void setPintuService(PintuServiceInterface pintuService) {
		this.pintuService = pintuService;
	}
	
	

}
