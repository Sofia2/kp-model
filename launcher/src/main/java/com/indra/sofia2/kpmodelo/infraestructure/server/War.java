/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.server;

import org.eclipse.jetty.webapp.WebAppContext;

public class War {

	private WebAppContext webapp = new WebAppContext();
	
	public WebAppContext getWebapp() {
		return webapp;
	}

	public War (String contextPath, String warFile){
		webapp.setContextPath(contextPath);
		webapp.setWar(warFile);
	}
	
}
