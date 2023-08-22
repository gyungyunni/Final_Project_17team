package com.example.final_project_17team.service;

import com.example.final_project_17team.dto.UserDto;
import com.example.final_project_17team.entity.User;
import com.example.final_project_17team.exception.ErrorCode;
import com.example.final_project_17team.exception.UserException;
import com.example.final_project_17team.jwt.JwtRequestDto;
import com.example.final_project_17team.jwt.JwtTokenDto;
import com.example.final_project_17team.jwt.JwtTokenUtils;
import com.example.final_project_17team.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtils jwtTokenUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;

        createUser(UserDto.builder()
                .username("user1")
                .password(passwordEncoder.encode("1234"))
                .email("user1@gmail.com")
                .phone("010-0000-0000")
                .gender(true)
                .age(Long.valueOf(20))
                .created_at(LocalDateTime.now())
                .build());
    }

    public JwtTokenDto loginUser(JwtRequestDto dto) {
        UserDto user = this.loadUserByUsername(dto.getUsername());
        log.info(user.getUsername());
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new BadCredentialsException(dto.getUsername());

        JwtTokenDto response = new JwtTokenDto();
        response.setToken(jwtTokenUtils.generateToken(user));
        return response;
    }

    public void createUser(UserDto user) {
        if (this.userExists(user.getUsername()))
            throw new UserException(ErrorCode.DUPLICATED_USER_NAME, String.format("Username : ", user.getUsername()));

        try {
            this.userRepository.save(user.newEntity());
        } catch (ClassCastException e) {
            log.error("failed to cast to {}", UserDto.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDto loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) throw new UsernameNotFoundException(username);

        log.info(optionalUser.get().getUsername());
        UserDto dto = UserDto.fromEntity(optionalUser.get());
        log.info("dto " + dto.getUsername());
        return dto;
    }


    @Override
    public void createUser(UserDetails user) {

    }
}