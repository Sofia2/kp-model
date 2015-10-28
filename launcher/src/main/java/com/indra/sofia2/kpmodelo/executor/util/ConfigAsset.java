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
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertiesManager;
import com.indra.sofia2.ssap.ssap.body.config.message.SSAPBodyConfigAsset;

public class ConfigAsset {

	public void configure(List<SSAPBodyConfigAsset> assets){
		if (assets!=null&&assets.size()>0){
			//Primero recupero el fichero de configuración GLOBAL que se encuentra en la ruta indicada
			//en la variable del sistema CONF_BASE
			String  PATH_MASTER = System.getProperty("CONF_BASE");
			String REAL_PATH=null;
			//Cargamos las propiedades
			Properties properties = new Properties();
			FileInputStream fileInputStream = null;
			try {
				REAL_PATH = PATH_MASTER+"/"+"CONF_BASE.properties";
				fileInputStream = new FileInputStream( new File( REAL_PATH ) );
				properties.load( fileInputStream );
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					fileInputStream.close();
				}
				catch (Exception e) {
				}
			}			
			//Eliminamos todas las ontologias existentes
			Enumeration<Object> enumeratorKey = properties.keys();
			while (enumeratorKey.hasMoreElements()){
				String key = (String)enumeratorKey.nextElement();
				if (key.startsWith("asset")){
					properties.remove(key);
				}
			}
			//Registramos la nueva configuración
			for (SSAPBodyConfigAsset asset : assets){
				String identificacion = asset.getIdentificacion();
				properties.put("asset."+identificacion+".identificacion", identificacion);
				properties.put("asset."+identificacion+".latitud", Double.toString(asset.getLatitud()));
				properties.put("asset."+identificacion+".longitud", Double.toString(asset.getLongitud()));
				Map<String, String> propiedades = asset.getPropiedades();
				if (propiedades!=null && propiedades.size()>0){
					for (String key : propiedades.keySet()){
						properties.put("asset."+identificacion+".propiedad."+key, propiedades.get(key));
					}
				}
				Map<String, String> propiedadescfg = asset.getPropiedadescfg();
				if (propiedadescfg!=null && propiedadescfg.size()>0){
					for (String key : propiedadescfg.keySet()){
						properties.put("asset."+identificacion+".propiedadcfg."+key, propiedadescfg.get(key));
					}
				}
			}
			FileOutputStream out=null;
			try {
				out = new FileOutputStream(REAL_PATH);
				properties.store(out, "UPDATED_ASSET");
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
