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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.dlogProxy.domain.Group;
import net.duckling.dlogProxy.domain.User;
import net.duckling.dlogProxy.service.IGroupService;
import net.duckling.dlogProxy.web.helper.DlogHttpClient;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("proxy")
@SessionAttributes("loginUser")
public class ProxyController {
	private static final Logger LOG=Logger.getLogger(ProxyController.class);
	
	@Autowired
	private IGroupService groupService;
	
	@RequestMapping("_nodes")
	@ResponseBody
	public void node(@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response)
	{
		DlogHttpClient.proxy(u,request, response);
	}
	@RequestMapping(value="{search:.*}/_search")
	public void search(@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		DlogHttpClient.proxy(u,request, response);
	}
	private void checkAllPartternEqual(String index,String absolute){
		String[] patterns=index.split(",");
		for(String p:patterns){
			if(!p.startsWith(absolute)){
				throw new RuntimeException("pattern is not equals others,"+index);
			}
		}
	}
	private String getFirstIndex(String index){
		if(!index.contains(",")){
			return index;
		}
		return index.split(",")[0];
	}
	private Group getLikeGroup(String index){
		List<Group> all=groupService.getAllGroups();
		String first=getFirstIndex(index);
		for(Group g:all){
			if(g.getGroupIndex().contains("[")&&g.getGroupIndex().contains("]")){
				String absolute=g.getGroupIndex().substring(g.getGroupIndex().indexOf("[")+1, g.getGroupIndex().indexOf("]"));
				if(first.startsWith(absolute)){
					checkAllPartternEqual(index,absolute);
					return g;
				}
			}else if(first.equals(g.getGroupIndex())){
				return g;
			}
		}
		return null;
		
	}
	@RequestMapping(value="{indexName}/_search")
	public void searchIndex(@PathVariable("indexName")String indexName,@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		Group g=getLikeGroup(indexName);
		if(g==null){
			LOG.error("group not found who index is like ["+indexName+"]");
			return;
		}
		if(!groupService.isMember(g.getId(),u.getId())&&!u.isSuperAdmin()){
			LOG.error("user["+u.getCstnetId()+"] is not member of["+g.getGroupName()+"]");
			return;
		}
		DlogHttpClient.proxy(u,request, response);
	}
	@RequestMapping(value="_aliases")
	public void aliases(@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		DlogHttpClient.proxy(u,request,response);
	}
	@RequestMapping(value="{search:.*}/_aliases")
	public void aliasesLong(@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		DlogHttpClient.proxy(u,request,response);
	}
	@RequestMapping(value="{search:.*}/_mapping")
	public void mapping(@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		DlogHttpClient.proxy(u,request,response);
	}
	@RequestMapping(value="kibana-int/dashboard/{search:.*}")
	public void getAppInfo(@PathVariable("search")String search,@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		Group g=groupService.getGroupByName(search);
		if(g==null){
			LOG.error("can't find group name is ["+search+"]");
			return;
		}
		if(!groupService.isMember(g.getId(),u.getId())&&!u.isSuperAdmin()){
			LOG.error("user["+u.getCstnetId()+"] is not member of["+g.getGroupName()+"]");
			return;
		}
		DlogHttpClient.proxy(u,request, response);
	}
	@RequestMapping(value="/kibana-int/temp{search:.*}")
	public void tempWrite(@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		DlogHttpClient.proxy(u,request, response);
	}
	@RequestMapping(value="/kibana-int/temp/{search:.*}")
	public void tempRead(@ModelAttribute("loginUser")User u,HttpServletRequest request,HttpServletResponse response){
		DlogHttpClient.proxy(u,request, response);
	}
}
