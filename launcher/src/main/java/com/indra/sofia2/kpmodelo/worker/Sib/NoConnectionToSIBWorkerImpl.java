/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sib;

import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.mycila.event.Event;

public abstract class NoConnectionToSIBWorkerImpl implements NoConnectionToSIBWorker {
	
	public void onEvent(Event<ErrorMessage> event) throws Exception{
		noConnected(event.getSource());
	}
	
	public abstract void noConnected(ErrorMessage error);
	
}
