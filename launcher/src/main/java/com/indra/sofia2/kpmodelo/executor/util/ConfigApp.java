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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.indra.sofia2.binaryrepository.client.BinaryRepository;
import com.indra.sofia2.binaryrepository.client.BinaryRepositoryImpl;
import com.indra.sofia2.kpmodelo.executor.ConfigExecutor;
import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertiesManager;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.ssap.kp.Kp;
import com.indra.sofia2.ssap.kp.SSAPMessageGenerator;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyConfigMessage;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyJoinMessage;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyReturnMessage;
import com.indra.sofia2.ssap.ssap.body.config.message.SSAPBodyConfigAppsw;

public class ConfigApp {
	static Logger log = Logger.getLogger( ConfigApp.class.getName() );
	
	public void backupWar(String SW_RUTA, String SW_RUTA_TMP){
		FileInputStream in = null;
		FileOutputStream out = null;
		try{
			//Realizamos copia de seguridad del WAR
			File inFile = new File(SW_RUTA);
			//Si existe
			if (inFile.exists()){
				File outFile = new File(SW_RUTA_TMP);
				in = new FileInputStream(inFile);
				out = new FileOutputStream(outFile);
				int c;
				while( (c = in.read() ) != -1){
					out.write(c);
				}
			}
		}catch (Exception e){
			throw new RuntimeException("Can´t do WAR backup of "+SW_RUTA, e);
		}finally{
			try {
				if (in!=null)in.close();
				if (out!=null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void downloadWar(String SW_RUTA, String SW_RUTA_TMP, String url, Kp kp ){
		InputStream in = null;
		FileOutputStream out = null;
		try{
			//Descargamos el WAR de la ruta que nos notifica el SIB
//			URL urlWar = new URL(url);
//			URLConnection urlCon = urlWar.openConnection();
//			in = urlCon.getInputStream();
			SSAPMessage msgJoin = SSAPMessageGenerator.getInstance().generateJoinByTokenMessage( PropertiesManager.getProperty( "TOKEN" ), PropertiesManager.getProperty( "KP" ) + ":" + PropertiesManager.getProperty( "INSTANCIA_KP" ) );
			log.info( "Envia mensaje JOIN al SIB: " + msgJoin.toJson() );
			SSAPMessage response = kp.send( msgJoin );
			if ( response != null ) {
				log.info( "getConfig response " + response.getBody() );
				SSAPBodyReturnMessage returnMessage = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage( response.getBody() );
				if ( returnMessage.isOk() == false ) {
					log.info( "download join false" );
					throw new Exception( returnMessage.getError() );
				}
				else {					
					//Envia el mensaje				
					log.info("Sessionkey recibida: "+ response.getSessionKey());
					
					String sessionKey = response.getSessionKey();

					
					BinaryRepository clienteRest = new BinaryRepositoryImpl( PropertiesManager.getProperty("BIN_URL") );
					in = clienteRest.getBinary(sessionKey, url);

					out = new FileOutputStream(SW_RUTA);
					byte [] array = new byte[1000];
					int leido = in.read(array);
					while (leido > 0) {
						out.write(array,0,leido);
						leido=in.read(array);
					}
				}
			}
			
		}catch (Exception e){
			throw new RuntimeException("Can´t do WAR Download of "+url, e);
		}finally{
			try {
				if (in!=null)in.close();
				if (out!=null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void backupProperties(String CFG_RUTA, String CFG_RUTA_TMP){
		FileInputStream in = null;
		FileOutputStream out = null;
		try{
			//Realizamos copia de seguridad de la CFG
			File inFile = new File(CFG_RUTA);
			if (inFile.exists()){
				File outFile = new File(CFG_RUTA_TMP);
				in = new FileInputStream(inFile);
				out = new FileOutputStream(outFile);
				int c;
				while( (c = in.read() ) != -1){
					out.write(c);
				}
			}
		}catch (Exception e){
			throw new RuntimeException("Can´t do CONFIG backup of "+CFG_RUTA, e);
		}finally{
			try {
				if (in!=null)in.close();
				if (out!=null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createProperties(SSAPBodyConfigAppsw message, String CFG_RUTA, String CFG_RUTA_TMP, SSAPBodyConfigAppsw configAppsw, String comentario){
		FileOutputStream out=null;
		try{
			//Creamos la nueva configuración en el properties
			Properties propNewCFG = new Properties();
			//Escribimos las nuevas propiedades
			propNewCFG.put("IDAPP", message.getIdentificacion());
			propNewCFG.put("LAST_VERSION_CFG_OK", Integer.toString(message.getVersioncfg()));
			propNewCFG.put("LAST_VERSION_SW_OK", Integer.toString(message.getVersionsw()));
			propNewCFG.putAll(configAppsw.getPropiedadescfg());
			out = new FileOutputStream(CFG_RUTA);
			propNewCFG.store(out, comentario);	
		}catch (Exception e){
			throw new RuntimeException("Can´t do CONFIG backup of "+CFG_RUTA, e);
		}finally{
			try {
				if (out!=null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void preDeploy(LifeCicleMessage message, Kp kp ){
		if (message.getVersionSW().longValue()!=message.getActual_sw()){
			String SW_RUTA =new String(message.getSW_RUTA()+"/"+message.getIdApp()+".war");
			String SW_RUTA_TMP =new String(message.getTMP()+"/"+message.getIdApp()+"_"+message.getActual_sw()+".war");
			backupWar( SW_RUTA, SW_RUTA_TMP );
			downloadWar( SW_RUTA, SW_RUTA_TMP, message.getUrl(), kp );
		}
		//Realizamos copia de seguridad del properties
		if ( message.getVersionSW().longValue() != message.getActual_sw() || message.getVersionCFG().longValue() != message.getActual_cfg() ) {
			String CFG_RUTA =new String(message.getCFG_RUTA()+"/"+"CONF_"+message.getIdApp()+".properties");
			String CFG_RUTA_TMP =new String(message.getTMP()+"/"+"CONF_"+message.getIdApp()+"_"+message.getActual_cfg()+".properties");
			backupProperties(CFG_RUTA, CFG_RUTA_TMP);
			createProperties(message.getConfigAppsw(), CFG_RUTA, CFG_RUTA_TMP, message.getConfigAppsw(), "Version "+message.getVersionCFG());
		}
	}
	
	public void restore(LifeCicleMessage message){
		String SW_RUTA =new String(message.getSW_RUTA()+"/"+message.getIdApp()+".war");
		String SW_RUTA_TMP =new String(message.getTMP()+"/"+message.getIdApp()+"_"+message.getActual_sw()+".war");
		String CFG_RUTA =new String(message.getCFG_RUTA()+"/"+"CONF_"+message.getIdApp()+".properties");
		String CFG_RUTA_TMP =new String(message.getTMP()+"/"+"CONF_"+message.getIdApp()+"_"+message.getActual_cfg()+".properties");
		//Recuperamos la copia de seguridad de la configuracion
		File inFileCFG = new File(CFG_RUTA_TMP);
		FileInputStream in = null;
		FileOutputStream out = null;
		try{
			if (inFileCFG.exists()){
				File outFile = new File(CFG_RUTA);
				in = new FileInputStream(inFileCFG);
				out = new FileOutputStream(outFile);
				int c;
				while( (c = in.read() ) != -1){
					out.write(c);
				}
				inFileCFG.delete();
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				if (in!=null)in.close();
				if (out!=null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Recuperamos la copia de seguridad del software
		File inFileSW = new File(SW_RUTA_TMP);
		try{
			if (inFileSW.exists()){
				File outFile = new File(SW_RUTA);
				in = new FileInputStream(inFileSW);
				out = new FileOutputStream(outFile);
				int c;
				while( (c = in.read() ) != -1){
					out.write(c);
				}
				inFileSW.delete();
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				if (in!=null)in.close();
				if (out!=null)out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
