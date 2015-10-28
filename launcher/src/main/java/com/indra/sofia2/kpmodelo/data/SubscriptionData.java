/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.data;

import com.indra.sofia2.ssap.ssap.SSAPQueryType;

public class SubscriptionData {

	private String subscriptionId;
	private String ontology;
	private SSAPQueryType queryType;
	private String query;
	
	public SubscriptionData(String subscriptionId, String ontology, String query, SSAPQueryType queryType){
		this.subscriptionId=subscriptionId;
		this.ontology=ontology;
		this.query=query;
		this.queryType=queryType;
	}
	
	
	public String getSubscriptionId() {
		return subscriptionId;
	}
	
	public String getOntology() {
		return ontology;
	}
	
	public String getQuery() {
		return query;
	}

	protected SSAPQueryType getQueryType() {
		return queryType;
	}
	
}
