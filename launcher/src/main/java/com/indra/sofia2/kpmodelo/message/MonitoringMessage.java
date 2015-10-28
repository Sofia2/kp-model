/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.message;

import java.io.Serializable;

public class MonitoringMessage  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2020100002226597193L;
	private Throwable error;

	public Throwable getError() {
		return error;
	}

	public MonitoringMessage(Throwable error){
		this.error=error;
	}

}
