/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.indra.sofia2.kpmodelo.worker.Sib.NoConnectionToSIBWorkerImpl;

@Component
public class NoConnection extends NoConnectionToSIBWorkerImpl {

	@Override
	public void noConnected(ErrorMessage error) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO NO DE PUEDE CONECTAR CON EL SIB
		 */
		//TODO
	}

}
