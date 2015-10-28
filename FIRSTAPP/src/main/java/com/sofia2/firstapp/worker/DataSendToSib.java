/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.SibMessage;
import com.indra.sofia2.kpmodelo.worker.Sib.DataSendToSIBWorkerImpl;

@Component
public class DataSendToSib extends DataSendToSIBWorkerImpl {

	@Override
	public void sended(SibMessage message) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO SE HAN ENVIADO DATOS AL SIB DE FORMA CORRECTA SI EL MESAJE
		 * ENVIADO ESTA EN LA BASE DE DATOS DE ERRORES LO BORRA DE ESTA
		 */
		//TODO
	}

}
