/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.SibMessage;
import com.indra.sofia2.kpmodelo.worker.Sib.ConnectionToSIBWorkerImpl;

@Component
public class Connection extends ConnectionToSIBWorkerImpl {

	@Override
	public void connected(SibMessage connected) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO SE HA REALIZADO UNA CONEXION CON EL SIB PREVIO A ESTE METODO
		 * LA CLASE COMPRUEBA SI EXISTEN SSAP NO ENVIADOS O CON ERRORES EN LA BASE DE DATOS LOS VUELVE 
		 * A ENVIAR Y LOS BORRA DE LA BASE DE DATOS
		 */
		//TODO
	}

}
