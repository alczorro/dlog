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
package net.duckling.dlogProxy.web;

import javax.servlet.http.HttpServletRequest;

import net.duckling.dlogProxy.common.ProxyConfig;
import net.duckling.dlogProxy.domain.User;
import net.duckling.dlogProxy.service.IUserService;
import net.duckling.dlogProxy.web.helper.SessionHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.Oauth;
import cn.vlabs.umt.oauth.UMTOauthConnectException;
import cn.vlabs.umt.oauth.UserInfo;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;


@Controller
@RequestMapping("oauth/callback")
public class OauthCallBackController {
	private static final Logger LOGGER=Logger.getLogger(OauthCallBackController.class);

	@Autowired
	private ProxyConfig config;
	
	@Autowired
	private IUserService userService;

	
	@RequestMapping
	public ModelAndView callback(HttpServletRequest request){
		AccessToken token=null;
		UserInfo userInfo=null;
		try {
			Oauth oauth = new Oauth(config.getOauthProperties());
			token = oauth.getAccessTokenByRequest(request);
			userInfo = token.getUserInfo();
		} catch (UMTOauthConnectException e){
			LOGGER.error(e.getMessage(),e);
			return new ModelAndView(new RedirectView(request.getContextPath()+"msg.jsp?msg="+e.getMessage()));
		} catch (OAuthProblemException e) {
			LOGGER.error(e.getMessage()+"["+e.getError()+":"+e.getDescription()+"]",e);
			return new ModelAndView(new RedirectView(request.getContextPath()+"msg.jsp?msg="+e.getMessage()));
		}
		User u=userService.ifNotExistsThenInsert(userInfo);
		u.setSuperAdmin(config.isSuperAdmin(u.getCstnetId()));
		SessionHelper.setSessionUser(request,u);
		return new ModelAndView(new RedirectView(request.getContextPath()));

	}
}
