/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.infraestructure.loader;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.indra.sofia2.kpmodelo.events.InfraestructureEvents;
import com.indra.sofia2.kpmodelo.events.LifeCicleEvents;
import com.indra.sofia2.kpmodelo.events.SensorEvents;
import com.indra.sofia2.kpmodelo.events.SibEvents;
import com.indra.sofia2.kpmodelo.infraestructure.subscription.SubscriptionServiceImpl;
import com.indra.sofia2.kpmodelo.infraestructure.worker.StopWorker;
import com.indra.sofia2.kpmodelo.message.ErrorMessage;
import com.indra.sofia2.kpmodelo.message.LifeCicleMessage;
import com.indra.sofia2.kpmodelo.message.MonitoringMessage;
import com.indra.sofia2.kpmodelo.message.SensorMessage;
import com.indra.sofia2.kpmodelo.message.SibMessage;
import com.indra.sofia2.kpmodelo.worker.Infraestructure.MonitoringWorker;
import com.indra.sofia2.kpmodelo.worker.LifeCicle.StartAppWorker;
import com.indra.sofia2.kpmodelo.worker.LifeCicle.StopAppWorker;

import com.indra.sofia2.kpmodelo.worker.Sensor.DataToSendWorker;
import com.indra.sofia2.kpmodelo.worker.Sensor.NewDataReceivedWorker;
import com.indra.sofia2.kpmodelo.worker.Sensor.PrepareToReceivedWorker;
import com.indra.sofia2.kpmodelo.worker.Sib.ConnectionToSIBWorker;
import com.indra.sofia2.kpmodelo.worker.Sib.DataSendToSIBWorker;
import com.indra.sofia2.kpmodelo.worker.Sib.ErrorSendToSIBWorker;
import com.indra.sofia2.kpmodelo.worker.Sib.NoConnectionToSIBWorker;
import com.indra.sofia2.ssap.kp.Listener4SIBIndicationNotifications;
import com.indra.sofia2.ssap.ssap.SSAPMessage;
import com.mycila.event.Dispatcher;
import com.mycila.event.Dispatchers;
import com.mycila.event.ErrorHandlers;
import com.mycila.event.Subscriber;
import com.mycila.event.Topic;
import com.mycila.jmx.JmxSelfNaming;
import com.mycila.jmx.MycilaJmxExporter;

@Component
public class KPWorkerCfg implements ApplicationContextAware {
	static Logger log = Logger.getLogger( KPWorkerCfg.class.getName() );

	private ApplicationContext appContext;
	private MycilaJmxExporter exporter = new MycilaJmxExporter(ManagementFactory.getPlatformMBeanServer());
	private Dispatcher dispatcher = null;

	@Autowired
	private SubscriptionServiceImpl subscriptionService;

	public KPWorkerCfg() {
		String property = PropertiesManager.getProperty("WORKER_THREADS");
		int numThreads = 90;
		if ( property != null && property.length() > 0 ) {
			numThreads = Integer.parseInt( property );
		}
		
		log.debug( "***************************************************"  );
		log.debug( "*                                                 *"  );
		log.debug( "* KPWorkerCfg:KPWorkerCfg:asynchronousUnsafe v1.0 *"  );
		log.debug( "*                                                 *"  );
		log.debug( "***************************************************"  );

		dispatcher = Dispatchers.asynchronousUnsafe( numThreads, ErrorHandlers.ignoreErrors() );

	}

	public void publish (String topic, Object message){
		dispatcher.publish(Topic.topic(topic), message);
	}

	public void registerEvent(String event, Class message, Subscriber worker){
		dispatcher.subscribe(Topic.topic(event), message, worker);
	}

	public void unregisterEvent(Subscriber worker){
		dispatcher.unsubscribe(worker);
	}

	@PostConstruct
	private void init(){	
		//Registramos todos los beans de monitorización
		try{
			Map<String, JmxSelfNaming> beans =appContext.getBeansOfType(JmxSelfNaming.class);
			exporter.setEnsureUnique(true);
			for (JmxSelfNaming event : beans.values()){
				exporter.register(event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		//Interación SIB
		try{
			Map<String, MonitoringWorker> beans =appContext.getBeansOfType(MonitoringWorker.class);
			for (MonitoringWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(InfraestructureEvents.MONITORING.name()), MonitoringMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try{
			Map<String, ErrorSendToSIBWorker> beans =appContext.getBeansOfType(ErrorSendToSIBWorker.class);
			for (ErrorSendToSIBWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(SibEvents.ERROR_ON_SENT_TO_SIB.name()), ErrorMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try{
			Map<String, ConnectionToSIBWorker> beans =appContext.getBeansOfType(ConnectionToSIBWorker.class);
			for (ConnectionToSIBWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(SibEvents.CONNECTION_TO_SIB.name()), SibMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		//Comunicación SIB
		try{
			Map<String, NoConnectionToSIBWorker> beans =appContext.getBeansOfType(NoConnectionToSIBWorker.class);
			for (NoConnectionToSIBWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(SibEvents.NO_CONNECTION_TO_SIB.name()), ErrorMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try{
			Map<String, DataSendToSIBWorker> beans =appContext.getBeansOfType(DataSendToSIBWorker.class);
			for (DataSendToSIBWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(SibEvents.DATA_SENT_TO_SIB.name()), SibMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		//Comunicación Sensórica
		try{
			Map<String, DataToSendWorker> beans =appContext.getBeansOfType(DataToSendWorker.class);
			for (DataToSendWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(SensorEvents.DATA_TO_SEND.name()), SSAPMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try{
			Map<String, NewDataReceivedWorker> beans =appContext.getBeansOfType(NewDataReceivedWorker.class);
			for (NewDataReceivedWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(SensorEvents.NEW_DATA_RECEIVED.name()), SensorMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try{
			Map<String, PrepareToReceivedWorker> beans =appContext.getBeansOfType(PrepareToReceivedWorker.class);
			for (PrepareToReceivedWorker event : beans.values()){
				dispatcher.subscribe(Topic.topic(SensorEvents.PREPARE_TO_RECEIVED.name()), LifeCicleMessage.class, event);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		//Arrancamos o paramos la aplicación
		try{
			//Buscamos la Interface
			StopAppWorker bean =appContext.getBean(StopAppWorker.class);
			if (bean!=null){
				WorkerManager.subscribe(LifeCicleEvents.STOP_APP.name(), LifeCicleMessage.class, bean);
			}else{
				//Si no existe ninguna implementacion de StopAppWorker ejecutamos la de por defecto 
				StopWorker beanAux =appContext.getBean(StopWorker.class);
				if (beanAux!=null){
					WorkerManager.subscribe(LifeCicleEvents.STOP_APP.name(), LifeCicleMessage.class, beanAux);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try{
			StartAppWorker bean =appContext.getBean(StartAppWorker.class);
			if (bean!=null){
				dispatcher.subscribe(Topic.topic(LifeCicleEvents.START_APP.name()), LifeCicleMessage.class, bean);					
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		//Registramos todos los subscriptores
		try{
			Map<String, Listener4SIBIndicationNotifications> beans =appContext.getBeansOfType(Listener4SIBIndicationNotifications.class);
			subscriptionService.addListeners(beans);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.appContext=applicationContext;
		
	}

}
