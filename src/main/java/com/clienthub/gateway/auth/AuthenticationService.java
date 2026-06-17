package com.clienthub.gateway.auth;

import com.clienthub.gateway.config.JwtService;
import com.clienthub.gateway.config.PasswordValidationService;
import com.clienthub.gateway.exception.custom.InvalidPasswordRequirementsException;
import com.clienthub.gateway.exception.custom.UserExistsException;
import com.clienthub.gateway.exception.custom.UserNotFoundException;
import com.clienthub.gateway.ipapi.IPApiResponse;
import com.clienthub.gateway.ipapi.IPApiService;
import com.clienthub.gateway.user.User;
import com.clienthub.gateway.user.UserRepository;
import com.clienthub.gateway.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordValidationService passwordValidationService;
    private final IPApiService ipApiService;
    private final ValidationUtils validationUtils;
    private final WebClient webClient;

    @Value("${app.geolocation.enabled}")
    private boolean geolocationEnabled;

    public AuthenticationResponse register(RegisterRequest request, String userIpAddress) {
        log.info("register :: initiating registration for user [{}], ip [{}]", request.getUsername(), userIpAddress);

        if (geolocationEnabled) {
            IPApiResponse response = ipApiService.ipAPICall(userIpAddress);
            validationUtils.isAuthorizeToRegister(response.getCountry());
        }

        validateIfUserExists(request.getEmail());
        validatePassword(request.getPassword());

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User savedUser = userRepository.save(user);
        log.info("register :: user persisted successfully, userId [{}]", savedUser.getId());
        // TODO: re-enable when Client Management API is available
        // webClient.post()
        //         .uri("/api/v1/customers")
        //         .bodyValue(Map.of(
        //             "appUserId", savedUser.getId(),
        //             "firstName", request.getFirstName(),
        //             "lastName", request.getLastName(),
        //             "username", request.getUsername(),
        //             "email", request.getEmail(),
        //             "age", request.getAge(),
        //             "phoneNumber", request.getPhoneNumber()
        //         ))
        //         .retrieve()
        //         .toBodilessEntity()
        //         .block();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    // login
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("authenticate :: attempting authentication for user [{}]", request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("The user %s could not be found".formatted(request.getUsername())));
        var jwtToken = jwtService.generateToken(user);
        log.info("authenticate :: authentication successful for user [{}]", request.getUsername());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void validatePassword(String password) {
        if (!passwordValidationService.isPasswordValid(password)) {
            throw new InvalidPasswordRequirementsException("Invalid password requirements.");
        }
    }

    private void validateIfUserExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserExistsException("This username is already taken.");
        }
    }

}
