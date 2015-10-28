/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SensorMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4520771890474893617L;
	Map<String, Serializable> parameters = new HashMap<String, Serializable>();
	
	public void setProperty (String property, Serializable value){
		this.parameters.put(property, value);
	}
	
	public Serializable getProperty (String property){
		return this.parameters.get(property);
	}
	
	public Set<String> getPropertiesName(){
		return this.parameters.keySet();
	}
	
	public Map<String, Serializable> getParameters() {
		return parameters;
	}
}
