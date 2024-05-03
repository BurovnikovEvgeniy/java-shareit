package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
}
