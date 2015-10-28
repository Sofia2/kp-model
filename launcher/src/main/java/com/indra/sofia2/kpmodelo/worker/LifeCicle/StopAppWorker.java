/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.LifeCicle;

import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.mycila.event.Subscriber;

public interface StopAppWorker extends Subscriber<LifeCicleMessage> {

	void stopApp(LifeCicleMessage message);
	
}
