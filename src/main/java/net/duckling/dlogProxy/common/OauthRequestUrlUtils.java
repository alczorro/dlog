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
/**
 * 
 */
package net.duckling.dlogProxy.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.duckling.cloudy.common.CommonUtils;
import net.duckling.cloudy.common.UrlUtils;

/**
 * @author lvly
 * @since 2013-3-29
 */
public class OauthRequestUrlUtils {
	private static final Logger LOG=Logger.getLogger(OauthRequestUrlUtils.class);
	public static String getUrl(ProxyConfig appConfig){
		StringBuffer sb=new StringBuffer();
		sb.append(appConfig.getAuthorizeUrl())
		  .append("?").append("response_type=code")
		  .append("&redirect_uri=").append(CommonUtils.trim(appConfig.getRedirectUri()))
		  .append("&client_id=").append(CommonUtils.trim(appConfig.getClientId()))
		  .append("&theme=").append(CommonUtils.trim(appConfig.getTheme()));
		return sb.toString();
	}
	public static String getLogoutUrl(ProxyConfig config,HttpServletRequest request){
		StringBuffer sb=new StringBuffer();
		try {
			sb.append(config.getLogOutUrl())
			  .append("?").append("WebServerURL=")
			  .append(URLEncoder.encode(UrlUtils.getBaseURL(request),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(),e);
		}
		return sb.toString();
	}
}
