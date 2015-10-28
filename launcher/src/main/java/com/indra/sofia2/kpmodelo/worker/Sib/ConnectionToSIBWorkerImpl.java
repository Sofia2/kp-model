/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sib;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.events.SensorEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.kpmodelo.infraestructure.persistence.Persistence;
import com.indra.sofia2.kpmodelo.infraestructure.persistence.Table;
import com.indra.sofia2.kpmodelo.message.SibMessage;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Event;

public abstract class ConnectionToSIBWorkerImpl implements ConnectionToSIBWorker {
	
	@Autowired
	protected Persistence persistence;
	@Autowired
	protected KPWorkerCfg kPWorkerCfg;
	
	public void onEvent(final Event<SibMessage> event) throws Exception{
		try{
			persistence.getTransactionManager().callInTransaction(new Callable<Void>() {
				public Void call() throws Exception {
					List<Table> tables = persistence.findAll();
					for (Table table : tables){
						kPWorkerCfg.publish(SensorEvents.DATA_TO_SEND.name(), SSAPMessage.fromJsonToSSAPMessage(table.getSsapMesssage()));
						
					}
        			return null;
                }
			});
		}catch (Exception e){
			e.printStackTrace();
		}
		connected(event.getSource());
	}
	
	public abstract void connected(SibMessage connected);
	
}
