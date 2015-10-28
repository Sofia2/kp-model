/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.message;

import com.indra.sofia2.ssap.ssap.SSAPMessage;

public class ErrorMessage extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8835699096763715136L;
	
	private SSAPMessage ssapRequestMessage;
	private SSAPMessage ssapResponseMessage;
	private Throwable exception;
	
	public ErrorMessage(){
	}
	
	public ErrorMessage(Throwable e){
		super(e);
		this.exception=e;
	}
	
	public SSAPMessage getSsapRequestMessage() {
		return ssapRequestMessage;
	}
	public void setSsapRequestMessage(SSAPMessage ssapRequestMessage) {
		this.ssapRequestMessage = ssapRequestMessage;
	}
	public SSAPMessage getSsapResponseMessage() {
		return ssapResponseMessage;
	}
	public void setSsapResponseMessage(SSAPMessage ssapResponseMessage) {
		this.ssapResponseMessage = ssapResponseMessage;
	}
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
}
