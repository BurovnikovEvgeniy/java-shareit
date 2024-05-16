package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.CreateUserValidation;
import ru.practicum.shareit.utils.UpdateUserValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {CreateUserValidation.class})
    private String name;
    @NotBlank(groups = {CreateUserValidation.class})
    @Email(groups = {CreateUserValidation.class, UpdateUserValidation.class})
    private String email;
}
