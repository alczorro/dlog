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

import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;

import net.duckling.dlogProxy.common.OauthRequestUrlUtils;
import net.duckling.dlogProxy.common.ProxyConfig;
import net.duckling.dlogProxy.web.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("logout")
public class LogoutController {
	@Autowired
	private ProxyConfig config;
	
	public static void main(String[] args) throws Exception{
		for(int i=1;i<3000;i++){
			URL url=new URL("http://passporttest.escience.cn/js/isLogin.do");
			URLConnection urlConn=url.openConnection();
			urlConn.getInputStream();
			if(i%100==0){
				System.out.println("success:"+i);
			}
		}
	}
	@RequestMapping
	public RedirectView logout(HttpServletRequest request){
		SessionHelper.setSessionUser(request, null);
		return new RedirectView(OauthRequestUrlUtils.getLogoutUrl(config, request));
	}
}
