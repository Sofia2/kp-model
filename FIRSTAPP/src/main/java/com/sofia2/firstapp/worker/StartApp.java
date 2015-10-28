/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.kpmodelo.worker.LifeCicle.StartAppWorkerImpl;

@Component
public class StartApp extends StartAppWorkerImpl {
	
	@Override
	public void startApp(LifeCicleMessage arg0) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO SE PRODUCE EL INICIO DE LA APLICACION MODELO
		 */
		//TODO
	}

}
