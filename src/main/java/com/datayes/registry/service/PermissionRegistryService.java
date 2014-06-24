package com.datayes.registry.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.datayes.registry.model.CloudApp;
import com.datayes.registry.model.PermissionProperty;

/**
 * @author qianzhong.fu
 *
 */
public interface PermissionRegistryService {
	/*
	 * @param appList
	 * @return response
	 */
	public Response batchAddPermissions(List<CloudApp> appList);
	/*
	 * add different versions to an existd app in Greg of given tenant;if tenantDomain equals null, then use default super tenant.
	 * if app doesn't exist, return false
	 * @param versionList
	 * @param appName
	 * @return response
	 */
	public Response batchAddAppVersions(List<CloudApp> versionList,String appAlias, String tenantDomain);
	/*
	 * add features to an existed app in Greg of given tenant;if tenantDomain equals null, then use default super tenant.
	 * if app doesn't exist, return false
	 * @param featureList
	 * @param appName
	 * @return response
	 */
	public Response batchAddAppFeatures(List<PermissionProperty> featureList,String versionPath, String tenantDomain);
	/*
	 * add app permission trees to given tenant
	 * @param appList
	 * @param tenantDomain
	 * @return response
	 */
	public Response batchAddAppsToTenant(List<CloudApp> appList,String tenantDomain);
	/*
	 * @param tenantDomain
	 * @return permission tree; or empty string;
	 */
	public String getPermissionsOfTenant(String tenantDomain); 
	/*
	 * get permission tree from super tenant;
	 * @return permission tree; or empty string;
	 */
	public String getAllPermissions();
}
