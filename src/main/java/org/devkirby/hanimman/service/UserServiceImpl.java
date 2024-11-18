package org.devkirby.hanimman.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.devkirby.hanimman.dto.UserDTO;
import org.devkirby.hanimman.entity.User;
import org.devkirby.hanimman.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Log4j2
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // 회원 생성
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        try {
            User user = modelMapper.map(userDTO, User.class);
            User savedUser = userRepository.save(user);
            log.info("User saved: " + savedUser);
            return modelMapper.map(savedUser, UserDTO.class); // 저장된 사용자 정보를 DTO로 변환하여 반환
        } catch (Exception e) {
            log.error("Error during user creation", e);
            throw new RuntimeException("회원가입 중 오류가 발생했습니다.", e);
        }
    }


    // 회원조회
    public UserDTO selectUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        Optional<User> opt = userRepository.findByNameAndPhonenumAndGenderAndBirth(userDTO.getName(), userDTO.getPhonenum(), userDTO.getGender(), userDTO.getBirth());
        User savedUser = new User();
        if (opt.isPresent()) {
            savedUser = opt.get();
        }
        return modelMapper.map(savedUser, UserDTO.class);
    }

    // 회원수정
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        Optional<User> opt = userRepository.findById(user.getId());
        if (opt.isPresent()) {
            User existingUser = opt.get();

            // 기존 사용자 정보 업데이트
            existingUser.setName(user.getName());
            existingUser.setNickname(user.getNickname());
            existingUser.setPrimaryAddress(user.getPrimaryAddress());
            existingUser.setSecondlyAddress(user.getSecondlyAddress());
            existingUser.setDeviceUnique(user.getDeviceUnique());

            // 업데이트된 사용자 저장
            User updatedUser = userRepository.save(existingUser);
            return modelMapper.map(updatedUser, UserDTO.class);
        } else {
            System.out.println("회원를 찾을수 없습니다" + userDTO.getId());
            return null;
            // throw new UserNotFoundException("회원을 찾을 수 없습니다: " + userDTO.getId());
        }
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getId() == null) {
            throw new IllegalArgumentException("userDTO가 없거나 ID가 없을 경우");
        }

        // 사용자 ID로 조회
        Optional<User> opt = userRepository.findById(userDTO.getId());

        if (opt.isPresent()) {
            User user = opt.get();

            // 탈퇴일 확인
            if (user.getDeletedAt() != null) {
                // 이미 탈퇴한 사용자 처리
                System.out.println("이 사용자는 이미 탈퇴한 상태입니다: " + userDTO.getId());
            } else {

                user.setDeletedAt(Instant.now());
                userRepository.save(user); // 변경된 사용자 정보를 저장
                System.out.println("회원을 성공적으로 탈퇴하였습니다: " + userDTO.getId());
            }
        } else {
            // 사용자를 찾지 못한 경우
            System.out.println("회원을 찾을 수 없습니다: " + userDTO.getId());
        }
    }

    @Override
    public boolean isExistCodeNum(String codenum) {
        return userRepository.existsByCodenum(codenum);
    }
}