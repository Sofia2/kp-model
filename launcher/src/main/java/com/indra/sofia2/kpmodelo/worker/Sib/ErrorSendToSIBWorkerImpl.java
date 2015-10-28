/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sib;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.events.InfraestructureEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.kpmodelo.infraestructure.persistence.Persistence;
import com.indra.sofia2.kpmodelo.infraestructure.persistence.Table;
import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.indra.sofia2.kpmodelo.message.MonitoringMessage;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Event;

public abstract class ErrorSendToSIBWorkerImpl implements ErrorSendToSIBWorker {
	
	@Autowired
	protected KPWorkerCfg kPWorkerCfg;
	
	@Autowired
	protected Persistence persistence;
	
	public void onEvent(final Event<ErrorMessage> event) throws Exception{
		persistence.getTransactionManager().callInTransaction(new Callable<Void>() {
			public Void call() throws Exception {
				SSAPMessage request = event.getSource().getSsapRequestMessage();
				request.setSessionKey( null );
				Table table = new Table(request.toJson());
				persistence.create(table);
    			return null;
            }
		});
		
		onError(event.getSource());
		MonitoringMessage monitoringMessage = toMonitoring(event.getSource());
		if (monitoringMessage!=null){
			kPWorkerCfg.publish(InfraestructureEvents.MONITORING.name(), monitoringMessage);
		}
	}
	
	public abstract void onError(ErrorMessage error);
	public abstract MonitoringMessage toMonitoring(ErrorMessage error);
	
}
