package com.indra.sofia2.kpmodelo.infraestructure.subscription;

import com.indra.sofia2.kpmodelo.infraestructure.exception.SubscriptionException;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Subscriber;

public interface Subscription extends Subscriber<SSAPMessage> {

	void onEvent(SSAPMessage message);
	
	void init() throws SubscriptionException;
}
