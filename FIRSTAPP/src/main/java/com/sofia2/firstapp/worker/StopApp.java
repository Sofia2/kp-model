/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.kpmodelo.worker.LifeCicle.StopAppWorkerImpl;

@Component
public class StopApp extends StopAppWorkerImpl {
	
	@Override
	public void stopApp(LifeCicleMessage message) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO SE SOLICITA LA DETENCION DE LA APLICACION MODELO
		 * EN ESTE METODO SE DEBERIA DE DETENER LA LECTURA SENSORICA Y REALIZAR LOS PROCESOS
		 * ADECUADOS PARA DETENER DE FORMA SEGURA LA APLICACION
		 */
		//TODO
	}

}
