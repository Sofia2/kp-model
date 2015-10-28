/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.kpmodelo.message.SensorMessage;
import com.indra.sofia2.kpmodelo.worker.Sensor.PrepareToReceivedWorkerImpl;

@Component
public class DataInputLoop extends PrepareToReceivedWorkerImpl {
	static Logger log = Logger.getLogger( DataInputLoop.class.getName() );
	
	// variable de ejemplo
	private int count = 0;

	@Override
	public SensorMessage readDataSensor(LifeCicleMessage lifeCicleMessage) {
		/*
		 * METODO QUE ES EJECUTADO DE FORMA CICLICA ES EL ENCARGADO DE LEER LA INFORMACIÓN SENSÓRICA HA DE DEVOLVER
		 * UN OBJETO SensorMessage CON LA INFORMACIÓN DE LOS SENSORES
		 */
		try {
			// Cada 10 segundos
			Thread.sleep( 2000 );

			// compone SensorMessage (key, value)
			SensorMessage sensor = new SensorMessage();

			// genera un mensaje de ejemplo
			sensor.setProperty( "contador", count ++ );
			sensor.setProperty( "data", "mensaje " + count + " " + UUID.randomUUID() );
			
			log.info( "Data to send " + sensor.getProperty( "data" ) );

			// devolver para enviar al SIB
			return sensor;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
