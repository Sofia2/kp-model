/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.events.LifeCicleEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;

@Component
public class Launcher {

	@Autowired
	private KPWorkerCfg cfg;
	
	@PostConstruct
	public void init(){		
		cfg.publish(LifeCicleEvents.START_APP.name(), new LifeCicleMessage());
	}
	
}
