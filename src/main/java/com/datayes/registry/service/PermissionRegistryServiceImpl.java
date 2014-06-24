package com.datayes.registry.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import com.datayes.registry.dao.PermissionRegistryDao;
import com.datayes.registry.model.CloudApp;
import com.datayes.registry.model.PermissionProperty;
import com.datayes.registry.util.JsonUtil;
import com.datayes.registry.util.RegistryConstant;

/**
 * @author qianzhong.fu
 *
 */
public class PermissionRegistryServiceImpl implements PermissionRegistryService{
	private Logger log = LoggerFactory.getLogger(PermissionRegistryService.class);
	private PermissionRegistryDao registryDao;
	public PermissionRegistryDao getRegistryDao() {
		return registryDao;
	}
	public void setRegistryDao(PermissionRegistryDao registryDao) {
		registryDao.addAppPermission(RegistryConstant.PERMISSION_PATH, RegistryConstant.PERMISSION_ROOT_PATH);
		this.registryDao = registryDao;
	}
	
	public Response batchAddPermissions(List<CloudApp> appList) {
		return batchAddAppsToTenant(appList, null);
	}
	public Response batchAddAppsToTenant(List<CloudApp> appList, String tenantDomain) {
		if(appList == null || appList.size() <= 0)
			return Response.status(Response.Status.BAD_REQUEST).entity(RegistryConstant.INVALID_INPUT).build();
		for(CloudApp app : appList){
			if(app == null)
				continue;
			PermissionProperty property = app.getProperties();
			if(property != null){
				Response response = addResource(tenantDomain, RegistryConstant.PERMISSION_ROOT_PATH, property);
				int statusCode = response.getStatus();
				if(statusCode == Response.Status.BAD_REQUEST.getStatusCode())
					continue;
				else if(statusCode == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
					return response;
				else{
					List<CloudApp> versions = app.getVersions();
					String appAlias = eliminateLeftSlash(property.getAlias()) + "/";
					response = batchAddAppVersions(versions, appAlias, tenantDomain);
					if(response.getStatus() != Response.Status.OK.getStatusCode())
						return response;
				}
			}
		}
		return Response.ok().entity(RegistryConstant.SUCCESS).build();
	}
	public Response batchAddAppVersions(List<CloudApp> versionList, String appAlias, String tenantDomain) {
		if(versionList == null || versionList.size() <= 0 || appAlias == null)
			return Response.status(Response.Status.BAD_REQUEST).entity(RegistryConstant.INVALID_INPUT).build();
		if(!registryDao.isResourceExists(RegistryConstant.PERMISSION_ROOT_PATH + appAlias))
			return Response.status(Response.Status.NOT_FOUND).entity(RegistryConstant.RESOURCE_NOT_FOUND).build();
		for(CloudApp version : versionList){
			if(version == null)
				continue;
			PermissionProperty property = version.getProperties();
			if(property != null){
				String prePath = RegistryConstant.PERMISSION_ROOT_PATH + appAlias + "/";
				Response response = addResource(tenantDomain, prePath, property);
				int statusCode = response.getStatus();
				if(statusCode == Response.Status.BAD_REQUEST.getStatusCode())
					continue;
				else if(statusCode == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
					return response;
				else{
					List<PermissionProperty> features = version.getFeatures();
					prePath = prePath + eliminateLeftSlash(property.getAlias()) + "/";
					response = batchAddAppFeatures(features, prePath,tenantDomain);
					if(response.getStatus() != Response.Status.OK.getStatusCode())
						return response;
				}
			}
		}
		return Response.ok().entity(RegistryConstant.SUCCESS).build();
	}
	
	public Response batchAddAppFeatures(List<PermissionProperty> featureList, String prePath, String tenantDomain) {
		if(featureList == null || featureList.size() <= 0 || prePath == null)
			return Response.status(Response.Status.BAD_REQUEST).entity(RegistryConstant.INVALID_INPUT).build();
		if(!registryDao.isResourceExists(prePath))
			return Response.status(Response.Status.NOT_FOUND).entity(RegistryConstant.RESOURCE_NOT_FOUND).build();
		for(PermissionProperty feature : featureList){
			if(feature == null){
				continue;
			}
			Response response = addResource(tenantDomain, prePath, feature);
			int statusCode = response.getStatus();
			if(statusCode == Response.Status.BAD_REQUEST.getStatusCode())
				continue;
			else if(statusCode == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
				return response;
		}
		return Response.ok().entity(RegistryConstant.SUCCESS).build();
	}
	private Response addResource(String tenantDomain, String prePath, PermissionProperty property){
//		registryDao.addAppPermission(RegistryConstant.PERMISSION_PATH, RegistryConstant.PERMISSION_ROOT_PATH);
		String alias = eliminateLeftSlash(property.getAlias());
		String name = property.getName();
		if(alias == null || alias =="" || name == null || name =="")
			return Response.status(Response.Status.BAD_REQUEST).entity(RegistryConstant.INVALID_INPUT).build();
		String resourcePath = prePath + alias;
		if(tenantDomain == null){
			if(!registryDao.addAppPermission(name, resourcePath))
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RegistryConstant.INTERNAL_ERROR).build();
		}
		else{
			if(!registryDao.addAppPermissionToTenant(tenantDomain, name, resourcePath))
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RegistryConstant.INTERNAL_ERROR).build();
		}
		return  Response.ok().entity(RegistryConstant.SUCCESS).build();
	}
	/*
	 * @param str=xx/yy or xxyyzz
	 * @return xx or xxyyzz
	 */
	private String eliminateLeftSlash(String str){
		if(str == null)
			return null;
		int slashIndex = str.indexOf("/");
		if(slashIndex == -1)
			return str;
		return str.substring(0,slashIndex);
	}
	
	private List<CloudApp> versionsFromPath(String featuresPath ,String versionName){
		List<CloudApp> versionList = new ArrayList<CloudApp>();
		try {
			String permissions = registryDao.getPermissions(null, featuresPath);
			List<String> features = JsonUtil.JsonToList(permissions, String.class);
			List<String> featureListOfVersion = new ArrayList<String>();
			for(String s : features){
				String feature = valueAfterLastLeftSlash(s);
				featureListOfVersion.add(feature);
			}
//			CloudApp app = new CloudApp();
//			app.setFeatures(featureListOfVersion);
//			app.setVersion(versionName);
//			versionList.add(app);
			return versionList;
		} catch (RegistryException e) {
			log.error("RegistryException: "+e.getMessage());
			return versionList;
		} catch (IOException e) {
			log.error("IOException: "+e.getMessage());
			return versionList;
		}
	}
	/*
	 * @param xx/yy/zz
	 * @return zz
	 */
	private String valueAfterLastLeftSlash(String str){
		if(str != null && str.length() >1){
			int start = str.lastIndexOf("/");
			str = str.substring(start+1);
			return str;
		}
		return null;
	}
	public String getPermissionsOfTenant(String tenantDomain) {
		String path = RegistryConstant.PERMISSION_ROOT_PATH;
		try {
			return registryDao.getPermissions(tenantDomain, path);
		} catch (RegistryException e) {
			log.error("RegistryException: "+e.getMessage());
			return null;
		} catch (IOException e) {
			log.error("IOException: "+e.getMessage());
			return null;
		}
	}

	public String getAllPermissions() {
		return null;
	}

}
