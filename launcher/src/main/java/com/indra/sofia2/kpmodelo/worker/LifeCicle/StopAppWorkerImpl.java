/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.LifeCicle;

import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.events.LifeCicleEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertyPlaceHolder;
import com.indra.sofia2.kpmodelo.infraestructure.loader.WorkerManager;
import com.indra.sofia2.kpmodelo.infraestructure.subscription.SubscriptionServiceImpl;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.mycila.event.Event;

public abstract class StopAppWorkerImpl implements StopAppWorker {

	@Autowired
	protected PropertyPlaceHolder property;
	@Autowired
	protected SubscriptionServiceImpl subscriptionService;
	
	public void onEvent(Event<LifeCicleMessage> event) throws Exception{
		if ( property.getProperty( "IDAPP" ) != null
				&& property.getProperty( "IDAPP" ).equals( event.getSource().getIdApp() ) ) {
			subscriptionService.unsubscribeListeners();
			stopApp( event.getSource() );
			WorkerManager.publish( LifeCicleEvents.APP_STOPED.name(), event.getSource() );
		}
	}
	
	public abstract void stopApp(LifeCicleMessage message);
	
}
