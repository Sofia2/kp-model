/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.fusesource.mqtt.client.QoS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.events.SibEvents;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertyPlaceHolder;
import com.indra.sofia2.kpmodelo.infraestructure.subscription.SubscriptionServiceImpl;
import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.indra.sofia2.kpmodelo.message.SibMessage;
import com.indra.sofia2.ssap.kp.Kp;
import com.indra.sofia2.ssap.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssap.kp.SSAPMessageGenerator;
import com.indra.sofia2.ssap.kp.config.MQTTConnectionConfig;
import com.indra.sofia2.ssap.kp.exceptions.ConnectionToSibException;
import com.indra.sofia2.ssap.kp.implementations.KpMQTTClient;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyReturnMessage;

@Component
public class SIB {
	static Logger log = Logger.getLogger( SIB.class.getName() );

	private static final int SSAP_RESPONSE_TIMEOUT = 10000;
	private ExecutorService queue;

	@Autowired
	private SubscriptionServiceImpl subscriptionService;

	@Autowired
	private KPWorkerCfg kPWorkerCfg;
	@Autowired
	private PropertyPlaceHolder property;

	private Kp kp;
	private String sessionKey;
	public MQTTConnectionConfig config;

	public MQTTConnectionConfig getConfig() {
	    return config;
	}

	public void setConfig(MQTTConnectionConfig config) {
	    this.config = config;
	}

	private long lastConnectionErrorTime = 0;

	public Kp getKp() {
		return kp;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	@PostConstruct
	public void init(){
		log.debug( "************************Init del SIB" );
		if ( kp == null ) {
			config = new MQTTConnectionConfig();
			String[] sibUrl = property.getProperty("SIB").split(":");

			if (sibUrl.length > 2){
				config.setHostSIB(sibUrl[0].trim().concat(":".concat(sibUrl[1].trim())));
				config.setPortSIB(Integer.valueOf(sibUrl[2].trim()));
			}else{
				config.setHostSIB(sibUrl[0]);
				config.setPortSIB(Integer.valueOf(sibUrl[1]));
			}
			config.setQualityOfService(QoS.AT_LEAST_ONCE);
			config.setTimeOutConnectionSIB(Integer.valueOf(property.getProperty("TIMEOUT")));
			config.setSsapResponseTimeout( SSAP_RESPONSE_TIMEOUT );
			config.setConnectAttemptsMax( 1 );
		}
		log.debug( "************************Fin ini del SIB" );
	}

	private synchronized void mqttConnect() throws ErrorMessage {
		log.debug( "************************SIB:connect:connect..." );	

		if ( kp == null ) {
			log.debug( "************************SIB:connect:create KP" );	
			kp = new KpMQTTClient(config);
		}
		if ( !kp.isConnected() ) { 
			log.debug( "************************SIB:connect:try connecting " + Thread.currentThread().getId() + " " + Thread.currentThread().getName() );		
			try {
				subscriptionService.removeListeners();

				kp.connect();
				log.debug( "************************SIB:connect:connected! join.." );	
				subscriptionService.subscribeListeners();

				SSAPMessage messaje = new SSAPMessageGenerator().
					generateJoinByTokenMessage(property.getProperty("TOKEN"), property.getProperty("KP")+":"+property.getProperty("INSTANCIA_KP"));
				SSAPMessage response = kp.send(messaje);
				SSAPBodyReturnMessage responseBody= SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(response.getBody());
				if ( responseBody.isOk() ) {
					log.info( "************************SIB:connect:join ok" );
					sessionKey=responseBody.getData();
					// kPWorkerCfg.publish(SibEvents.SUSCRIBE_TO_SIB.name(), new SibMessage());
				}
				else {
					log.info( "************************SIB:connect:join error" );
					ErrorMessage errorMessage = new ErrorMessage();
					errorMessage.setSsapRequestMessage(messaje);
					errorMessage.setSsapResponseMessage(response);
					throw errorMessage;
				}

				log.debug( "************************SIB:connect:publish " );
				kPWorkerCfg.publish(SibEvents.CONNECTION_TO_SIB.name(), new SibMessage());
			}
			catch (ConnectionToSibException e){
//				ErrorMessage errorMessage = new ErrorMessage(e);
				log.info( "************************SIB:connect:ERROR CONNECTING! " + Thread.currentThread().getId() + " " + Thread.currentThread().getName() );	
				throw e;
			}
		}
	}

	public void subscribeListener (Listener4SIBIndicationNotifications listener){
		if ( kp != null ) {
			kp.addListener4SIBNotifications(listener);
		}
	}

	public void unsubscribeListener (Listener4SIBIndicationNotifications listener){
		if ( kp != null ) {
			kp.removeListener4SIBNotifications(listener);
		}
	}

	public Future<SSAPMessage> send( final SSAPMessage msg ) {
		if ( queue == null ) {
			queue = Executors.newSingleThreadExecutor();
		}
		Callable<SSAPMessage> task = new Callable<SSAPMessage>() {
		    public SSAPMessage call() throws Exception {
		    	// Despues de un error deja correr las tareas del executor durante config.getSsapResponseTimeout() * 2
		    	if ( (lastConnectionErrorTime + config.getSsapResponseTimeout() * 2) < System.currentTimeMillis() ) {
		    		SSAPMessage respose = mqttSend( msg );
		    		return respose;
		    	}
		    	else {
		    		log.debug( "####################### Message bypassed until reconnect " + msg.getBody() );
		    	}
		    	return null;
		    }
		};
		Future<SSAPMessage> submit = queue.submit( task );
		return submit;
	}

	public SSAPMessage mqttSend(SSAPMessage msg){
		log.info( "SIB:send" );
		SSAPMessage response=null;

		try{
			if ( !this.isConnected() ){	
				log.info( "************************SIB:send:noConnection" );
				mqttConnect();
			}

			log.info( "************************SIB:send:postConnect" );
			SSAPMessage request = msg;
			request.setSessionKey(sessionKey);
			log.info( "************************SIB:send:preKpsend" );
			response = kp.send(msg);
			log.info( "************************SIB:send:postKpsend" );
			SSAPBodyReturnMessage responseBody= SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(response.getBody());
			
			if (responseBody.isOk()){
				log.info( "************************SIB:send:kpsendOK" );
				SibMessage sibMessage = new SibMessage();
				sibMessage.setRequest(request);
				sibMessage.setResponse(response);
				
				kPWorkerCfg.publish(SibEvents.DATA_SENT_TO_SIB.name(), sibMessage);
			}
			else{
				log.debug( "************************SIB:send:kpsendNoOK" );
//				ErrorMessage errorMessage = new ErrorMessage();
//				errorMessage.setSsapRequestMessage(msg);
//				errorMessage.setSsapResponseMessage(response);
//				kPWorkerCfg.publish(SibEvents.ERROR_ON_SENT_TO_SIB.name(), errorMessage);
				
				log.info( "************************SIB_ERROR processing message" );
			}
		}
		catch (ErrorMessage ex) {
			log.info( "************************SIB:send:ERROR" );
			
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setSsapRequestMessage(msg);
			errorMessage.setSsapResponseMessage(response);
			kPWorkerCfg.publish(SibEvents.ERROR_ON_SENT_TO_SIB.name(), errorMessage);
			
			lastConnectionErrorTime = System.currentTimeMillis();
			mqttDisconnect();
			
			log.info( "************************SIB:send:ERROR:lastConnectionErrorTime" + lastConnectionErrorTime );
		}
		catch( Exception ex ) {
			log.info( "************************SIB:send:EXCEPTION " + ex.getMessage() );
			
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setSsapRequestMessage(msg);
			errorMessage.setSsapResponseMessage(response);
			kPWorkerCfg.publish(SibEvents.ERROR_ON_SENT_TO_SIB.name(), errorMessage);

			lastConnectionErrorTime = System.currentTimeMillis();
			mqttDisconnect();
			
			log.info( "************************SIB:send:EXCEPTION:lastConnectionErrorTime" + lastConnectionErrorTime );
		}
		log.info( "************************SIB:send:end" );
		return response;
	}

	private void mqttDisconnect() {
		log.info( "************************SIB:disconnecting..." );
		try {
			subscriptionService.removeListeners();
		}
		catch (Exception e) {
			log.info( "************************SIB:error on unsuscribe" );
		}
		try {
			kp.disconnect();
			log.info( "************************SIB:disconnected " + kp.isConnected() );
		} 
		catch (Exception e) {
			log.info( "************************SIB:error on disconnect" );
		}
		// Evaluar 
//		finally {
//			kp = null;
//		}
	}

	public long getLastConnectionErrorTime() {
		return lastConnectionErrorTime;
	}
	
	public boolean isConnected() {
		if ( kp != null && kp.isConnected() ){
			return true;
		}
		return false;
	}

}
