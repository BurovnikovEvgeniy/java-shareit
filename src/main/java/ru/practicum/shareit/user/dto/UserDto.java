package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.validated.group.CreateUserValidation;
import ru.practicum.shareit.user.validated.group.UpdateUserValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private long id;
    @NotBlank(groups = {CreateUserValidation.class})
    private String name;
    @NotBlank(groups = {CreateUserValidation.class})
    @Email(groups = {CreateUserValidation.class, UpdateUserValidation.class})
    private String email;
}
