package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {

    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);

//    public static UserDto toUserDto(User user) {
//        return new UserDto(
//                user.getId(),
//                user.getName(),
//                user.getEmail()
//        );
//    }

//    public static User toUser(UserDto userDto) {
//        return new User(
//                userDto.getId(),
//                userDto.getName(),
//                userDto.getEmail()
//        );
//    }
}
