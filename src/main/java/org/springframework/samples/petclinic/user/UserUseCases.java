package org.springframework.samples.petclinic.user;

import org.springframework.samples.petclinic.rest.dto.UserDto;

public interface UserUseCases {
    UserDto addUser(UserDto userDto);
}
