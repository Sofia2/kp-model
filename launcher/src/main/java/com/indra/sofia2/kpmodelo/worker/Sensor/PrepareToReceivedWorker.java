/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sensor;

import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.kpmodelo.message.SensorMessage;
import com.mycila.event.Subscriber;

public interface PrepareToReceivedWorker extends Subscriber<LifeCicleMessage> {

	SensorMessage readDataSensor(LifeCicleMessage lifeCicleMessage);
	
}
