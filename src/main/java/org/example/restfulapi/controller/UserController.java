package org.example.restfulapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.example.restfulapi.config.Translator;
import org.example.restfulapi.dto.request.UserRequestDTO;
import org.example.restfulapi.dto.response.ResponseData;
import org.example.restfulapi.dto.response.ResponseError;
import org.example.restfulapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Add user",description = "Add new user")
    @PostMapping("/")
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO)
    {
        try{
            log.info("Request add user, {}, {}",userDTO.getFisrtName(),userDTO.getLastName());
            userService.addUser(new UserRequestDTO("Phong","Le","081","phongga088"));
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"),1);
        }
        catch (Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Save failed");
        }
    }
    @PutMapping("/{userID}")
    public ResponseData<String> updateUser(@PathVariable("userID") @Min(value = 1, message = "userId must be greater than 0")  int id,@Valid @RequestBody UserRequestDTO userDTO)
    {
        return new ResponseData<>(HttpStatus.ACCEPTED.value(),"User updated successfully");
    }
    @PatchMapping("/{userID}")
    public ResponseData<?> changeStatus(@PathVariable("userID") @Min(value = 1, message = "userId must be greater than 0") int id,@RequestParam boolean status)
    {
        return new ResponseData<>(HttpStatus.ACCEPTED.value(),"User changed status successfully");
    }
    @DeleteMapping("/{userID}")
    public ResponseData<?> deleteUser(@PathVariable("userID") @Min(value = 1, message = "userId must be greater than 0")int id)
    {
        if(id == 1)
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(),"User deleted successfully");
        else
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Cant delete");

    }
    @GetMapping("/{userID}")
    public ResponseData<UserRequestDTO> getUser(@PathVariable("userID")  @Min(value = 1, message = "userId must be greater than 0") int id)
    {
        return new ResponseData<>(HttpStatus.OK.value(),"User deleted successfully",new UserRequestDTO("Phong","Le","081","phongga088"));

    }
    @Operation(summary = "Get list user per page",description = "Return user by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<List<UserRequestDTO>> getAllUser(@RequestParam(defaultValue = "0", required = false) int pageNo,@Valid @Min(10) @RequestParam(defaultValue = "1", required = false) int pageSize) {

        List<UserRequestDTO> list = new ArrayList<>();
        list.add(new UserRequestDTO("Phong","Le","081","phongga088"));
        list.add(new UserRequestDTO("Quang","Nguyen","090","quangga088"));
        list.add(new UserRequestDTO("Han","Nguyen","099","hanga088"));

        return new ResponseData<>(HttpStatus.OK.value(), "User deleted successfully",list);
    }


}
