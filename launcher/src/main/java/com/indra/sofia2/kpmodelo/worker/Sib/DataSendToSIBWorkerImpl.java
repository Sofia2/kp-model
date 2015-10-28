/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sib;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.infraestructure.persistence.Persistence;
import com.indra.sofia2.kpmodelo.infraestructure.persistence.Table;
import com.indra.sofia2.kpmodelo.message.SibMessage;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Event;

public abstract class DataSendToSIBWorkerImpl implements DataSendToSIBWorker {
	
	@Autowired
	protected Persistence persistence;
	
	public void onEvent(final Event<SibMessage> event) throws Exception{
		try{
			persistence.getTransactionManager().callInTransaction(new Callable<Void>() {
				public Void call() throws Exception {
					SSAPMessage request = event.getSource().getRequest();
					request.setSessionKey( null );
                	Table table = persistence.findById( request.toJson());
                	if (table!=null){
                		persistence.delete(table);
                	}
        			return null;
                }
			});
		}catch (Exception e){
			e.printStackTrace();
		}
		sended(event.getSource());
	}
	
	public abstract void sended(SibMessage message);
}
