/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sensor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.events.SensorEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.kpmodelo.message.SensorMessage;
import com.mycila.event.Event;

public abstract class PrepareToReceivedWorkerImpl implements PrepareToReceivedWorker {
	static Logger log = Logger.getLogger( PrepareToReceivedWorkerImpl.class.getName() );

	@Autowired
	protected KPWorkerCfg kPWorkerCfg;
	
	public void onEvent(Event<LifeCicleMessage> event) throws Exception{
		LifeCicleMessage receivedEvent = event.getSource();
		while (true){
			SensorMessage sensorMessage = readDataSensor(receivedEvent);
			if (sensorMessage!=null){
//				log.debug( "2.1 PrepareToReceivedWorkerImpl:onEvent:publish_to_NEW_DATA_RECEIVED:init" );
				kPWorkerCfg.publish(SensorEvents.NEW_DATA_RECEIVED.name(), sensorMessage);
//				log.debug( "2.2 PrepareToReceivedWorkerImpl:onEvent:publish_to_NEW_DATA_RECEIVED:end" );
			}
		}
	}
	
	public abstract SensorMessage readDataSensor(LifeCicleMessage lifeCicleMessage);
		
}
