/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.subscription;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.data.SubscriptionData;
import com.indra.sofia2.kpmodelo.infraestructure.SIB;
import com.indra.sofia2.kpmodelo.infraestructure.exception.SubscriptionException;
import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.indra.sofia2.ssap.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssap.kp.SSAPMessageGenerator;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.ssap.ssap.SSAPQueryType;
import com.indra.sofia2.ssap.ssap.body.SSAPBodyReturnMessage;

@Component
public class SubscriptionServiceImpl implements SubscriptionService{

	private Map<String, Listener4SIBIndicationNotifications> listeners;
	private Map<String, SubscriptionData> subscriptionsData = Collections.synchronizedMap( new HashMap<String, SubscriptionData>() );
	
	@Autowired
	private SIB sib;
	
	private static SSAPMessageGenerator messageGenerator = SSAPMessageGenerator.getInstance();
	
	public Map<String, SubscriptionData> getSubscriptionsData() {
		return subscriptionsData;
	}
	
	public SubscriptionData getSubscriptionData(String subscriptionsId) {
		return subscriptionsData.get(subscriptionsId);
	}

	public void subscribeListener(String key, Listener4SIBIndicationNotifications listener){
		if (this.listeners==null){
			this.listeners= new HashMap<String, Listener4SIBIndicationNotifications>();
		}else{
			Listener4SIBIndicationNotifications oldListener = this.listeners.get(key);
			if (oldListener!=null){
				sib.unsubscribeListener(oldListener);
				this.listeners.remove(key);
			}			
		}
		sib.subscribeListener(listener);
		this.listeners.put(key, listener);
	}
	
	public void addListeners( Map<String, Listener4SIBIndicationNotifications> listeners ) {
		this.listeners = listeners;
	}
	
	public void subscribeListeners(){
		for (Listener4SIBIndicationNotifications listener : listeners.values()){
			sib.subscribeListener(listener);
		}
	}
	
	public void removeListeners(){
		if (listeners!=null){
			Set<String> keys = listeners.keySet();
			for (String key : keys){
				Listener4SIBIndicationNotifications listener = listeners.get(key);
				sib.unsubscribeListener(listener);
			}
		}
	}
	
	public void unsubscribeListeners(){
		if (listeners!=null){
			Set<String> keys = listeners.keySet();
			for (String key : keys){
				Listener4SIBIndicationNotifications listener = listeners.get(key);
				sib.unsubscribeListener(listener);
				listeners.remove(key);
			}
		}

		Set<String> keys = subscriptionsData.keySet();
		synchronized ( subscriptionsData ) {

			Iterator<String> it = keys.iterator();
			while( it.hasNext() ) {
				String next = it.next();
				SubscriptionData subscriptionData = subscriptionsData.get( next );
				SSAPMessage msg=messageGenerator.generateUnsubscribeMessage(sib.getSessionKey(), subscriptionData.getOntology(), next );
				Future<SSAPMessage> future = sib.send(msg);
				SSAPMessage msgSubscribe = null;
				try {
					msgSubscribe = future.get();
				} 
				catch (Exception e) {
					Log.error( "unable to unsubscribe" );
				}
				SSAPBodyReturnMessage responseSubscribeBody = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(msgSubscribe.getBody());
				if (responseSubscribeBody.isOk()){
					subscriptionsData.remove( next );
				}
			}
		}
	}
	
	public String subscribe(String ontology, String query, SSAPQueryType queryType) throws SubscriptionException {
		SSAPMessage msg=messageGenerator.generateSubscribeMessage(sib.getSessionKey(), ontology, 100, query, queryType);
		Future<SSAPMessage> future = sib.send(msg);
		SSAPMessage msgSubscribe;
		try {
			msgSubscribe = future.get();
		} 
		catch (Exception e) {
			throw new SubscriptionException( e.getMessage() );
		}
		
		//Checks if subscribe message was OK in SIB
		if ( msgSubscribe == null ) 
			throw new SubscriptionException("Unable to Suscribe");
		SSAPBodyReturnMessage responseSubscribeBody = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(msgSubscribe.getBody());
		if (responseSubscribeBody.isOk()){
			SubscriptionData subscriptionData = new SubscriptionData(responseSubscribeBody.getData(),
					ontology ,query, queryType);
			subscriptionsData.put(responseSubscribeBody.getData(), subscriptionData);
		}else{
			throw new SubscriptionException(responseSubscribeBody.getError());
		}
		return responseSubscribeBody.getData();
	}
	
	public void unsubscribe(String ontology, String idSubscription) throws SubscriptionException{
		unsubscribe(ontology, idSubscription, true);
	}
	
	public void unsubscribe(String ontology, String idSubscription, boolean remove) throws SubscriptionException {
		SSAPMessage msg=messageGenerator.generateUnsubscribeMessage(sib.getSessionKey(), ontology, idSubscription);
		Future<SSAPMessage> future = sib.send(msg);
		SSAPMessage msgSubscribe = null;
		try {
			msgSubscribe = future.get();
		} 
		catch (Exception e) {
//			throw new SubscriptionException( e.getMessage() );
		}
		//Checks if subscribe message was OK in SIB
		SSAPBodyReturnMessage responseSubscribeBody = SSAPBodyReturnMessage.fromJsonToSSAPBodyReturnMessage(msgSubscribe.getBody());
		if (responseSubscribeBody.isOk() && remove){
			subscriptionsData.remove(idSubscription);
		}
		else {
			throw new SubscriptionException(responseSubscribeBody.getError());
		}		
	}

	@Override
	public void unsubscribeAll() {
		for ( SubscriptionData subscriber : subscriptionsData.values() ) {
			try {
				unsubscribe( subscriber.getOntology(), subscriber.getSubscriptionId(), false );
			} 
			catch ( Exception e) {
//				e.printStackTrace();
			} 
		}
		subscriptionsData.clear();
	}



	
}
