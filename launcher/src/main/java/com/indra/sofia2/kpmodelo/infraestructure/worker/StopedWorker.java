/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.worker;

import java.io.File;

import com.indra.sofia2.kpmodelo.executor.util.ConfigApp;
import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertiesManager;
import com.indra.sofia2.kpmodelo.infraestructure.server.WarManager;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.mycila.event.Event;
import com.mycila.event.Subscriber;

public class StopedWorker implements Subscriber<LifeCicleMessage> {

	private WarManager warManager;
	private ConfigApp configUtil = new ConfigApp();
	
	public StopedWorker(WarManager warManager){
		this.warManager=warManager;
	}
	
	public void onEvent(Event<LifeCicleMessage> event) throws Exception{
		LifeCicleMessage message = event.getSource();
		if (message.isDelete()){
			delete(message);
		}else{
			update(message);
		}
		
	}
	
	private void update(LifeCicleMessage message){
		//LA APLICACION HA NOTIFICADO QUE HA REALIZATO TODAS LAS TAREAS NECESARIAS PARA PARARSE Y POR LO TANTO PODEMOS PARARLA.
		warManager.stopWar(message.getIdApp());
		//REALIZAMOS LA COPIA DE SEGURIDAD Y DESCARGA DE LOS ELEMENTOS REQUERIDOS
		try{
			configUtil.preDeploy(message, null);
			//DESPLEGAMOS EL WAR
			warManager.depolyWar(message.getIdApp(), message.getSW_RUTA()+"/"+message.getIdApp()+".war");
		}catch (Throwable e){
			//Restauramos las copias de seguridad que hemos generado
			configUtil.restore(message);
		}
		//ARRANCAMOS EL WAR
		warManager.startWar(message.getIdApp());
	}
	
	//Arrancamos todas las aplicaciones existentes.
		private void delete(LifeCicleMessage message){
			String idApp = message.getIdApp();
			File CFG_RUTA =new File(PropertiesManager.getProperty("CONF_RUTA"));
			File SW_RUTA =new File(PropertiesManager.getProperty("SW_RUTA"));
			warManager.undeployWar(idApp);
			File sw = new File(SW_RUTA+"/"+idApp+".war");
			if (sw.exists()){
				sw.delete();
			}
			File cfg = new File( CFG_RUTA + "/CONF_" +idApp + ".properties" );
			if (cfg.exists()){
				cfg.delete();
			}
		}
}
