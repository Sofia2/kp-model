/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.infraestructure.SIB;
import com.indra.sofia2.kpmodelo.worker.Sensor.DataToSendWorkerImpl;
import com.indra.sofia2.ssap.ssap.SSAPMessage;

@Component
public class SendServiceWrapper extends DataToSendWorkerImpl {
	static Logger log = Logger.getLogger( SendServiceWrapper.class.getName() );
	@Override
	public SSAPMessage preProcessSSAPMessage(SSAPMessage requestMessage) {
		/*
		 * METODO QUE ES EJECUTADO JUSTO ANTES DE ENVIAR EL SSAP CREADO EN NewDataReceived AL SIB
		 * EL MENSAJE ENVIADO SERA EL QUE DEVUELVA ESTE METODO PARA ENVIAR EL MENSAJE GENERADO PREVIAMENTE
		 * DEVOLVER EL OBJETO DE ENTRADA requestMessage SIN MODIFICAR
		 */
		log.info( requestMessage.getBody() );
		
		return requestMessage;
	}

	@Override
	public void postProcessSSAPMessage(SSAPMessage requestMessage, SSAPMessage responseMessage) {
		/*
		 * METODO QUE ES EJECUTADO DESPUES DE ENVIAR EL SSAP AL SIB LOS PARAMETROS QUE SON EL requestMessage
		 * MENSAJE ENVIADO Y EL requestMessage MENSAJE DE RESPUESTA DEL SIB
		 */
		//TODO
	}

}
