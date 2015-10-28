/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.loader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertyPlaceHolder  extends PropertyPlaceholderConfigurer {
	
	private Properties properties;
	
	public PropertyPlaceHolder(String idApp) throws Exception{
		init(idApp);
	}
	
	private void init(String idApp) throws Exception {
		Properties proMaster = new Properties();
		//Establecemos la propiedades globales desde el KP
		Enumeration<Object> enumeratorKey = PropertiesManager.getProperties().keys();
		while (enumeratorKey.hasMoreElements()){
			String key = (String)enumeratorKey.nextElement();
			proMaster.put(key, PropertiesManager.getProperty(key));
		}
		//Establecemos el nombre de la aplicación
		proMaster.put("IDAPP", idApp);
		Properties propCfgApl = new Properties();
		try{
			propCfgApl.load(new FileInputStream(new File(PropertiesManager.getProperty("CONF_RUTA")+"/CONF_"+idApp+".properties")));
		}catch (Exception e) {
			propCfgApl.load(new FileInputStream(new File("/usr/"+"/CONF_"+idApp+".properties")));
		}
		Enumeration keys = propCfgApl.propertyNames();
		
		while (keys.hasMoreElements()){
			String key = (String)keys.nextElement();
			String value = (String)propCfgApl.get(key);
			if (value!=null&&!value.equals("")){
				//La propiedad esta definida debemos sobrescribirla
				proMaster.put(key, value);
			}else{
				//Si existe en proMaster no la podemos sobrescribir si no existe aunque sea vacia la sobrescribimos
				if (proMaster.get(key)==null){
					//No existe la sobrescribimos
					proMaster.put(key, value);
				}
			}
		}

		setProperties(proMaster);
		properties = mergeProperties();
	}
	
	public void refreshProperty(String key) throws Exception {
		init (properties.getProperty("IDAPP"));
	}
	
	public String getProperty(String key){
		return properties.getProperty(key);
	}
	
}
