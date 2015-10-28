/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.LifeCicle;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.infraestructure.exception.SubscriptionException;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.kpmodelo.infraestructure.subscription.Subscriber;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.mycila.event.Event;

@Component
public class SubscribersWorkerImpl implements SubscribersWorker, ApplicationContextAware {
	static Logger log = Logger.getLogger( SubscribersWorkerImpl.class.getName() );
	
	@Autowired
	protected KPWorkerCfg kPWorkerCfg;

	private ApplicationContext applicationContext;	

	public void onEvent(Event<LifeCicleMessage> event) throws Exception{
		
		Map<String, Subscriber> beans =applicationContext.getBeansOfType(Subscriber.class);
		
		for ( Subscriber suscriber : beans.values() ) {
			try{
				suscriber.init();
			}
			catch ( SubscriptionException e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
