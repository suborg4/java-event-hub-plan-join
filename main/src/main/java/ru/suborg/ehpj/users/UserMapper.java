package ru.suborg.ehpj.users;

import lombok.experimental.UtilityClass;
import ru.suborg.ehpj.users.dto.NewUserRequest;
import ru.suborg.ehpj.users.dto.UserDto;
import ru.suborg.ehpj.users.dto.UserShortDto;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public User toUser(NewUserRequest newUserRequest) {
        return new User(
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }
}
