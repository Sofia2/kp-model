/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Infraestructure;

import com.indra.sofia2.kpmodelo.message.MonitoringMessage;
import com.mycila.event.Subscriber;

public interface MonitoringWorker extends Subscriber<MonitoringMessage> {

	void monitoring(MonitoringMessage message);
	
}
