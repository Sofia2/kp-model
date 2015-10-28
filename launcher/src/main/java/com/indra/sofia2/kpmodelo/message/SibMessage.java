/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.message;

import java.io.Serializable;

import com.indra.sofia2.ssap.ssap.SSAPMessage;

public class SibMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1596108213192245364L;
	private SSAPMessage request;
	private SSAPMessage response;
	
	public SSAPMessage getRequest() {
		return request;
	}
	public void setRequest(SSAPMessage request) {
		this.request = request;
	}
	public SSAPMessage getResponse() {
		return response;
	}
	public void setResponse(SSAPMessage response) {
		this.response = response;
	}
	
}
