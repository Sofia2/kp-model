/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.server;

import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.URI;
import java.rmi.registry.LocateRegistry;

import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;

import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertiesManager;

public class ServerManager {

	private  Server server;
	private ConnectorServer cnt;
	private MBeanContainer mbContainer;
	static Logger log = Logger.getLogger( ServerManager.class.getName() );
	
	public ServerManager(){
		server = new Server(new InetSocketAddress("127.0.0.1", Integer.valueOf(PropertiesManager.getProperty("PORT"))));
		server.setHandler(new HandlerCollection(true)); 
		
		mbContainer=new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
		server.addEventListener(mbContainer);
		server.addBean(mbContainer);
		

		try{ 
		    
		    int rmiRegistryPort= 8090;	
			int rmiServerPort= 8091;
			try{
			    rmiRegistryPort = Integer.parseInt( PropertiesManager.getProperty("JMXPORT"));
			    rmiServerPort = Integer.parseInt(PropertiesManager.getProperty("JMXRMIPORT"));
			}catch (Exception e){
			    log.warn("Error obteniendo configuración de puertos jmx, usando por defecto: 8090 y 8091",e);
			}
			
			final String hostname = "localhost";
			
			LocateRegistry.createRegistry(rmiRegistryPort);
			
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + hostname + ":" +rmiServerPort + "/jndi/rmi://" 
								+ hostname + ":" +  rmiRegistryPort + "/jmxrmi");
			JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbContainer.getMBeanServer());
			cs.start();
		   
		    
		} catch (Exception e) {
		    log.error("Error registrando servicio JMX: ",e);
		}
	       
		
		
		
	}

	public URI getBaseUri(){
		return server.getURI();
	}
	
	public Handler getHandler(){
		return server.getHandler();
	}
	
	public void stop(){
		try{
			server.stop();
			cnt.stop();
		}catch(Exception e){
		    e.printStackTrace();
		}
	}
	
	public void start(){
		try{
			server.start();
			server.join();
			cnt.start();
		}catch(Exception e){
		    e.printStackTrace();
		}
	}
}
