package com.bookrental.bookrental.controller;

import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.generic.GlobalApiResponse;
import com.bookrental.bookrental.pojo.ChangePasswordRequest;
import com.bookrental.bookrental.pojo.EmailRequest;
import com.bookrental.bookrental.pojo.VerifyOTPRequest;
import com.bookrental.bookrental.pojo.user.UserRequestPojo;
import com.bookrental.bookrental.pojo.user.UserResponsePojo;
import com.bookrental.bookrental.service.otp.OTPService;
import com.bookrental.bookrental.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/user")
@Tag(name = ModuleNameConstants.USER)
public class UserController extends MyBaseController {
    private final UserService userService;

    private final OTPService otpService;

    public UserController(UserService userService, OTPService otpService) {
        this.userService = userService;
        this.otpService = otpService;
        this.module = ModuleNameConstants.USER;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Create and update user",
            description = "This end point is used to create and update user",
            responses = @ApiResponse(
                    responseCode = "200", description = "success",
                    content = {
                            @Content(schema = @Schema(implementation = UserRequestPojo.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> create(@RequestBody @Valid UserRequestPojo user) {
        userService.createUser(user);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.SAVE.getCode(), module), null));
    }

    @PostMapping("/change-password")
    @PreAuthorize("permitAll()")
    @Operation(summary = "update password",
            description = "This end point is used to update password. ",
            responses = @ApiResponse(

                    responseCode = "200", description = "success",
                    content = {
                            @Content(schema = @Schema(implementation = ChangePasswordRequest.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        Boolean success = userService.changePassword(request.getOldPassword(), request.getNewPassword(), principal);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.PASSWORD_UPDATE.getCode(), module), success));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Retrieve all user",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = UserResponsePojo.class)))},
                            description = "This end point fetch all user"
                    )
            }
    )
    public ResponseEntity<GlobalApiResponse> getAll() {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIEVE.getCode(), module), userService.getAllUser()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Get user by id",
            description = "This end point can be used for getting user by id",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = UserResponsePojo.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> getSingleUser(@PathVariable Integer id) {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIEVE.getCode(), module),
                userService.getUserById(id)));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Delete user",
            description = "This end point used to delete user",
            responses = @ApiResponse(responseCode = "200"
            )
    )
    public ResponseEntity<GlobalApiResponse> delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.DELETE.getCode(), module), null
        ));
    }


    @GetMapping("/download-excel-data")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Retrieve all users in excel",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = UserResponsePojo.class)))},
                            description = "This end point fetch all users"
                    )
            }
    )
    public ResponseEntity<Resource> download() throws IOException {
        String fileName = "user.xlsx";
        ByteArrayInputStream bis = userService.getExcelData();
        InputStreamResource file = new InputStreamResource(bis);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<GlobalApiResponse> sendOTP(@RequestBody EmailRequest request) {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.GENERATE_OTP.getCode(), module),
                otpService.generateAndStore(request.getEmail())));
    }

    @PostMapping("/change-password-otp")
    @PreAuthorize("permitAll()")
    public ResponseEntity<GlobalApiResponse> verifyOTP(@RequestBody VerifyOTPRequest request) {
        if (otpService.verifyOTP(request.getEmail(), request.getOtp(), request.getNewPassword())) {
            return ResponseEntity.ok(successResponse(customMessageSource.get(Message.VERIFY_OTP.getCode(), module),
                    null));
        } else {
            return ResponseEntity.ok(successResponse(customMessageSource.get(Message.NOT_GENERATE_OTP.getCode(), module),
                    null
            ));
        }
    }
}
