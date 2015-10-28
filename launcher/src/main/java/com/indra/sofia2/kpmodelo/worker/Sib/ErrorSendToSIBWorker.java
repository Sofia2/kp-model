/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.worker.Sib;

import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.indra.sofia2.kpmodelo.message.MonitoringMessage;
import com.mycila.event.Subscriber;

public interface ErrorSendToSIBWorker extends Subscriber<ErrorMessage> {

	void onError(ErrorMessage error);
	MonitoringMessage toMonitoring(ErrorMessage error);
}
