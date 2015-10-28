/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sensor;

import com.indra.sofia2.kpmodelo.message.SensorMessage;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Subscriber;

public interface NewDataReceivedWorker extends Subscriber<SensorMessage> {

	SSAPMessage generateSSAPMessage(SensorMessage sensorData);
	
}
