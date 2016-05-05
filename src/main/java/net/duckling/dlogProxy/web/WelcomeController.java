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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.duckling.cloudy.common.CommonUtils;
import net.duckling.dlogProxy.common.OauthRequestUrlUtils;
import net.duckling.dlogProxy.common.ProxyConfig;
import net.duckling.dlogProxy.domain.Group;
import net.duckling.dlogProxy.domain.User;
import net.duckling.dlogProxy.service.IGroupService;
import net.duckling.dlogProxy.web.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
public class WelcomeController {
	@Autowired
	private ProxyConfig config;

	@Autowired
	private IGroupService groupService;

	@RequestMapping
	@ResponseBody
	public ModelAndView index(HttpServletRequest request) throws UnsupportedEncodingException {
		if (!SessionHelper.hasSessionUser(request)) {
			return new ModelAndView(new RedirectView(OauthRequestUrlUtils.getUrl(config)));

		}
		User u=SessionHelper.getSessionUser(request);
		
		List<Group> groups=null;
		if(u.isSuperAdmin()){
			groups=groupService.getAllGroups();
		}else{
			groups=groupService.getMyAllGroup(u.getId());
		}
		if(CommonUtils.isNull(groups)){
			if(u.isSuperAdmin()){
				return new ModelAndView(new RedirectView(request.getContextPath()+"/user/group/list"));
			}else{
				return new ModelAndView(new RedirectView(request.getContextPath()+"/msg.jsp?msg="+URLEncoder.encode("未找到任何应用信息","UTF-8")));
			}
		}
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("groups",groups);
		mv.addObject("adminGroups",groupService.adminGroups(groups));
		return mv;
	}
	

}
