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

import net.duckling.cloudy.common.CommonUtils;
import net.duckling.dlogProxy.domain.Detail;
import net.duckling.dlogProxy.domain.Group;
import net.duckling.dlogProxy.domain.JsonResult;
import net.duckling.dlogProxy.domain.User;
import net.duckling.dlogProxy.service.IGroupService;
import net.duckling.dlogProxy.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import cn.vlabs.duckling.api.umt.rmi.userv7.UMTUser;
import cn.vlabs.duckling.api.umt.rmi.userv7.UserService;
import cn.vlabs.umt.oauth.UserInfo;

@Controller
@RequestMapping("/user/group")
@SessionAttributes("loginUser")
public class GroupController {

	@Autowired
	private IGroupService groupService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private UserService umtService;
	
	@RequestMapping("list")
	public ModelAndView list(@ModelAttribute("loginUser")User u){
		ModelAndView mv=new ModelAndView("groups");
		List<Group> groups=null;
		if(u.isSuperAdmin()){
			groups=groupService.getAllGroups();
		}else{
			groups=groupService.getMyAdminGroups(u.getId());
		}
		mv.addObject("groups",groups);
		mv.addObject("adminGroups",groupService.adminGroups(groups));
		return mv;
	}
	
	@RequestMapping(value="create",method=RequestMethod.POST)
	@ResponseBody
	public JsonResult create(@ModelAttribute("loginUser")User u,@RequestParam("groupName")String groupName,@RequestParam("groupIndex")String groupIndex){
		if(!u.isSuperAdmin()){
			return new JsonResult("权限不足");
		}
		if(CommonUtils.isNull(groupName)){
			return new JsonResult("应用名称不应为空");
		}
		if(CommonUtils.isNull(groupIndex)){
			return new JsonResult("应用索引不能为空");
		}
		if(groupService.isUsed(groupName)){
			return new JsonResult("应用名称已使用");
		}
		if(groupService.isIndexUsed(groupIndex)){
			return new JsonResult("应用索引重复");
		}
		Group g=new Group();
		g.setGroupName(CommonUtils.trim(groupName));
		g.setGroupIndex(CommonUtils.trim(groupIndex));
		groupService.create(g); 
		return new JsonResult();
	}
	
	@RequestMapping(value="detail/{groupId}",method=RequestMethod.GET)
	@ResponseBody
	public JsonResult detail(@ModelAttribute("loginUser")User u,@PathVariable("groupId")int gid){
		if(!groupService.isAdmin(gid,u.getId())&&!u.isSuperAdmin()){
			return new JsonResult("权限不足");
		}
		Group group=groupService.getGroupById(gid);
		if(group==null){
			return new JsonResult("未找到应用");
		}
		List<User> users=userService.getUsersByGid(gid);
		Detail d=new Detail();
		d.setUsers(users);
		d.setGroup(group);
		JsonResult result=new JsonResult();
		result.setData(d);
		return result;
	}
	@RequestMapping(value="deleteMember" ,method=RequestMethod.POST)
	@ResponseBody
	public JsonResult deleteMember(@ModelAttribute("loginUser")User u,@RequestParam("gid")int gid,@RequestParam("uid")int uid){
		if(u.getId()==uid){
			return new JsonResult("请不要删除自己");
		}
		if(!groupService.isAdmin(gid,u.getId())&&!u.isSuperAdmin()){
			return new JsonResult("权限不足");
		}
		if(!groupService.deleteMember(gid,uid)){
			return new JsonResult("请勿删除最后一个管理员");
		};
		return new JsonResult();
	}
	
	@RequestMapping(value="changeAdmin" ,method=RequestMethod.POST)
	@ResponseBody
	public JsonResult addMember(@ModelAttribute("loginUser")User u,@RequestParam("uid")int uid,@RequestParam("val")boolean isAdmin,@RequestParam("gid")int gid){
		if(!u.isSuperAdmin()&&u.getId()==uid){
			return new JsonResult("请勿更改自己的管理员属性");
		}
		if(!groupService.isAdmin(gid,u.getId())&&!u.isSuperAdmin()){
			return new JsonResult("权限不足");
		}
		boolean result=groupService.changeAdmin(gid, uid,isAdmin);
		if(!result){
			return new JsonResult("请勿删除最后一个管理员");
		}
		return new JsonResult();
	}
	
	@RequestMapping(value="addMember" ,method=RequestMethod.POST)
	@ResponseBody
	public JsonResult addMember(@ModelAttribute("loginUser")User u,@RequestParam("memberCstnetId")String cstnetId,@RequestParam("gid")int gid){
		if(CommonUtils.isNull(cstnetId)){
			return new JsonResult("请输入新增用户的邮箱全称");
		}
		if(!groupService.isAdmin(gid,u.getId())&&!u.isSuperAdmin()){
			return new JsonResult("权限不足");
		}
		UMTUser umtUser=umtService.getUMTUserByLoginName(CommonUtils.trim(cstnetId));
		if(umtUser==null){
			return new JsonResult("未找到该用户");
		}
		UserInfo userInfo=new UserInfo();
		userInfo.setCstnetId(umtUser.getCstnetId());
		userInfo.setTrueName(umtUser.getTruename());
		userInfo.setUmtId(umtUser.getUmtId());
		User newUser=userService.ifNotExistsThenInsert(userInfo);
		if(groupService.isMember(gid,newUser.getId())){
			return new JsonResult("用户已存在");
		}
		groupService.addMember(gid,newUser.getId());
		return new JsonResult();
	}
	@RequestMapping(value="delete/{groupId}",method=RequestMethod.POST)
	@ResponseBody
	public JsonResult delete(@ModelAttribute("loginUser")User u,@PathVariable("groupId")int gid){
		if(!groupService.isAdmin(gid,u.getId())&&!u.isSuperAdmin()){
			return new JsonResult("权限不足");
		}
		groupService.deleteGroup(gid);
		return new JsonResult();
	}
	@RequestMapping(value="update",method=RequestMethod.POST)
	@ResponseBody
	public JsonResult update(
			@ModelAttribute("loginUser")User u,
			@RequestParam("groupId")int groupId,
			@RequestParam("groupName")String groupName,
			@RequestParam("groupIndex")String groupIndex
			){
		if(groupId==0){
			return new JsonResult("未找到应用");
		}
		if(CommonUtils.isNull(groupName)){
			return new JsonResult("应用名称不允许为空");
		}
		if(CommonUtils.isNull(groupIndex)){
			return new JsonResult("应用索引不允许为空");
		}
		if(!groupService.isAdmin(groupId,u.getId())&&!u.isSuperAdmin()){
			return new JsonResult("权限不足");
		}
		if(!groupService.canChangeNameTo(groupId,groupName)){
			return new JsonResult("应用名称已被使用");
		}
		if(!groupService.canChangeIndexTo(groupId,groupIndex)){
			return new JsonResult("应用索引名已被使用");
		}
		Group g=new Group();
		g.setGroupIndex(groupIndex.trim());
		g.setGroupName(groupName.trim());
		g.setId(groupId);
		groupService.update(g);
		return new JsonResult();
	}
	
}
