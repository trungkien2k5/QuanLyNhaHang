package com.kien.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kien.auth.dto.reponse.LoginResponse;
import com.kien.auth.dto.reponse.RefreshTokenResponse;
import com.kien.auth.dto.request.ForgotPasswordRequest;
import com.kien.auth.dto.request.LoginRequest;
import com.kien.auth.dto.request.RefreshTokenRequest;
import com.kien.auth.dto.request.RegisterRequest;
import com.kien.auth.dto.request.UpdateProfileRequest;
import com.kien.auth.service.AuthService;
import com.kien.auth.repository.NguoiDungRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import com.kien.auth.security.CustomUserDetailsService;
import com.kien.auth.security.JwtService;
import com.kien.auth.security.JwtFilter;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "JWT_SECRET=test-secret-key-for-unit-test-123456789"
})

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
     AuthService authService;

    @MockitoBean
     NguoiDungRepository nguoiDungRepository;

    @MockitoBean
     PasswordEncoder passwordEncoder;
    @MockitoBean
    JwtFilter jwtFilter;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setTenDangNhap("kien");
        request.setMatKhau("123456");

        LoginResponse response = new LoginResponse(
                "access-token-test",
                "refresh-token-test"
        );
        response.setAccessToken("access-token");
        response.setRefreshToken("refresh-token");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_invalidRequest_returns400() throws Exception {
        LoginRequest request = new LoginRequest();

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setTenDangNhap("kien");
        request.setMatKhau("123456");
        request.setHoTen("Nguyen Trung Kien");
        request.setEmail("kien@gmail.com");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequest.class));
    }



    @Test
    void refresh_success() throws Exception {

        RefreshTokenResponse response =
                new RefreshTokenResponse("access-token-test");

        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "refreshToken": "refresh-token-test"
                            }
                            """)
        ).andExpect(status().isOk());
    }



    @Test
    void logout_success() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        mockMvc.perform(
                        post("/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(authService)
                .logout(any(RefreshTokenRequest.class));
    }



    @Test
    void forgotPassword_success() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("kien@gmail.com");

        mockMvc.perform(
                        post("/auth/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        verify(authService)
                .forgotPassword(any(ForgotPasswordRequest.class));
    }
}
