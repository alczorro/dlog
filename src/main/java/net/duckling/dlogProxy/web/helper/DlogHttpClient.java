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
package net.duckling.dlogProxy.web.helper;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.cloudy.common.CommonUtils;
import net.duckling.dlogProxy.common.BeanFactory;
import net.duckling.dlogProxy.common.ProxyConfig;
import net.duckling.dlogProxy.domain.User;
import net.duckling.dlogProxy.service.IGroupService;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class DlogHttpClient {
	private static final Logger LOG=Logger.getLogger(DlogHttpClient.class);
	private static String esAddress;
	private static String getEsAddress(){
		if(CommonUtils.isNull(esAddress)){
			esAddress=BeanFactory.getBean(ProxyConfig.class).getEsAddress();
		}
		return esAddress;
	}
	private static IGroupService groupService;
	private static IGroupService getGroupService(){
		if(groupService==null){
			groupService=BeanFactory.getBean(IGroupService.class);
		}
		return groupService;
	}
	public static void proxy(User u,HttpServletRequest request,HttpServletResponse response){
		String esUrl=getURL(request);
		if(esUrl==null){
			return;
		}
		if(u.getId()<1){
			LOG.error("can't understant the user who id is "+u.getId());
			return;
		}
		String method=request.getMethod();
		HttpUriRequest client = null;
		switch(method){
			case "GET":{
				client=new HttpGet(esUrl);
				break;
			}
			case "PUT":{
				client=new HttpPut(esUrl);
				setEntity((HttpPut)client, request);
				break;
			}
			case "POST":{
				client=new HttpPost(esUrl);
				setEntity((HttpPost)client, request);
				break;
			}
			case "DELETE":{
				client=new HttpDelete(esUrl);
				break;
			}
		}
		copyToNewHeader(request,client);
		readEntityToResponse(response,client);
	}
	private static void readEntityToResponse(HttpServletResponse response,HttpUriRequest uri){
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse responseEntity=null;
		try {
				responseEntity = client.execute(uri);
				int status = responseEntity.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					String returnStr=EntityUtils.toString(responseEntity.getEntity(),"UTF-8");
					Header[] headers=responseEntity.getAllHeaders();
					if(headers!=null){
						for(Header header:headers){
							response.setHeader(header.getName(),header.getValue());
						}
					}
					response.getWriter().println(returnStr);
				} else {
					LOG.error("elasticsearch service return code:"+status+"when request="+uri.getURI());
					return ;
				} 
			} catch (ParseException | IOException e) {
				LOG.error(e.getMessage(),e);
		} finally {
			try {
				if(client!=null)client.close();
				if(responseEntity!=null)responseEntity.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(),e);
			}
		}
	}
	
	private static void setEntity(HttpEntityEnclosingRequestBase post,HttpServletRequest request){
		try {
			post.setEntity(new InputStreamEntity(request.getInputStream()));
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
		}
	}
	private static void copyToNewHeader(HttpServletRequest request,HttpUriRequest post){
		Enumeration headersName=request.getHeaderNames();
		while(headersName.hasMoreElements()){
			String headerName=headersName.nextElement().toString();
			if("content-length".equals(headerName)){
				continue;
			}
			post.addHeader(headerName,request.getHeader(headerName));
		}
	}
	/**
	 * 获得es真实的URL
	 * */
	private static String getURL(HttpServletRequest request){
		String esAddress=getEsAddress();
		if(CommonUtils.isNull(esAddress)){
			LOG.error("can't find the es url,please config it");
			return null;
		}
		String context=request.getContextPath();
		if(CommonUtils.isNull(context)){
			context="/";
		}
		if(!context.endsWith("/")){
			context+="/";
		}
		String strartURI=context+"proxy";
		String requestURI=request.getRequestURI();
		String queryString=request.getQueryString();
		requestURI=requestURI.replace(strartURI, "");
		if(!CommonUtils.isNull(queryString)){
			requestURI+="?"+queryString;
		}
		return esAddress+requestURI;
	}
}
