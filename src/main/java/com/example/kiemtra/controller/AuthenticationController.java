package com.example.kiemtra.controller;


import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.request.AuthenticationRequest;
import com.example.kiemtra.dto.request.IntrospectRequest;
import com.example.kiemtra.dto.request.LogoutRequest;
import com.example.kiemtra.dto.request.RefreshRequest;
import com.example.kiemtra.dto.response.AuhthenticationResponse;
import com.example.kiemtra.dto.response.IntrospectResponse;
import com.example.kiemtra.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/token")
    ApiResponse<AuhthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
       return ApiResponse.<AuhthenticationResponse>builder()
               .result(authenticationService.authenticate(request))
               .build();
    }

    @PostMapping("/introspect")
        ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
            var result = authenticationService.introspect(request);
            return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuhthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuhthenticationResponse>builder().result(result).build();
    }
}
