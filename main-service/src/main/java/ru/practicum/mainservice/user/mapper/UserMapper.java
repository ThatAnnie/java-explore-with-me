package ru.practicum.mainservice.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.dto.UserShortDto;
import ru.practicum.mainservice.user.model.User;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);

    List<UserDto> toUsersDto(List<User> users);
}
