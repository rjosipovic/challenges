package com.playground.user_manager.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

import java.time.LocalDate;

@Value
public class CreateUserDTO {

    @NotBlank
    String alias;
    @NotBlank
    @Email
    String email;
    @Past
    LocalDate birthdate;
    @Pattern(regexp = "male|female")
    String gender;

}
