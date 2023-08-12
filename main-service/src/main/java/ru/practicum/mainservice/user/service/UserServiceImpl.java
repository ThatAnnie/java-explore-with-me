package ru.practicum.mainservice.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.common.CustomPageRequest;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("createUser: {}", newUserRequest);
        return UserMapper.INSTANCE.toUserDto(userRepository.save(UserMapper.INSTANCE.toUser(newUserRequest)));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("deleteUser with id={}", userId);
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        });
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("getUsers with ids ={} with from={}, size={}", ids, from, size);
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));
        List<User> users = (ids == null || ids.isEmpty())
                ? userRepository.findAll(pageRequest).getContent()
                : userRepository.findByIdIn(ids, pageRequest);
        return UserMapper.INSTANCE.toUsersDto(users);
    }
}
