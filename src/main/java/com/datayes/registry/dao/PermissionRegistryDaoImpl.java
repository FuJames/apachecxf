package com.datayes.registry.dao;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import com.datayes.registry.util.JsonUtil;
/**
 * @author qianzhong.fu
 *
 */
public class PermissionRegistryDaoImpl extends PermissionRegistryDao{
	private Logger log = LoggerFactory.getLogger(PermissionRegistryDaoImpl.class);
	
	@Override
	public boolean addAppPermission(String property, String resourcePath) {
		Registry registry = getRegistry();
		return addPermission(registry, property, resourcePath);
	}
	@Override
	public boolean addAppPermissionToTenant(String tenantDomain,String property, String path) {
		Registry registry = getRegistryOfTenant(tenantDomain);
		boolean status = addPermission(registry, property, path);
		PrivilegedCarbonContext.endTenantFlow();
		return status;
	}
	private boolean addPermission(Registry registry,String property,String resourcePath){
		if(registry == null){
			log.error("registry null");
			return false;
		}
		try{
			Resource resource = registry.newCollection();
			resource.setProperty("name", property);
			registry.put(resourcePath, resource);
			return true;
		}catch(RegistryException e){
			log.error("error message is "+e.getMessage());
			return false;
		}
	}
	/*
	 * path : start with /permission....
	 */
	public String getPermissions(String tenantDomain,String path) throws IOException, RegistryException {
		Registry registry = getRegistry();
		if(tenantDomain != null)
			registry = getRegistryOfTenant(tenantDomain);
		if(registry == null){
			log.error("registry null");
			return null;
		}
		Resource resource = registry.get(path);
		String[] resourcePaths = null;
		if ((resource instanceof Collection)){
			Collection collection = (Collection)resource;
			resourcePaths = collection.getChildren();
		}
		if(tenantDomain != null)
			PrivilegedCarbonContext.endTenantFlow();
		return JsonUtil.objToJson(resourcePaths);
	}
	
}
