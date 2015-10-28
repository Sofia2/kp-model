/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.listener;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.infraestructure.exception.SubscriptionException;
import com.indra.sofia2.kpmodelo.infraestructure.subscription.Subscriber;
import com.indra.sofia2.ssap.ssap.SSAPMessage;

@Component
public class SubscriptionListener extends Subscriber {
	static Logger log = Logger.getLogger( SubscriptionListener.class.getName() );

	@Override
	@PostConstruct	
	public void init() throws SubscriptionException {
		/*
		 * METODO EN EL QUE ESTABLECEMOS QUE SUSCRIPCION ATENDERA ESTE LISTENER
		 * Se ejecuta una vez al inicio del worker
		 * imprescindible realizar la query de suscripcion. 
		 * El APP se suscribe al mismo mensaje que envia
		 */
		 // subscribe( Ontology, query, SSAPQueryType.SQLLIKE ); 
	}

	@Override
	public void onEvent(SSAPMessage arg0) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO SE NOTIFICA LA INFORMACION 
		 */
		// parsear mensaje
		// SSAPBodyMessage ssapeBodyMessage = SSAPBodyMessage.fromJsonToSSAPBodyMessage( arg0.getBody() ); 
	}

}
