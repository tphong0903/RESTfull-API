package org.example.restfulapi.service.Impl;

import org.example.restfulapi.dto.request.UserRequestDTO;
import org.example.restfulapi.exception.ResourceNotFoundException;
import org.example.restfulapi.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public int addUser(UserRequestDTO requestDTO) {
        System.out.println("Save to db");
        if(requestDTO.getFisrtName().equals("Han"))
            throw new ResourceNotFoundException("User isnt exsist");
        return 0;
    }
}
