package org.devkirby.hanimman.controller;

import lombok.extern.log4j.Log4j2;
import org.devkirby.hanimman.dto.UserDTO;
import org.devkirby.hanimman.entity.Gender;
import org.devkirby.hanimman.entity.Nickname;
import org.devkirby.hanimman.service.UserService;
import org.devkirby.hanimman.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Log4j2
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAndSignupOrLogin(
            @RequestBody ResultRequest resultData,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        // 필수 필드 검증
        if (resultData.getName() == null || resultData.getPhoneNumber() == null ||
                resultData.getBirthDate() == null || resultData.getGender() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Missing required fields."));
        }

        try {
            // ResultRequest -> UserDTO 변환
            UserDTO userDTO = JsonToDTO(resultData);
            log.info("UserDTO created from ResultRequest: " + userDTO);

            // 유저가 존재하는지 확인
            UserDTO existingUser = userService.selectUser(userDTO);

            if (existingUser == null || existingUser.getId() == null) {
                // 유저가 없으면 회원가입 진행
                String nickname = nicknameGenerator();
                String codeNum = generateUniqueCodenum();

                userDTO.setCodenum(codeNum);
                userDTO.setNickname(nickname);
                log.info("Generated nickname and codeNum for new user");

                // 유저 생성
                UserDTO savedUserDTO = userService.createUser(userDTO);
                log.info("New user created: " + savedUserDTO);

                // JWT 토큰 발행 및 응답
                return generateResponseWithToken(savedUserDTO, HttpStatus.CREATED, "Sign-up successful.");
            }

            // 로그인 성공 -> JWT 생성 및 응답
            log.info("Existing user found: " + existingUser);
            return generateResponseWithToken(existingUser, HttpStatus.OK, "Login successful.");

        } catch (IllegalArgumentException e) {
            log.error("Invalid input data", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid input data."));
        } catch (Exception e) {
            log.error("Error during verification, signup, or login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred during the process"));
        }
    }

    private ResponseEntity<SuccessResponse> generateResponseWithToken(UserDTO userDTO, HttpStatus status, String message) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "user");
        claims.put("username", userDTO.getName());
        claims.put("phoneNumber", userDTO.getPhonenum());

        try {
            String token = JWTUtil.generateToken(claims, userDTO.getId());
            log.info("Generated JWT Token: " + token);  // 토큰 생성 로그

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            log.info("Response Headers: " + headers.toString());  // 헤더 로그

            return ResponseEntity.status(status)
                    .headers(headers)
                    .body(new SuccessResponse(message));
        } catch (Exception e) {
            log.error("Error generating token or setting headers", e);
            throw new RuntimeException("Token generation or header setting failed", e);
        }
    }

    // ResultRequest to UserDTO 변환
    private UserDTO JsonToDTO(ResultRequest resultData) {
        Gender gender = resultData.getGender().equalsIgnoreCase("MALE") ? Gender.M : Gender.F;
        UserDTO userDTO = UserDTO.builder()
                .name(resultData.getName())
                .phonenum(resultData.getPhoneNumber())
                .gender(gender)
                .birth(LocalDate.parse(resultData.getBirthDate()))
                .build();
        log.info("Converted ResultRequest to UserDTO: " + userDTO);
        return userDTO;
    }

    public String nicknameGenerator() {
        Random random = new Random();
        Nickname[] nicknames = Nickname.values();
        Nickname selectNickname = nicknames[random.nextInt(nicknames.length)];
        String generatedNickname = selectNickname.toString().replace("_", " ");
        log.info("Generated nickname: " + generatedNickname);
        return generatedNickname;
    }

    public String generateUniqueCodenum() {
        String codeNum;
        do {
            codeNum = codenumGenerator();
        } while (userService.isExistCodeNum(codeNum));
        log.info("Generated unique codeNum: " + codeNum);
        return codeNum;
    }

    public static String codenumGenerator() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            code.append(characters.charAt(randomIndex));
        }

        String generatedCode = code.toString();
        log.info("Generated codeNum: " + generatedCode);
        return generatedCode;
    }

    // Error response DTO
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        // Getter
        public String getMessage() {
            return message;
        }
    }

    // Success response DTO
    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        // Getter
        public String getMessage() {
            return message;
        }
    }

    public static class ResultRequest {
        private String name;
        private String phoneNumber;
        private String birthDate;
        private String gender;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getBirthDate() { return birthDate; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
    }
}