/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.executor;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.fusesource.mqtt.client.QoS;

import com.indra.sofia2.kpmodelo.events.LifeCicleEvents;
import com.indra.sofia2.kpmodelo.executor.util.ConfigApp;
import com.indra.sofia2.kpmodelo.executor.util.ConfigAsset;
import com.indra.sofia2.kpmodelo.executor.util.ConfigOnt;
import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertiesManager;
import com.indra.sofia2.kpmodelo.infraestructure.loader.WorkerManager;
import com.indra.sofia2.kpmodelo.infraestructure.server.WarManager;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.ssap.kp.Kp;
import com.indra.sofia2.ssap.kp.SSAPMessageGenerator;
import com.indra.sofia2.ssap.kp.config.MQTTConnectionConfig;
import com.indra.sofia2.ssap.kp.implementations.KpMQTTClient;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyConfigMessage;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyReturnMessage;
import com.indra.sofia2.ssap.ssap.body.config.message.SSAPBodyConfigAppsw;

public class ConfigExecutor implements Runnable {
	static Logger log = Logger.getLogger( ConfigExecutor.class.getName() );

	private boolean started = false;

	private Kp kp;
	private WarManager warManager;
	private ConfigApp configApp = new ConfigApp();
	private ConfigOnt configOnt = new ConfigOnt();
	private ConfigAsset configAsset = new ConfigAsset();

	private enum APP_ACTIONS { LABEL, REMOVE, REDEPLOY, INIT, NOACTION, DELETE }; 

	// Se ejecuta una vez en el init ConfigExecutor->TimerExecutor->KPModelo
	public ConfigExecutor(WarManager warManager){
		this.warManager=warManager;
		if (kp==null){
			MQTTConnectionConfig config = new MQTTConnectionConfig();
			config.setConnectAttemptsMax( 1 );
			
			String[] sibUrl = PropertiesManager.getProperty("SIB").split(":");

			// si lleva protocolo, p.ej ssl://host:puerto
			if (sibUrl.length > 2){
				config.setHostSIB(sibUrl[0].trim().concat(":".concat(sibUrl[1].trim())));
				config.setPortSIB(Integer.valueOf(sibUrl[2].trim()));
			}else{
				config.setHostSIB(sibUrl[0].trim());
				config.setPortSIB(Integer.valueOf(sibUrl[1].trim()));
			}

			config.setQualityOfService(QoS.AT_LEAST_ONCE);
			config.setTimeOutConnectionSIB(Integer.valueOf(PropertiesManager.getProperty("TIMEOUT").trim()));
			kp=new KpMQTTClient(config);
		}
	}
	
	public void run() { 
		log.info( "start getConfig..." );
		//Recuperamos la configuracion del SIB
		SSAPMessage join = SSAPMessageGenerator.getInstance().generateGetConfigMessage(PropertiesManager.getProperty( "KP" ), 
				PropertiesManager.getProperty( "INSTANCIA_KP" ), 
				PropertiesManager.getProperty( "TOKEN" ),
				null,
				null );
		try {	
			kp.connect();
			SSAPMessage response = kp.send( join );
			if ( response != null ) {
				log.info( "getConfig response " + response.getBody() );
				SSAPBodyReturnMessage returnMessage = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage( response.getBody() );
				if ( returnMessage.isOk() == false ) {
					log.info( "getConfig ok false" );
					throw new Exception( returnMessage.getError() );
				}
				else {					
					SSAPBodyConfigMessage configMessage = SSAPBodyConfigMessage.fromJsonToSSAPBodyConfigMessage( returnMessage.getData() );
					configOnt.configure( configMessage.getLontologia() );
					configAsset.configure( configMessage.getLasset() );
					
					//Cargamos el listado de aplicaciones desplegadas leyendo el fichero de configuracion de cada una 
					File CFG_RUTA = new File( PropertiesManager.getProperty( "CONF_RUTA" ) );
					File SW_RUTA = new File( PropertiesManager.getProperty( "SW_RUTA" ) );
					Map<String, Properties> aplDesplegadas = extractDeployed( CFG_RUTA, SW_RUTA );
					
					log.info( "getConfig evaluate Apps" );
					//Buscamos las Aplicaciones y componemos la accion
					evaluateApps( configMessage, aplDesplegadas );
 					
					log.info( "getConfig exec Apps" );
					//Buscamos las Aplicaciones y ejecutamos la accion
					execApps( configMessage, aplDesplegadas, CFG_RUTA, SW_RUTA );					
				}
			}
			log.info( "getConfig response null" );
		}
		catch( Exception e ) {
			log.info( "getConfig exception " + e.getMessage() );
			//Si se ha producido alguna excepción y las aplicaciones no se habian inciado las inicio
			if ( !started ) {
				initApps();
			}
		}
		finally {
			//El KPModelo está inicializado
			started = true;
			if ( kp.isConnected() ) {
				kp.disconnect();
			}
		}
	}

	private void evaluateApps( SSAPBodyConfigMessage configMessage, Map<String, Properties> aplDesplegadas) {

		for ( SSAPBodyConfigAppsw apl : configMessage.getLappsw() ) {
			if ( apl.getVersioncfg() != null && apl.getVersionsw() != null ) {
				Properties propertiesDesplegada = aplDesplegadas.get( apl.getIdentificacion() );
				// si la nueva app no existe, desplegarla
				if ( propertiesDesplegada == null ) {
					Properties aux = new Properties();
					aux.put( APP_ACTIONS.LABEL, APP_ACTIONS.REDEPLOY );
					aux.put( "IDAPP", apl.getIdentificacion() );
					aplDesplegadas.put( apl.getIdentificacion(), aux );
				}
				// si la nueva app tiene el mismo version de sw cfg no hacer nada
				else if ( ("" + apl.getIdentificacion()).equals( propertiesDesplegada.getProperty( "IDAPP" ) )
						&& ("" + apl.getVersioncfg()).equals( propertiesDesplegada.getProperty( "LAST_VERSION_CFG_OK" ) )
						&& ("" + apl.getVersionsw()).equals( propertiesDesplegada.getProperty( "LAST_VERSION_SW_OK" ) )
						&& started ) {
					propertiesDesplegada.put( APP_ACTIONS.LABEL, APP_ACTIONS.NOACTION );
				}
				// si la nueva app tiene el mismo version de sw cfg y estamos arrancando el servidor, inicializarla (cheaquear mejor si la app esta levantada) 
				else if ( ("" + apl.getIdentificacion()).equals( propertiesDesplegada.getProperty( "IDAPP" ) )
						&& ("" + apl.getVersioncfg()).equals( propertiesDesplegada.getProperty( "LAST_VERSION_CFG_OK" ) )
						&& ("" + apl.getVersionsw()).equals( propertiesDesplegada.getProperty( "LAST_VERSION_SW_OK" ) )
						&& !started ) {
					propertiesDesplegada.put( APP_ACTIONS.LABEL, APP_ACTIONS.INIT );
				}
				// Si la version es -1 borrarla
				else if ( ("" + apl.getIdentificacion()).equals( propertiesDesplegada.getProperty( "IDAPP" ) )
						&& ("" + apl.getVersioncfg()).equals( "-1" )
						&& ("" + apl.getVersionsw()).equals( "-1" ) ) {
					propertiesDesplegada.put( APP_ACTIONS.LABEL, APP_ACTIONS.DELETE );
				}	
				// Si ha cambiado la version del app, redesplegarla
				else if ( !("" + apl.getIdentificacion()).equals( propertiesDesplegada.getProperty( "IDAPP" ) )
						|| !("" + apl.getVersioncfg()).equals( propertiesDesplegada.getProperty( "LAST_VERSION_CFG_OK" ) )
						|| !("" + apl.getVersionsw()).equals( propertiesDesplegada.getProperty( "LAST_VERSION_SW_OK" ) ) ) {
					propertiesDesplegada.put( APP_ACTIONS.LABEL, APP_ACTIONS.REDEPLOY );
				}
			}
		}
		
		// levantar tambien las aplicaciones desplegadas que no esten configuradas en el servidor
		Iterator<Properties> it = aplDesplegadas.values().iterator();
		while ( it.hasNext() ) {
			Properties propertiesDesplegada = it.next();
			if ( !started && propertiesDesplegada.get( APP_ACTIONS.LABEL ) == null ) {
				propertiesDesplegada.put( APP_ACTIONS.LABEL, APP_ACTIONS.INIT );
			}
			else if ( started && propertiesDesplegada.get( APP_ACTIONS.LABEL ) == null ) {
				propertiesDesplegada.put( APP_ACTIONS.LABEL, APP_ACTIONS.NOACTION );
			}			
		}
	}
	
	private void execApps( SSAPBodyConfigMessage configMessage, Map<String, Properties> aplDesplegadas, File CFG_RUTA, File SW_RUTA ) {
		for ( String key : aplDesplegadas.keySet() ) {
			Properties propertiesDesplegada = aplDesplegadas.get( key );
			APP_ACTIONS action = (APP_ACTIONS)propertiesDesplegada.get( APP_ACTIONS.LABEL );
			switch ( action ) {
			case REDEPLOY:
				for ( SSAPBodyConfigAppsw apl : configMessage.getLappsw() ) {
					if ( apl.getIdentificacion().equals( propertiesDesplegada.get( "IDAPP" ) ) ) {
						if ( apl.getVersioncfg() != null && apl.getVersionsw() != null ) {
							configureApp( apl );
							break;
						}
					}
				}
				break;
			case INIT:
				File aux = new File( PropertiesManager.getProperty( "CONF_RUTA" ) + "/CONF_" + propertiesDesplegada.getProperty( "IDAPP" ) + ".properties" );				
				initApps( aux );
				break;
			case DELETE:
				removeApps( configMessage, aplDesplegadas, CFG_RUTA, SW_RUTA );
				break;					
			default:
				break;
			}
		}
	}	
	
	private void removeApps( SSAPBodyConfigMessage configMessage, Map<String, Properties> aplDesplegadas, File CFG_RUTA, File SW_RUTA ) {
		for ( Properties props : aplDesplegadas.values() ) {
			APP_ACTIONS action = (APP_ACTIONS)props.get( APP_ACTIONS.LABEL );
			if ( action == null ) {
				String idApp = props.getProperty( "IDAPP" );
				//Ahora debemos desistalar las apps
				if ( started ) {
					LifeCicleMessage message = new LifeCicleMessage();
					message.setDelete(true);
					message.setIdApp( idApp );
					WorkerManager.publish( LifeCicleEvents.STOP_APP.name(), message );
				}
				else {
					warManager.undeployWar( idApp );
					File sw = new File(SW_RUTA+"/"+idApp+".war");
					if ( sw.exists() ) {
						sw.delete();
					}
					File cfg = new File( CFG_RUTA + "/CONF_" + idApp + ".properties" );
					if ( cfg.exists() ) {
						cfg.delete();
					}
				}					
			}
		} 
	}		

	private Map<String, Properties> extractDeployed( File CFG_RUTA, File SW_RUTA ) {
		// chequear que si existe un fichero SW equivalente al CFG
		File[] ficheros = SW_RUTA.listFiles();
		// chequear que si existe un fichero SW equivalente al CFG
		Set<String> validos = new HashSet<String>();
		for ( int x = 0; x < ficheros.length; x++ ) {
			if ( ficheros[ x ].isFile() && ficheros[ x ].getName().endsWith( ".war" ) ) {
				validos.add( ficheros[ x ].getName().substring( 0, ficheros[ x ].getName().length() - ".war".length() ) );
			}
		}
		
		ficheros = CFG_RUTA.listFiles();
		Map<String, Properties > aplDesplegadas = new HashMap<String, Properties>();
		for ( int x = 0; x < ficheros.length; x++ ) {
			if ( ficheros[ x ].isFile() && ficheros[ x ].getName().endsWith( ".properties" ) && validos.contains( ficheros[ x ].getName().substring( "_CONF".length(), ficheros[ x ].getName().length() - ".properties".length() ) ) ) {
				Properties appProperties = new Properties();
				FileInputStream fileInputStream = null;
				try {
					fileInputStream = new FileInputStream( ficheros[ x ] );
					appProperties.load( fileInputStream );
					// Construimos un mapa de apliacionses desplegadas
					String idApp = (String)appProperties.get( "IDAPP" );	
					aplDesplegadas.put( idApp, appProperties );
				} 
				catch ( Exception e ) {
					e.printStackTrace();
				}
				finally {
					try {
						fileInputStream.close();
					}
					catch (Exception e) {
					}
				}
			}
			else {
				 ficheros[ x ].delete();
			}
		}

		return aplDesplegadas;
	}
	
	private String configureApp( SSAPBodyConfigAppsw configAppsw ){
		LifeCicleMessage message = new LifeCicleMessage();
		message.setTMP(PropertiesManager.getProperty("TMP_RUTA"));
		message.setSW_RUTA(PropertiesManager.getProperty("SW_RUTA"));
		message.setCFG_RUTA(PropertiesManager.getProperty("CONF_RUTA"));
		message.setIdApp(configAppsw.getIdentificacion());
		message.setVersionSW(configAppsw.getVersionsw());
		message.setVersionCFG(configAppsw.getVersioncfg());
		message.setUrl(configAppsw.getUrl());
		message.setConfigAppsw(configAppsw);
		//Cargamos el properties de la Aplicación
		Properties propCfgApl = new Properties();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream( new File( message.getCFG_RUTA()+"/CONF_"+message.getIdApp()+".properties" ) );
			propCfgApl.load( fileInputStream );
			message.setActual_sw( Integer.valueOf((String)propCfgApl.get("LAST_VERSION_SW_OK")) );
			message.setActual_cfg( Integer.valueOf((String)propCfgApl.get("LAST_VERSION_CFG_OK")) );
			//LA CONFIGURACION HA SIDO RECUPERADA 
			if ( message.getVersionCFG().longValue() != message.getActual_cfg() || message.getVersionSW().longValue() != message.getActual_sw() ) {
				if ( started ) {
					//SI ALGUNA DE LAS VERSIONES DE SW O CFG SON DISTINTAS DEBEMOS HACER EL REDESPLIEGUE NOTIFICAMOS A LAS APLICACIONES INVOLUCRADAS
					WorkerManager.publish(LifeCicleEvents.STOP_APP.name(), message);
				}
				else{
					ConfigApp configApl = new ConfigApp();
					//COMO NO ESTAMOS EN EJECUCION REALIZO EL CAMBIO DE LOS WAR SIN NOTIFICAR
					//REALIZAMOS LA COPIA DE SEGURIDAD Y DESCARGA DE LOS ELEMENTOS REQUERIDOS
					try{
						configApl.preDeploy(message, kp );
						//DESPLEGAMOS EL WAR
						warManager.depolyWar(message.getIdApp(), message.getSW_RUTA()+"/"+message.getIdApp()+".war");
					}
					catch (Throwable e){
						//Restauramos las copias de seguridad que hemos generado
						configApl.restore(message);
					}
					//ARRANCAMOS EL WAR
					warManager.startWar(message.getIdApp());
				}
			}
			else{
				//COMO NO HEMOS HECHO NADA SOLO TENEMOS QUE DESPLEGAR EL WAR EL WAR YA PREPARADO
				warManager.depolyWar(message.getIdApp(), message.getSW_RUTA()+"/"+message.getIdApp()+".war");
				warManager.startWar(message.getIdApp());
			}
		}
		catch (Exception e) {
			//NO EXISTE CONFIGURACION LOCAL ES UN NUEVO DESPLIEGUE
			message.setActual_sw(0);
			message.setActual_cfg(0);
			//REALIZAMOS LA COPIA Y DESCARGA DE LOS ELEMENTOS REQUERIDOS
			configApp.preDeploy( message, kp );
			//DESPLEGAMOS EL WAR
			warManager.depolyWar(message.getIdApp(), message.getSW_RUTA()+"/"+message.getIdApp()+".war");
			//ARRANCAMOS EL WAR
			warManager.startWar(message.getIdApp());
			
		}
		finally {
			try {
			 fileInputStream.close();
			}
			catch (Exception e) {
			}
		}
		
		return message.getIdApp();
	}	
	
	//Arrancamos todas las aplicaciones existentes.
	public void initApps() {
		File CFG_RUTA = new File( PropertiesManager.getProperty( "CONF_RUTA" ) );

		File[] ficheros = CFG_RUTA.listFiles();
		for ( int x = 0; x < ficheros.length; x++ ) {
			//Buscamos todos los ficheros de configuración
			initApps( ficheros[ x ] );
		}
	}

	public void initApps( File file ) {
		File SW_RUTA = new File( PropertiesManager.getProperty( "SW_RUTA" ) );
		
		if ( file != null && file.isFile() ) {
			Properties appProperties = new Properties();
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream( file );
				appProperties.load( fileInputStream );
				//Cargamos el WAR asociado a la aplicacion
				String IdApp = (String)appProperties.get( "IDAPP" );
				File war = new File( SW_RUTA + "/" + IdApp + ".war" );
				if ( war.exists() ) {
					warManager.depolyWar( IdApp, war.getAbsolutePath() );
				}
			} 
			catch ( Exception e ) {
				e.printStackTrace();
			}
			finally {
				try {
					fileInputStream.close();
				}
				catch ( Exception e ) {
				}
			}
		}		
	}
}
