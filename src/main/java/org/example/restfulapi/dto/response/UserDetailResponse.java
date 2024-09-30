package org.example.restfulapi.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.restfulapi.util.PhoneNumber;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class UserDetailResponse implements Serializable {
    private long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
}
