/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Infraestructure;

import com.indra.sofia2.kpmodelo.message.MonitoringMessage;
import com.mycila.event.Event;

public abstract class MonitoringWorkerImpl implements MonitoringWorker {

	public void onEvent(Event<MonitoringMessage> event) throws Exception{
		monitoring(event.getSource());
	}
	
	public abstract void monitoring(MonitoringMessage message);

}
