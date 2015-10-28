package com.indra.sofia2.kpmodelo.infraestructure.subscription;

import org.springframework.beans.factory.annotation.Autowired;

import com.indra.sofia2.kpmodelo.data.SubscriptionData;
import com.indra.sofia2.kpmodelo.infraestructure.exception.SubscriptionException;
import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.indra.sofia2.ssap.ssap.SSAPQueryType;
import com.mycila.event.Event;

public abstract class Subscriber implements Subscription {

	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private KPWorkerCfg kpworker;

	private SubscriptionData subscriptionData;

	protected SubscriptionData getSubscriptionData() {
		return subscriptionData;
	}
	
	protected void subscribe(String ontology, String query, SSAPQueryType queryType) throws SubscriptionException{
		String subscriptionId = subscriptionService.subscribe(ontology, query, queryType);
		kpworker.registerEvent(subscriptionId, SSAPMessage.class, this);
		subscriptionData = new SubscriptionData(subscriptionId, ontology, query, queryType);
	}
	
	protected void unsubscribe() throws SubscriptionException{
		if (subscriptionData!=null){
			subscriptionService.unsubscribe(subscriptionData.getOntology(), subscriptionData.getSubscriptionId());
			kpworker.unregisterEvent(this);
		}
	}
	
	public void onEvent(Event<SSAPMessage> event) throws Exception{
		onEvent(event.getSource());
	}

}
