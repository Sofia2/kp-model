package com.indra.sofia2.kpmodelo.infraestructure.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.infraestructure.loader.KPWorkerCfg;
import com.indra.sofia2.ssap.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssap.ssap.SSAPMessage;

@Component
public class SubscriptionListenerSib implements Listener4SIBIndicationNotifications {

	@Autowired
	private KPWorkerCfg kpworker;

	@Override
	public void onIndication(String idSubscription, SSAPMessage message) {
		kpworker.publish(idSubscription, message);
	}
	
}
