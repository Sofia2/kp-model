/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.sofia2.firstapp.worker;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.message.SensorMessage;
import com.indra.sofia2.kpmodelo.worker.Sensor.NewDataReceivedWorkerImpl;
import com.indra.sofia2.ssap.kp.SSAPMessageGenerator;
import com.indra.sofia2.ssap.ssap.SSAPMessage;

import flexjson.JSONSerializer;

@Component
public class PrepareMessageToSend extends NewDataReceivedWorkerImpl {
	static Logger log = Logger.getLogger( PrepareMessageToSend.class.getName() );
	
	private DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
	
	public PrepareMessageToSend() {
	}
	
	@Override
	public SSAPMessage generateSSAPMessage(SensorMessage sensorData) {
		/*
		 * METODO QUE ES EJECUTADO CUANDO EL CAPTADOR DE DATOS SENSORICOS LOS NOTIFICA
		 * ESTE METODO HA DE TRANFORMAR LOS DATOS CAPTADOS DE LOS SENSORES EN UN SSAP VALIDO
		 * SI ESTE METODO DEVUELVE UN OBJETO DISTINTO DE NULO LO ENVIA AUTOMATICAMENTE A LA CLASE
		 * DataToSend EL SESSION KEY NO ES NECESARIO PUES LA PLATAFORMA LO RELLENARA AUTOMATICAMENTE
		 */

		// componer json del mensaje a enviar
		JSONSerializer serializer = new JSONSerializer();
		String mensaje = serializer.serialize( sensorData.getParameters() );
		// generar mensaje SSAP y devolverlo listo para envio
		SSAPMessage ssap = new SSAPMessageGenerator().generateInsertMessage( null, "firstAppOntology", mensaje );

		return ssap;

	}
}
