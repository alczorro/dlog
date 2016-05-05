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
package net.duckling.dlogProxy.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.cloudy.db.simpleORM.repository.BaseDAO;
import net.duckling.dlogProxy.dao.IGroupDAO;
import net.duckling.dlogProxy.domain.Group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDAOImpl implements IGroupDAO{
	
	private static final RowMapper<Group> rowMapper=new RowMapper<Group>() {
		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			Group g=new Group();
			g.setAdmin(rs.getBoolean("is_admin"));
			g.setGroupName(rs.getString("group_name"));
			g.setId(rs.getInt("id"));
			return g;
		}
	};
	@Autowired
	private BaseDAO<Group> baseDAO;
	
	@Override
	public boolean isAdmin(int gid, int uid) {
		String sql="select count(*) c from group_user_ref r "
				+" where r.group_id=:groupId "
				+" and r.user_id=:uid "
				+" and r.is_admin='true'";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("uid",uid);
		map.put("groupId", gid);
		return baseDAO.getTmpl().queryForInt(sql, map)>0;
	}
	@Override
	public boolean isUsed(String groupName) {
		String sql="select count(*) c from groups g "
				+" where g.group_name=:groupName ";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("groupName",groupName);
		return baseDAO.getTmpl().queryForInt(sql, map)>0;
	}
	@Override
	public List<Group> getMyAllGroup(int uid) {
		String sql="select g.*,r.is_admin from groups g ,group_user_ref r "
					+" where  g.id=r.group_id "
					+" and r.user_id=:uid";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("uid",uid);
		return baseDAO.getTmpl().query(sql,map, rowMapper);
	}
	@Override
	public void deleteGroup(int gid) {
		Group g=new Group();
		g.setId(gid);
		baseDAO.delete(g);
	}
	@Override
	public void create(Group g) {
		baseDAO.insert(g);
	}
	@Override
	public List<Group> getAllGroups() {
		return baseDAO.select(new Group());
	}
	@Override
	public List<Group> getMyAdminGroups(int uid) {
		String sql="select g.*,r.is_admin from groups g ,group_user_ref r "
				+" where  g.id=r.group_id "
				+" and r.user_id=:uid"
				+" and r.is_admin='true'";
	Map<String,Object> map=new HashMap<String,Object>();
	map.put("uid",uid);
	return baseDAO.getTmpl().query(sql,map, rowMapper);
	}
	@Override
	public Group getGroupById(int gid) {
		Group g=new Group();
		g.setId(gid);
		return baseDAO.selectOne(g);
	}
	@Override
	public boolean isMember(int gid, int uid) {
		String sql="select count(*) c from group_user_ref r "
				+" where r.group_id=:groupId "
				+" and r.user_id=:uid ";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("uid",uid);
		map.put("groupId", gid);
		return baseDAO.getTmpl().queryForInt(sql, map)>0;
	}
	@Override
	public void addMember(int gid, int uid) {
		String sql="insert into group_user_ref(`group_id`,`user_id`) values(:gid,:uid)";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("gid", gid);
		map.put("uid", uid);
		baseDAO.getTmpl().update(sql, map);
	}
	@Override
	public void deleteMember(int gid, int uid) {
		String sql="delete from group_user_ref where group_id=:gid and user_id=:uid";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("gid", gid);
		map.put("uid", uid);
		baseDAO.getTmpl().update(sql, map);
	}
	@Override
	public int getAdminCount(int gid) {
		String sql="select count(*) c from group_user_ref r "
				+" where r.group_id=:groupId "
				+" and r.is_admin='true'";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("groupId", gid);
		return baseDAO.getTmpl().queryForInt(sql, map);
	}
	@Override
	public void changeAdmin(int gid, int uid, boolean isAdmin) {
		String sql="update group_user_ref set is_admin=:isAdmin where group_id=:gid and user_id=:uid";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("gid", gid);
		map.put("uid", uid);
		map.put("isAdmin",isAdmin?"true":"false");
		baseDAO.getTmpl().update(sql, map);		
	}
	@Override
	public boolean isMember(String name, int uid) {
		String sql="select count(*) c from group_user_ref r,groups g "
				+" where r.group_id=g.id "
				+" and r.user_id=:uid "
				+" and g.group_name=:groupName";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("uid",uid);
		map.put("groupName", name);
		return baseDAO.getTmpl().queryForInt(sql, map)>0;
	}
	@Override
	public boolean isIndexUsed(String index) {
		String sql="select count(*) c from groups g "
				+" where g.group_index=:groupIndex ";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("groupIndex",index);
		return baseDAO.getTmpl().queryForInt(sql, map)>0;
	}
	@Override
	public Group getGroupByIndex(String index) {
		Group g=new Group();
		g.setGroupIndex(index);
		return baseDAO.selectOne(g);
	}
	@Override
	public List<Group> getGroupsByIndexLike(String trim) {
		String sql="select * c from groups g "
				+" where g.group_index like :absolute ";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("absolute","%"+trim+"%");
		return baseDAO.getTmpl().query(sql,map, baseDAO.getORMParser(Group.class).getRowMapper());
	}
	@Override
	public Group getGroupByName(String trim) {
		Group g=new Group();
		g.setGroupName(trim);
		return baseDAO.selectOne(g);
	}
	
	@Override
	public boolean canChangeIndexTo(int groupId, String groupIndex) {
		String sql="select count(*) c from groups g "
				+" where g.id!=:groupId "
				+" and g.group_index=:index ";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("index",groupIndex);
		map.put("groupId", groupId);
		return baseDAO.getTmpl().queryForInt(sql, map)==0;
	}
	@Override
	public boolean canChangeNameTo(int groupId, String groupName) {
		String sql="select count(*) c from groups g "
				+" where g.id!=:groupId "
				+" and g.group_name=:groupName ";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("groupName",groupName);
		map.put("groupId", groupId);
		return baseDAO.getTmpl().queryForInt(sql, map)==0;
	}
	@Override
	public void update(Group g) {
		baseDAO.update(g);
	}
	
}
