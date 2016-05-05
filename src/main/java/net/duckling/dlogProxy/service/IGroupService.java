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
package net.duckling.dlogProxy.service;

import java.util.List;

import net.duckling.dlogProxy.domain.Group;

public interface IGroupService {
	List<Group> getMyAllGroup(int uid);

	List<Group> adminGroups(List<Group> groups);

	boolean isAdmin(int gid, int uid);

	void deleteGroup(int gid);

	boolean isUsed(String groupName);

	void create(Group g);

	List<Group> getAllGroups();

	List<Group> getMyAdminGroups(int id);

	Group getGroupById(int gid);

	boolean isMember(int gid, int id);
	
	boolean isMember(String name,int id);

	void addMember(int gid, int id);

	boolean deleteMember(int gid, int uid);

	boolean changeAdmin(int gid, int uid, boolean isAdmin);

	boolean isIndexUsed(String groupIndex);

	Group getGroupByIndex(String index);

	List<Group> getGroupsByIndexLike(String absolute);

	Group getGroupByName(String search);

	boolean canChangeNameTo(int groupId, String groupName);

	boolean canChangeIndexTo(int groupId, String groupIndex);

	void update(Group g);
}
