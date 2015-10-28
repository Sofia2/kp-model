/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2014  SPAIN
 * 
 * All rights reserved
 ******************************************************************************/
package com.indra.sofia2.kpmodelo.message;

import java.io.Serializable;

import com.indra.sofia2.ssap.ssap.body.config.message.SSAPBodyConfigAppsw;

public class LifeCicleMessage implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5162893761150483467L;
	
	private boolean delete=false;
	
	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	private String TMP;
	private String SW_RUTA;
	private String CFG_RUTA;
	private String idApp;
	private Integer versionCFG;
	private Integer versionSW;
	private  String url;
	
	private SSAPBodyConfigAppsw configAppsw;
	
	private Integer actual_sw;
	private Integer actual_cfg;

	public SSAPBodyConfigAppsw getConfigAppsw() {
		return configAppsw;
	}

	public void setConfigAppsw(SSAPBodyConfigAppsw configAppsw) {
		this.configAppsw = configAppsw;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTMP() {
		return TMP;
	}

	public void setTMP(String tMP) {
		TMP = tMP;
	}

	public String getSW_RUTA() {
		return SW_RUTA;
	}

	public void setSW_RUTA(String sW_RUTA) {
		SW_RUTA = sW_RUTA;
	}

	public String getCFG_RUTA() {
		return CFG_RUTA;
	}

	public void setCFG_RUTA(String cFG_RUTA) {
		CFG_RUTA = cFG_RUTA;
	}

	public String getIdApp() {
		return idApp;
	}

	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}

	public Integer getVersionCFG() {
		return versionCFG;
	}

	public void setVersionCFG(Integer versionCFG) {
		this.versionCFG = versionCFG;
	}

	public Integer getVersionSW() {
		return versionSW;
	}

	public void setVersionSW(Integer versionSW) {
		this.versionSW = versionSW;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getActual_sw() {
		return actual_sw;
	}

	public void setActual_sw(Integer actual_sw) {
		this.actual_sw = actual_sw;
	}

	public Integer getActual_cfg() {
		return actual_cfg;
	}

	public void setActual_cfg(Integer actual_cfg) {
		this.actual_cfg = actual_cfg;
	}
}
