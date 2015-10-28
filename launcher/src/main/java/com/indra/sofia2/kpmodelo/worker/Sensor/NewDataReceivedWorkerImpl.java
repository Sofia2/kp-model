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
import com.indra.sofia2.kpmodelo.message.SensorMessage;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Event;

public abstract class NewDataReceivedWorkerImpl implements NewDataReceivedWorker {
	static Logger log = Logger.getLogger( NewDataReceivedWorkerImpl.class.getName() );
	@Autowired
	protected KPWorkerCfg kPWorkerCfg;
	
	public void onEvent(Event<SensorMessage> event) throws Exception{
//		log.debug( "3 NewDataReceivedWorkerImpl:onEvent " );
		SSAPMessage ssapMessage = generateSSAPMessage(event.getSource());
		if (ssapMessage!=null){
//			log.debug( "4 NewDataReceivedWorkerImpl:onEvent:publish_to_DATA_TO_SEND" );
			kPWorkerCfg.publish(SensorEvents.DATA_TO_SEND.name(), ssapMessage);
		}
	}
	
	public abstract SSAPMessage generateSSAPMessage(SensorMessage sensorData);
}
