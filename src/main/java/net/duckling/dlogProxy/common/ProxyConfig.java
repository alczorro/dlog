/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */
package net.duckling.dlogProxy.common;

/**
 * 
 */

import java.io.Serializable;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * 系统配置项，路径是conf/vmt.properties
 * @author lvly
 * @since 2013-4-15
 */
@Configuration
@PropertySource("classpath:/proxy.properties")
public class ProxyConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -434021228316796216L;
	
	@Value("${elasticsearch.address}")
	private String esAddress;
	
	
	/**
	 *  =key
		=secret
		=http://localhost:8222/dlogProxy/oauth/callback
		=${duckling.umt.site}/ouath2/token
		=${duckling.umt.site}/oauth2/authorize
		=
		e=full
	 * 
	 * */
	@Value("${oauth.umt.client_id}")
	private String clientId;
	@Value("${oauth.umt.client_secret}")
	private String clientSecret;
	@Value("${oauth.umt.redirect_uri}")
	private String redirectUri;
	@Value("${oauth.umt.access_token_URL}")
	private String accessToken;
	@Value("${oauth.umt.authorize_URL}")
	private String authorizeUrl;
	@Value("${oauth.umt.theme}")
	private String theme;
	@Value("${oauth.umt.scope}")
	private String scope;
	
	@Value("${duckling.umt.logout}")
	private String logOutUrl;
	
	@Value("${super.admin}")
	private String superAdminStr;
	
	public boolean isSuperAdmin(String cstnetId){
		String[] superAdmins=superAdminStr.split(",");
		for(String superAdmin:superAdmins){
			if(superAdmin.equals(cstnetId)){
				return true;
			}
		}
		return false;
	}
	
	public String getLogOutUrl() {
		return logOutUrl;
	}

	public void setLogOutUrl(String logOutUrl) {
		this.logOutUrl = logOutUrl;
	}


	public Properties getOauthProperties(){
		Properties prop=new Properties();
		prop.setProperty("client_id", getClientId());
		prop.setProperty("client_secret", getClientSecret());
		prop.setProperty("redirect_uri", getRedirectUri());
		prop.setProperty("access_token_URL",getAccessToken());
		prop.setProperty("authorize_URL", getAuthorizeUrl());
		prop.setProperty("scope",getScope());
		prop.setProperty("theme", getTheme());
		return prop;
	}

	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAuthorizeUrl() {
		return authorizeUrl;
	}



	public void setAuthorizeUrl(String authorizeUrl) {
		this.authorizeUrl = authorizeUrl;
	}



	public String getTheme() {
		return theme;
	}



	public void setTheme(String theme) {
		this.theme = theme;
	}



	public String getScope() {
		return scope;
	}



	public void setScope(String scope) {
		this.scope = scope;
	}



	public String getEsAddress() {
		return esAddress;
	}



	public void setEsAddress(String esAddress) {
		this.esAddress = esAddress;
	}
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
