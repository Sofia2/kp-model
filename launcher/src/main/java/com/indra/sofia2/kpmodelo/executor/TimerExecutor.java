/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.indra.sofia2.kpmodelo.infraestructure.server.WarManager;

public class TimerExecutor implements Runnable {

	private int timer;
	private boolean stop=false;
	
	private ExecutorService pool = Executors.newCachedThreadPool();
	private WarManager warManager;
	
	public TimerExecutor(int timer, WarManager warManager){
		this.timer=((timer*60)*1000);
		this.warManager=warManager;
	}
	
	public void setStop(boolean stop) {
		this.stop = stop;
	}



	public void run() { 
		ConfigExecutor configExecutor = new ConfigExecutor( warManager );
		while (!stop){
			pool.execute( configExecutor );
			try {
				Thread.sleep(timer);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
