/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.events.LifeCicleEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertyPlaceHolder;
import com.indra.sofia2.kpmodelo.infraestructure.loader.WorkerManager;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.mycila.event.Event;
import com.mycila.event.Subscriber;

@Component 
public class StopWorker implements Subscriber<LifeCicleMessage> {

	@Autowired
	protected PropertyPlaceHolder property;
	
	public void onEvent(Event<LifeCicleMessage> event) throws Exception{
		if (property.getProperty("IDAPP")!=null
				&&property.getProperty("IDAPP").equals(event.getSource().getIdApp())){
			WorkerManager.publish(LifeCicleEvents.APP_STOPED.name(), event.getSource());
		}
	}

}
