package ru.suborg.ehpj.users.service;

import ru.suborg.ehpj.users.dto.NewUserRequest;
import ru.suborg.ehpj.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);

    void deleteUser(Long userId);
}
