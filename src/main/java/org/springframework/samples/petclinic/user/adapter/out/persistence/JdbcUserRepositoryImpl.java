/*
 * Copyright 2016-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.user.adapter.out.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.samples.petclinic.user.domain.model.Role;
import org.springframework.samples.petclinic.user.domain.model.User;
import org.springframework.samples.petclinic.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jdbc")
public class JdbcUserRepositoryImpl implements UserRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertUser = new SimpleJdbcInsert(dataSource).withTableName("users");
    }

    @Override
    public void save(User user) throws DataAccessException {

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        try {
            getByUsername(user.getUsername());
            this.namedParameterJdbcTemplate.update("UPDATE users SET password=:password, enabled=:enabled WHERE username=:username", parameterSource);
        } catch (EmptyResultDataAccessException e) {
            this.insertUser.execute(parameterSource);
        } finally {
            updateUserRoles(user);
        }
    }

    private User getByUsername(String username) {

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        return this.namedParameterJdbcTemplate.queryForObject("SELECT * FROM users WHERE username=:username",
            params, BeanPropertyRowMapper.newInstance(User.class));
    }

    private void updateUserRoles(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        this.namedParameterJdbcTemplate.update("DELETE FROM roles WHERE username=:username", params);
        for (Role role : user.getRoles()) {
            params.put("role", role.getName());
            if (role.getName() != null) {
                this.namedParameterJdbcTemplate.update("INSERT INTO roles(username, role) VALUES (:username, :role)", params);
            }
        }
    }
}
