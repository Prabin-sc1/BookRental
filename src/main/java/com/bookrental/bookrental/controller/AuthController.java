package com.bookrental.bookrental.controller;

import com.bookrental.bookrental.config.CustomMessageSource;
import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.exception.AppException;
import com.bookrental.bookrental.generic.GlobalApiResponse;
import com.bookrental.bookrental.jwt.JwtHelper;
import com.bookrental.bookrental.pojo.jwt.JwtRequestPojo;
import com.bookrental.bookrental.pojo.jwt.JwtResponsePojo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = ModuleNameConstants.AUTHENTICATION)
public class AuthController extends MyBaseController {
    private final UserDetailsService userDetailsService;

    private final AuthenticationManager manager;

    private final JwtHelper helper;

    private final CustomMessageSource customMessageSource;

    public AuthController(UserDetailsService userDetailsService, AuthenticationManager manager,
                          JwtHelper helper, CustomMessageSource customMessageSource) {
        this.userDetailsService = userDetailsService;
        this.manager = manager;
        this.helper = helper;
        this.customMessageSource = customMessageSource;
        this.module = ModuleNameConstants.AUTHENTICATION;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Generate token",
            description = "This end point used to generate token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success",
                            content = {
                                    @Content(schema = @Schema(implementation = JwtResponsePojo.class))
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<GlobalApiResponse> login(@RequestBody JwtRequestPojo request) {
        this.doAuthenticate(request.getEmail(), request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        JwtResponsePojo response = JwtResponsePojo.builder()
                .jwtToken(token)
                .username(userDetails.getUsername()).build();
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.TOKEN_GENERATE.getCode()),
                response));
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new AppException(customMessageSource.get(Message.CREDENTIAL_INVALID.getCode()));
        }
    }
}
