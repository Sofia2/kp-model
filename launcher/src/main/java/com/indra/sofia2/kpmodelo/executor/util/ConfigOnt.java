/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.executor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertiesManager;
import com.indra.sofia2.ssap.ssap.body.config.message.SSAPBodyConfigOntologia;

public class ConfigOnt {

	public void configure(List<SSAPBodyConfigOntologia> ontologias){
		if (ontologias!=null&&ontologias.size()>0){
			//Primero recupero el fichero de configuración GLOBAL que se encuentra en la ruta indicada
			//en la variable del sistema CONF_BASE
			String  PATH_MASTER = System.getProperty("CONF_BASE");
			String REAL_PATH=null;
			//Cargamos las propiedades
			Properties properties = new Properties();
			try {
				REAL_PATH = PATH_MASTER+"/"+"CONF_BASE.properties";
				properties.load(new FileInputStream(new File(REAL_PATH)));
			} catch (Exception e) {
				try {
					REAL_PATH = "/usr/"+"CONF_BASE.properties";
					properties.load(new FileInputStream(new File(REAL_PATH)));
				} catch (Exception e1) {
					e.printStackTrace();
				}
			}
			//Eliminamos todas las ontologias existentes
			Enumeration<Object> enumeratorKey = properties.keys();
			while (enumeratorKey.hasMoreElements()){
				String key = (String)enumeratorKey.nextElement();
				if (key.startsWith("ontologia")){
					properties.remove(key);
				}
			}
			//Registramos la nueva configuración
			for (SSAPBodyConfigOntologia ontologia : ontologias){
				String identificacion = ontologia.getIdentificacion();
				properties.put("ontologia."+identificacion+".identificacion", identificacion);
				properties.put("ontologia."+identificacion+".tipopermiso", ontologia.getTipopermiso());
				properties.put("ontologia."+identificacion+".esquemajson", ontologia.getEsquemajson());
			}
			FileWriter out=null;
			try {
				out = new FileWriter( REAL_PATH );
				properties.store(out, "UPDATED_ONTOLOGIA");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PropertiesManager.refreshProperties();
			}
		}
	}
	
}
