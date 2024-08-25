package org.example.restfulapi.service;

import org.example.restfulapi.dto.request.UserRequestDTO;

public interface UserService {
    int addUser(UserRequestDTO requestDTO);
}
