package org.springframework.samples.petclinic.user.domain.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.user.domain.model.User;

public interface UserRepository {

    void save(User user) throws DataAccessException;
}
