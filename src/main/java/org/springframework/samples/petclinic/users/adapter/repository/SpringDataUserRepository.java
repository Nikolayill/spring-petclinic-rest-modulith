package org.springframework.samples.petclinic.users.adapter.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.users.domain.model.User;
import org.springframework.samples.petclinic.users.domain.service.UserRepository;

@Profile("spring-data-jpa")
public interface SpringDataUserRepository extends UserRepository, Repository<User, String>  {

}
