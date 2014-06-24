package com.datayes.registry.dao;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
/**
 * @author qianzhong.fu
 *
 */
public abstract class PermissionRegistryDao {
	private Logger log = LoggerFactory.getLogger(PermissionRegistryDao.class);
	/*
	 * add permission to path in superTenant's Governance Registry;
	 * @resourceName colleciton name in Greg
	 * @param permission name
	 * @param path
	 * @return true or false;
	 */
	public abstract boolean addAppPermission(String property, String resourcePath);
	/*
	 * add permissions to Greg of given tenant
	 * @param tenantDomain
	 * @param property
	 * @param path
	 * @return true or false;
	 */
	public abstract boolean addAppPermissionToTenant(String tenantDomain,String property, String resourcePath);
	/*
	 * get permission tree of the given path in Greg of tenantDomain. if tenantDomain equals, then use default super tenant.
	 * @param path 
	 * @return permission tree
	 */
	public abstract String getPermissions(String tenantDomain,String resourcePath) throws IOException , RegistryException;
	/*
	 * get Registry of Super tenant;
	 * @return registry
	 */
	public Registry getRegistry(){
		Registry registry = null;
		PrivilegedCarbonContext context = PrivilegedCarbonContext.getCurrentContext(); 
		if(context == null){
			log.error("carbon context null");
			return registry;
		}
		registry = (Registry)context.getRegistry(RegistryType.SYSTEM_GOVERNANCE); 
		return registry;
	}
	/*
	 * return registry of the given tenant
	 * @param tenantDomain
	 * @return registry
	 */
	public Registry getRegistryOfTenant(String tenantDomain){
		Registry registry = null;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext context = PrivilegedCarbonContext.getCurrentContext(); 
		context.setTenantDomain(tenantDomain,true);
		PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext(); 
		if(carbonContext == null){
			return registry;
		}
		registry = (Registry)carbonContext.getRegistry(RegistryType.SYSTEM_GOVERNANCE); 
		return registry;
	}
	public boolean isResourceExists(String resourcePath){
		Registry registry = getRegistry();
		if(registry == null){
			log.error("registry null");
			return false;
		}
		try{
			return registry.resourceExists(resourcePath);
		}catch(RegistryException e){
			log.error("registry error");
			return false;
		}
	}
}
