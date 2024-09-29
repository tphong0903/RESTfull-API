package org.example.restfulapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module;
import org.example.restfulapi.config.Translator;
import org.example.restfulapi.dto.request.UserRequestDTO;
import org.example.restfulapi.dto.response.PageResponse;
import org.example.restfulapi.dto.response.ResponseData;
import org.example.restfulapi.dto.response.ResponseError;
import org.example.restfulapi.dto.response.UserDetailResponse;
import org.example.restfulapi.exception.ResourceNotFoundException;
import org.example.restfulapi.service.UserService;
import org.example.restfulapi.util.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private  final PasswordEncoder passwordEncoder;

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping("/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO userDTO)
    {
        try{
            log.info("Request add user, {}, {}",userDTO.getFirstName(),userDTO.getLastName());
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            long userId = userService.saveUser(userDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"),userId);
        }
        catch (Exception e){
            log.error("errorMessage={}",e.getMessage(),e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Save failed");
        }
    }
    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userID}")
    public ResponseData<String> updateUser(@PathVariable("userID") @Min(value = 1, message = "userId must be greater than 0")  long id,@Valid @RequestBody UserRequestDTO userDTO)
    {
        try{
            userService.updateUser(id,userDTO);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(),Translator.toLocale("user.upd.success"));
        }
        catch (Exception e) {
            log.info("errorMessage={}",e.getMessage(),e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Update failed");
        }
    }
    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userID}")
    public ResponseData<?> changeStatus(@PathVariable("userID") @Min(value = 1, message = "userId must be greater than 0") int id,@RequestParam UserStatus status)
    {
        try{
            userService.changeStatus(id,status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(),Translator.toLocale("user.upd.success"));
        }
        catch (Exception e) {
            log.info("errorMessage={}",e.getMessage(),e.getCause());
            return new ResponseData<>(HttpStatus.ACCEPTED.value(),"User changed status successfully");
        }

    }
    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userID}")
    public ResponseData<?> deleteUser(@PathVariable("userID") @Min(value = 1, message = "userId must be greater than 0")int id)
    {
        if(id == 1)
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(),"User deleted successfully");
        else
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Cant delete");

    }
    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userID}")
    public ResponseData<UserDetailResponse> getUser(@PathVariable("userID")  @Min(value = 1, message = "userId must be greater than 0") int id)
    {
        try {
            return new ResponseData<>(HttpStatus.OK.value(),"Get user successfully",userService.getUser(id));
        }
        catch (ResourceNotFoundException e){
            log.info("errorMessage={}",e.getMessage(),e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),e.getMessage());
        }

    }
    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")    @GetMapping("/list")
    public ResponseData<?> getAllUserWithSortBy(@RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "pageNo must be greater than 1")  int pageNo,
                                                                       @Valid @Min(10) @RequestParam(defaultValue = "1", required = false) int pageSize,
                                                                       @RequestParam(required = false) String sortBy
    ) {
        log.info("Request all users");
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user successfully",userService.getAllUsersWithSortBy(pageNo,pageSize,sortBy));

    }
    @Operation(summary = "Get list of users with sort by multiple columns", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<?> getAllUsersWithSortByMultipleColumns(@RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "pageNo must be greater than 1")  int pageNo,
                                                             @Valid @Min(10) @RequestParam(defaultValue = "1", required = false) int pageSize,
                                                             @RequestParam(required = false) String... sorts
    ) {
        log.info("Request get list of users with sort by multiple columns");
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user successfully",userService.getAllUsersWithSortByMultipleColumns(pageNo,pageSize,sorts));

    }
    @Operation(summary = "Get list of users with sort by column and search", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/list-with-sort-by-column-and-search")
    public ResponseData<?> getAllUsersWithSortByColumnAndSearch(@RequestParam(defaultValue = "1", required = false) @Min(value = 1, message = "pageNo must be greater than 1")  int pageNo,
                                                                @Valid @Min(10) @RequestParam(defaultValue = "1", required = false) int pageSize,
                                                                @RequestParam(required = false) String search,
                                                                @RequestParam(required = false) String sort
    ) {
        log.info("Request get list of users with sort by multiple columns");
        return new ResponseData<>(HttpStatus.OK.value(), "Get all user successfully",userService.getAllUsersWithSortByColumnAndSearch(pageNo,pageSize,sort,search));

    }
    @Operation(summary = "Advance search query by criteria", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/advance-search-with-criteria")
    public ResponseData<?> advanceSearchWithCriteria(@RequestParam(defaultValue = "1", required = false) int pageNo,
                                                     @RequestParam(defaultValue = "10", required = false) int pageSize,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String address,
                                                     @RequestParam(defaultValue = "") String... search) {
        log.info("Request advance search query by criteria");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.advanceSearchWithCriteria(pageNo, pageSize, sortBy, address, search));
    }


}
