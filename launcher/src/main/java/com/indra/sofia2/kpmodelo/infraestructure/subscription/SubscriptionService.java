/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.subscription;

import java.util.Map;

import com.indra.sofia2.kpmodelo.data.SubscriptionData;
import com.indra.sofia2.kpmodelo.infraestructure.exception.SubscriptionException;
import com.indra.sofia2.ssap.ssap.SSAPQueryType;

public interface SubscriptionService {

	String subscribe(String ontology, String query, SSAPQueryType queryType) throws SubscriptionException;
	
	void unsubscribe(String ontology, String idSubscription) throws SubscriptionException;
	
	void unsubscribeAll();
	
	SubscriptionData getSubscriptionData(String subscriptionId);
	
	public Map<String, SubscriptionData> getSubscriptionsData();
}
