package org.springframework.samples.petclinic.user.application;

import org.springframework.samples.petclinic.rest.dto.UserDto;
import org.springframework.samples.petclinic.user.UserUseCase;
import org.springframework.samples.petclinic.user.domain.model.User;
import org.springframework.samples.petclinic.user.domain.service.UserMapper;
import org.springframework.samples.petclinic.user.domain.service.UserService;
import org.springframework.stereotype.Component;

@Component
class UserUseCasesImpl implements UserUseCase {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserUseCasesImpl(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        this.userService.saveUser(user);
        return userMapper.toUserDto(user);
    }
}
