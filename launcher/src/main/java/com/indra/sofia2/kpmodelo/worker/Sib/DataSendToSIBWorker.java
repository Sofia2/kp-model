/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sib;

import com.indra.sofia2.kpmodelo.message.SibMessage;
import com.mycila.event.Subscriber;

public interface DataSendToSIBWorker extends Subscriber<SibMessage> {

	void sended(SibMessage message);
	
}
