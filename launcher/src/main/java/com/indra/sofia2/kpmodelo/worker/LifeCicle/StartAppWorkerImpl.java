/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.LifeCicle;

import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.events.LifeCicleEvents;
import com.indra.sofia2.kpmodelo.events.SensorEvents;
import com.indra.sofia2.kpmodelo.events.SibEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.mycila.event.Event;

public abstract class StartAppWorkerImpl implements StartAppWorker {

	@Autowired
	protected KPWorkerCfg kPWorkerCfg;
	
	public void onEvent(Event<LifeCicleMessage> event) throws Exception{
		LifeCicleMessage lifeCicleEvent = event.getSource();
		startApp(event.getSource());
		kPWorkerCfg.publish(SensorEvents.PREPARE_TO_RECEIVED.name(), lifeCicleEvent);
	}
	
	public abstract void startApp(LifeCicleMessage message);
		
}
