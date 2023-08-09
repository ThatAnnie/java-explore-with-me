package ru.practicum.mainservice.user.service;

import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
