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
package net.duckling.dlogProxy.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.dlogProxy.common.OauthRequestUrlUtils;
import net.duckling.dlogProxy.common.ProxyConfig;
import net.duckling.dlogProxy.web.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginInterCeptor extends HandlerInterceptorAdapter{
	@Autowired
	private ProxyConfig proxyConfig;
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if( SessionHelper.hasSessionUser(request)){
			return true;
		}else {
			String url=OauthRequestUrlUtils.getLogoutUrl(proxyConfig, request);
			response.sendRedirect(url);
			return false;
		}
	}
}
