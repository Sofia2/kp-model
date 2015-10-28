/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sensor;

import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Subscriber;

public interface DataToSendWorker extends Subscriber<SSAPMessage> {

	SSAPMessage preProcessSSAPMessage(SSAPMessage requestMessage);
	void postProcessSSAPMessage(SSAPMessage requestMessage, SSAPMessage responseMessage);
}
