/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.executor;

import com.indra.sofia2.kpmodelo.infraestructure.server.ServerManager;

public class ServerExecutor implements Runnable {
	
	private ServerManager serverManager;
	
	public ServerExecutor(ServerManager serverManager){
		this.serverManager=serverManager;
	}
	
	public void run() { // run the service
		serverManager.start();
	}
}
