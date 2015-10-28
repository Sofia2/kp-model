/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;

import com.indra.sofia2.kpmodelo.executor.ServerExecutor;
import com.indra.sofia2.kpmodelo.executor.TimerExecutor;
import com.indra.sofia2.kpmodelo.infraestructure.loader.PropertiesManager;
import com.indra.sofia2.kpmodelo.infraestructure.loader.WorkerManager;
import com.indra.sofia2.kpmodelo.infraestructure.server.ServerManager;
import com.indra.sofia2.kpmodelo.infraestructure.server.WarManager;
import com.j256.ormlite.spring.TableCreator;

public class KpModelo {

	private ExecutorService pool;

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		KpModelo server = new KpModelo();
		server.init();
	}

	public void init(){
		System.setProperty(TableCreator.AUTO_CREATE_TABLES, Boolean.toString(true));

		ServerManager serverManager = new ServerManager();
		WarManager warManager = new WarManager(serverManager);

		new WorkerManager(warManager);

		pool = Executors.newCachedThreadPool();

		Runnable serverExecutor = new ServerExecutor(serverManager);
		pool.execute(serverExecutor);

		Runnable timerExecutor = new TimerExecutor(Integer.valueOf(PropertiesManager.getProperty("TIME_4_NEWVERSION")) ,warManager);
		pool.execute(timerExecutor);
	}
}
