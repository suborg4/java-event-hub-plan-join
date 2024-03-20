package ru.suborg.ehpj.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.suborg.ehpj.users.UserMapper;
import ru.suborg.ehpj.users.UserRepository;
import ru.suborg.ehpj.users.dto.NewUserRequest;
import ru.suborg.ehpj.users.dto.UserDto;
import ru.suborg.ehpj.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserRequest)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (userIds == null) {
            return userRepository.findAll(pageable).map(UserMapper::toUserDto).getContent();
        } else {
            return userRepository.findAllByIdIn(userIds, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }
}
