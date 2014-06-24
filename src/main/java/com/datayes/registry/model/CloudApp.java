package com.datayes.registry.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author qianzhong.fu
 *
 */
public class CloudApp implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private PermissionProperty properties;
	private List<CloudApp> versions;
	private List<PermissionProperty> features;
	
	public PermissionProperty getProperties() {
		return properties;
	}
	public void setProperties(PermissionProperty properties) {
		this.properties = properties;
	}
	public List<CloudApp> getVersions() {
		return versions;
	}
	public void setVersions(List<CloudApp> versions) {
		this.versions = versions;
	}
	public List<PermissionProperty> getFeatures() {
		return features;
	}
	public void setFeatures(List<PermissionProperty> features) {
		this.features = features;
	}
}
