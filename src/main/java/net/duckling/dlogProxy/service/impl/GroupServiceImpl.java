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
package net.duckling.dlogProxy.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.duckling.cloudy.common.CommonUtils;
import net.duckling.dlogProxy.dao.IGroupDAO;
import net.duckling.dlogProxy.domain.Group;
import net.duckling.dlogProxy.service.IGroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements IGroupService{
	
	@Autowired
	private IGroupDAO groupDAO;
	
	@Override
	public List<Group> getMyAllGroup(int uid) {
		return groupDAO.getMyAllGroup(uid);
	}
	@Override
	public List<Group> adminGroups(List<Group> groups) {
		List<Group> admins=new ArrayList<Group>();
		if(CommonUtils.isNull(groups)){
			return admins;
		}
		for(Group g:groups){
			if(g.isAdmin()){
				admins.add(g);
			}
		}
		return admins;
	}
	@Override
	public boolean isAdmin(int gid, int uid) {
		return groupDAO.isAdmin(gid,uid);
	}
	@Override
	public void deleteGroup(int gid) {
		if(gid==0){
			return;
		}
		groupDAO.deleteGroup(gid);
		
	}
	@Override
	public boolean isUsed(String groupName) {
		if(CommonUtils.isNull(groupName)){
			return true;
		}
		return groupDAO.isUsed(CommonUtils.trim(groupName));
	}
	@Override
	public void create(Group g) {
		groupDAO.create(g);
	}
	@Override
	public List<Group> getAllGroups() {
		return groupDAO.getAllGroups();
	}
	@Override
	public List<Group> getMyAdminGroups(int id) {
		return groupDAO.getMyAdminGroups(id);
	}
	@Override
	public Group getGroupById(int gid) {
		return groupDAO.getGroupById(gid);
	}
	@Override
	public boolean isMember(int gid, int uid) {
		return groupDAO.isMember(gid,uid);
	}
	@Override
	public void addMember(int gid, int id) {
		groupDAO.addMember(gid,id);
		
	}
	@Override
	public boolean deleteMember(int gid, int uid) {
		if(groupDAO.isAdmin(gid, uid)&&groupDAO.getAdminCount(gid)==1){
			return false;
		}
		groupDAO.deleteMember(gid,uid);
		return true;
	}
	@Override
	public boolean changeAdmin(int gid, int uid, boolean isAdmin) {
		
		//如果是干掉管理员，则检查是否是最后一个管理员
		if(isAdmin==false&&groupDAO.isAdmin(gid, uid)&&groupDAO.getAdminCount(gid)==1){
			return false;
		}
		groupDAO.changeAdmin(gid,uid,isAdmin);
		return true;
	}
	@Override
	public boolean isMember(String name, int uid) {
		return groupDAO.isMember(name, uid);
	}
	@Override
	public boolean isIndexUsed(String groupIndex) {
		return groupDAO.isIndexUsed(CommonUtils.trim(groupIndex));
	}
	@Override
	public Group getGroupByIndex(String index) {
		if(CommonUtils.isNull(index)){
			return null;
		}
		return groupDAO.getGroupByIndex(index.trim());
	}
	@Override
	public List<Group> getGroupsByIndexLike(String absolute) {
		if(CommonUtils.isNull(absolute)){
			return null;
		}
		return groupDAO.getGroupsByIndexLike(absolute.trim());
	}
	@Override
	public Group getGroupByName(String search) {
		if(CommonUtils.isNull(search)){
			return null;
		}
		return groupDAO.getGroupByName(search.trim());
	}
	@Override
	public boolean canChangeIndexTo(int groupId, String groupIndex) {
		if(groupId==0||CommonUtils.isNull(groupIndex)){
			return false;
		}
		return groupDAO.canChangeIndexTo(groupId,groupIndex.trim());
	}
	
	@Override
	public boolean canChangeNameTo(int groupId, String groupName) {
		if(groupId==0||CommonUtils.isNull(groupName)){
			return false;
		}
		return groupDAO.canChangeNameTo(groupId,groupName.trim());
	}
	@Override
	public void update(Group g) {
		groupDAO.update(g);
		
	}

}
