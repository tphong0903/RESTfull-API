package org.example.restfulapi.dto.request;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.example.restfulapi.util.EnumPattern;
import org.example.restfulapi.util.PhoneNumber;
import org.example.restfulapi.util.UserStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class UserRequestDTO implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    private String fisrtName;
    @NotNull(message = "lastName must be not null")
    private String lastName;
    @PhoneNumber
    private String phone;
    @Email(message = "Email invalid format")
    private String email;
    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @EnumPattern(name = "status",regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    public UserRequestDTO() {
    }
    public UserRequestDTO(String fisrtName, String lastName, String phone, String email) {
        this.fisrtName = fisrtName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public void setFisrtName(String fisrtName) {
        this.fisrtName = fisrtName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
