/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.loader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesManager {

	private static Properties properties;

	public static Properties getProperties() {
		if (properties==null){
			loadProperties();
		}
		return properties;
	}
	
	public static String getProperty(String property) {
		if (properties==null){
			loadProperties();
		}
		return properties.getProperty(property);
	}
	
	public static void refreshProperties(){
		if (properties!=null){
			properties.clear();
			properties=null;
		}
	}
	
	private static void  loadProperties(){
		//Primero recupero el fichero de configuración GLOBAL que se encuentra en la ruta indicada
		//en la variable del sistema CONF_BASE
		String  PATH_MASTER = System.getProperty("CONF_BASE");
		//Cargamos las propiedades
		properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(PATH_MASTER+"/"+"CONF_BASE.properties")));
		} catch (Exception e) {
			try {
				properties.load(new FileInputStream(new File("/usr/"+"CONF_BASE.properties")));
			} catch (Exception e1) {
				e.printStackTrace();
			}
		}
	}
	
	
}
