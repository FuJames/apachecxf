package com.datayes.registry.controller;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.datayes.registry.model.CloudApp;
import com.datayes.registry.service.PermissionRegistryService;
import com.datayes.registry.util.RegistryConstant;

public class RegistryController{
	private PermissionRegistryService registryService;
	public PermissionRegistryService getRegistryService() {
		return registryService;
	}
	public void setRegistryService(PermissionRegistryService registryService) {
		this.registryService = registryService;
	}
	
	@POST
	@Path("/addApps")
	@Consumes(RegistryConstant.JSON_TYPE)
	@Produces(RegistryConstant.JSON_TYPE)
	public Response addApps(List<CloudApp> appList){
		return registryService.batchAddPermissions(appList);
	}

	@POST
	@Path("/addVersions/{appAlias}")
	@Consumes(RegistryConstant.JSON_TYPE)
	@Produces(RegistryConstant.JSON_TYPE)
	public Response addVersions(List<CloudApp> versionList,@PathParam("appAlias") String appAlias){
		return registryService.batchAddAppVersions(versionList, appAlias, null);
	}
	
	@POST
	@Path("/addApps/{tenantDomain}")
	@Consumes(RegistryConstant.JSON_TYPE)
	@Produces(RegistryConstant.JSON_TYPE)
	public Response addAppsToTenant(List<CloudApp> appList,@PathParam("tenantDomain") String tenantDomain){
		registryService.batchAddAppsToTenant(appList, tenantDomain);
		return Response.ok().build();
	}
	
}


