/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.server;

import java.util.HashMap;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;

public class WarManager {

	private static HashMap<String, War> wars = new HashMap<String, War>();
	
	private ServerManager serverManager;
	
	public WarManager(ServerManager serverManager){
		this.serverManager=serverManager;
	}

	public void stopWar(String appId){
		War war = wars.get(appId);
		if (war!=null){
			Handler[] array = ((HandlerCollection) serverManager.getHandler()).getHandlers();
			for (Handler handler : array){
				if (handler.equals(war.getWebapp())){
					if (handler.isRunning()){
						try {
							handler.stop();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void startWar(String appId){
		War war = wars.get(appId);
		if (war!=null){
			Handler[] array = ((HandlerCollection) serverManager.getHandler()).getHandlers();
			for (Handler handler : array){
				if (handler.equals(war.getWebapp())){
					if (!handler.isRunning()){
						try {
							handler.start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public void undeployWar(String appId){
		War war = wars.get(appId);
		if (war!=null){
			try {
				HandlerCollection collection = ((HandlerCollection) serverManager.getHandler());
				Handler[] array = collection.getHandlers();
				for (Handler handler : array){
					if (handler.equals(war.getWebapp())){
						if (handler.isRunning()){
							try {
								handler.stop();
								handler.destroy();
								collection.removeHandler(handler);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				wars.remove(appId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void depolyWar(String appId, String warUrl){
		try {
			War war = wars.get(appId);
			if (war!=null){
				undeployWar(appId);
			}
			war=new War("/"+appId, warUrl);
			war.getWebapp().setExtractWAR(true);
			war.getWebapp().setAttribute("APPMODELONAME", appId);
			
			((HandlerCollection) serverManager.getHandler()).addHandler(war.getWebapp());
			
			//Register JSP Support
			ServletHolder holderJsp = new ServletHolder("jsp",JspServlet.class);
			holderJsp.setInitOrder(0);
			ServletHolder holderDefault = new ServletHolder("default",DefaultServlet.class);
			holderDefault.setInitParameter("resourceBase", serverManager.getBaseUri().toASCIIString());
			holderDefault.setInitParameter("dirAllowed","true");
			war.getWebapp().addServlet(holderDefault,"/"+appId);	
			
			wars.put(appId, war);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
