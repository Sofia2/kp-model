/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.loader;

import com.indra.sofia2.kpmodelo.events.LifeCicleEvents;
import com.indra.sofia2.kpmodelo.infraestructure.server.WarManager;
import com.indra.sofia2.kpmodelo.infraestructure.worker.StopedWorker;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.mycila.event.Dispatcher;
import com.mycila.event.Dispatchers;
import com.mycila.event.ErrorHandlers;
import com.mycila.event.Subscriber;
import com.mycila.event.Topic;

public class WorkerManager {
	
	private static Dispatcher dispatcher = Dispatchers.synchronousSafe(ErrorHandlers.ignoreErrors());
	
	public WorkerManager(WarManager warManager){
		dispatcher.subscribe(Topic.topic(LifeCicleEvents.APP_STOPED.name()), LifeCicleMessage.class, new StopedWorker(warManager));
	}
	
	public static void subscribe(String topic, Class message, Subscriber worker){
		dispatcher.subscribe(Topic.topic(topic), message, worker);
	}
	
	public static void publish (String topic, Object message){
		dispatcher.publish(Topic.topic(topic), message);
	}
}
