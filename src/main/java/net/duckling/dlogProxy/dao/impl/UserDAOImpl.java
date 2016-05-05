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
import net.duckling.dlogProxy.dao.IUserDAO;
import net.duckling.dlogProxy.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements IUserDAO{
	@Autowired
	private BaseDAO<User> baseDAO;

	@Override
	public void insert(User u) {
		u.setId(baseDAO.insert(u));
	}

	@Override
	public User getUserByUmtId(String umtId) {
		User u=new User();
		u.setUmtId(umtId);
		return baseDAO.selectOne(u);
		
	}
	@Override
	public List<User> getUsersByGid(int gid) {
		String sql=" select u.*,r.is_admin "
				+" from user_info u,group_user_ref r "
				+" where u.id=r.user_id "
				+" and r.group_id=:gid";
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("gid",gid);
		return baseDAO.getTmpl().query(sql, map, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User u=new User();
				u.setCstnetId(rs.getString("cstnet_id"));
				u.setId(rs.getInt("id"));
				u.setTrueName(rs.getString("true_name"));
				u.setUmtId(rs.getString("umt_id"));
				u.setAdmin(rs.getBoolean("is_admin"));
				return u;
			}
		});
	}

}
