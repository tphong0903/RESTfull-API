package org.example.restfulapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.restfulapi.util.Platform;

import java.io.Serializable;
@Getter
public class SignInRequest implements Serializable {
    @NotBlank(message = "username must not be blank")
    private String username;
    @NotBlank(message = "password must not be blank")
    private String password;
    @NotNull(message = "platform must not be null")
    private Platform platform;
    private String deviceToken;

    private String version;
}
