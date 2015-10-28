/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sensor;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.infraestructure.SIB;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Event;

public abstract class DataToSendWorkerImpl implements DataToSendWorker {
	static Logger log = Logger.getLogger( DataToSendWorkerImpl.class.getName() );
	@Autowired
	protected SIB sib;
	
	public void onEvent(Event<SSAPMessage> event) throws Exception{
		SSAPMessage requestMessage = preProcessSSAPMessage(event.getSource());
		Future<SSAPMessage> future = sib.send(requestMessage);
		SSAPMessage responseMessage = future.get();
		if ( requestMessage!=null ){
			postProcessSSAPMessage(requestMessage, responseMessage);
		}
	}
	
	public abstract SSAPMessage preProcessSSAPMessage(SSAPMessage requestMessage);
	public abstract void postProcessSSAPMessage(SSAPMessage requestMessage, SSAPMessage responseMessage);
	
}
