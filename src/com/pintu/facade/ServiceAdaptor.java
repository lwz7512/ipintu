/**
 * 
 */
package com.pintu.facade;

/**
 * Servlet���÷���Ĳ���ת������������װ�ͻ��˲�����ʵ�ַ�����ã�
 * @author lwz
 *
 */
public class ServiceAdaptor {

	//��Springע��
	private PintuServiceInterface pintuService;
	
	public ServiceAdaptor() {
		// TODO Auto-generated constructor stub
	}

	public PintuServiceInterface getPintuService() {
		return pintuService;
	}

	public void setPintuService(PintuServiceInterface pintuService) {
		this.pintuService = pintuService;
	}
	
	

}
