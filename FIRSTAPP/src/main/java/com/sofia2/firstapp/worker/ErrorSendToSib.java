/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.indra.sofia2.kpmodelo.message.MonitoringMessage;
import com.indra.sofia2.kpmodelo.worker.Sib.ErrorSendToSIBWorkerImpl;

@Component
public class ErrorSendToSib extends ErrorSendToSIBWorkerImpl {

	@Override
	public MonitoringMessage toMonitoring(ErrorMessage error) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO SE HAN ENVIADO DATOS AL SIB Y SE PRODUCE UN ERROR EN EL ENVIO
		 * ESTE METODO TRANSFORMA EL MENSAJE ERRORMESSAGE error EN UN MENSAJE MonitoringMessage QUE SERA
		 * INTERCEPTADO POR LA CLASE Monitoring 
		 * 
		 * SI EL MENSAJE DEVUELTO ES DISTINTO DE NULL PUBLICA publish(InfraestructureEvents.MONITORING.name(), monitoringMessage); 
		 */
		//TODO
		return new MonitoringMessage(error);
	}

	@Override
	public void onError(ErrorMessage error) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO SE HAN ENVIADO DATOS AL SIB Y SE PRODUCE UN ERROR EN EL ENVIO
		 * ANTES DE EJECUTAR ESTE METODO DE FORMA AUTOMATICA SE ALMACENA EL SSAP ENVIADO EN LA BASE DE DATOS
		 * CONFIGURADA PARA SU POSTERIOR REENVIO
		 * 
		 *  ESTE METODO PODRIA A PARTE DE ESCRIBIR EL LOG ADECUADO ELIMINAR DE LA BASE DE DATOS EL SSAP SI SE DETECTA
		 *  QUE LO HA PROVOCADO UN ERROR SEMANTICO O SINTACTICO 
		 */
		//TODO
	}
}
