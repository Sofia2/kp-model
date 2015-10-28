/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.MonitoringMessage;
import com.indra.sofia2.kpmodelo.worker.Infraestructure.MonitoringWorkerImpl;

@Component
public class Monitoring extends MonitoringWorkerImpl {

	@Override
	public void monitoring(MonitoringMessage arg0) {
		// TODO Auto-generated method stub

	}

}
