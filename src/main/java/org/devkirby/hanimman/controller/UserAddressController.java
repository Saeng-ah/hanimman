package org.devkirby.hanimman.controller;

import org.devkirby.hanimman.config.CustomUserDetails;
import org.devkirby.hanimman.dto.ResponseUserAddressDTO;
import org.devkirby.hanimman.dto.UserAddressDTO;
import org.devkirby.hanimman.entity.Address;
import org.devkirby.hanimman.entity.UserAddress;
import org.devkirby.hanimman.service.AddressService;
import org.devkirby.hanimman.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;
    private CustomUserDetails customUserDetails;
    @Autowired
    private AddressService addressService;

    // 첫번째 주소 생성
    @PostMapping("/save")
    public ResponseEntity<UserAddressDTO> saveUserAddress(@RequestBody UserAddressDTO userAddressDTO, @AuthenticationPrincipal CustomUserDetails loginUser) {
        System.out.println(loginUser);
        userAddressDTO.setUserId(loginUser.getId());
        UserAddressDTO savedAddress = userAddressService.saveUserAddress(userAddressDTO);

//        System.out.println(savedAddress + "무슨값이 있나요??");
        return ResponseEntity.ok(savedAddress);
    }

    // 두 번째 주소 저장
    @PostMapping("/save/secondary")
    public ResponseEntity<UserAddressDTO> saveSecondaryUserAddress(@RequestBody UserAddressDTO userAddressDTO, @AuthenticationPrincipal CustomUserDetails loginUser) {
        System.out.println(loginUser + "???????????????");
        userAddressDTO.setUserId(loginUser.getId());
        UserAddressDTO savedAddress = userAddressService.saveSecondaryUserAddress(userAddressDTO);
        System.out.println(savedAddress + "무슨값이 있음?");
        return ResponseEntity.ok(savedAddress);
    }

    //주소 조회
    @GetMapping("/select")
    public ResponseEntity<ResponseUserAddressDTO> getUserAddress(@AuthenticationPrincipal CustomUserDetails loginUser) {
        System.out.println("-----------------------유저 address select" + loginUser);
        Optional<UserAddressDTO> addresses = userAddressService.getUserAddress(loginUser.getId());
        UserAddressDTO userAddressDTO = addresses.orElseThrow();
        //주소명 불러오기
       String primaryAddressName = userAddressService.selectUserAddressName(userAddressDTO.getPrimaryAddressId());
       String secondAddressName = userAddressService.selectUserAddressName(userAddressDTO.getSecondlyAddressId());

        ResponseUserAddressDTO responseUserAddressDTO = ResponseUserAddressDTO.builder()
                .primaryAddressId(userAddressDTO.getPrimaryAddressId())
                .secondlyAddressId(userAddressDTO.getSecondlyAddressId())
                .primaryAddressName(primaryAddressName)
                .secondAddressName(secondAddressName)
                .build();

        return ResponseEntity.ok(responseUserAddressDTO);
    }

    // 주소 수정
    @PutMapping("/update")
    public ResponseEntity<UserAddressDTO> updateUserAddress(
            @RequestBody UserAddressDTO userAddressDTO,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        // 유효성 검사: ID가 null인지 확인
        if (userAddressDTO.getId() == null) {
            return ResponseEntity.badRequest().body(null); // ID가 없으면 400 Bad Request
        }

        // userAddressService를 사용하여 주소를 가져옴
        UserAddressDTO existingAddress = userAddressService.getUserAddress(userAddressDTO.getId())
                .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다."));

        // 주소 업데이트 서비스 호출
        UserAddressDTO updatedAddress = userAddressService.updateUserAddress(userAddressDTO);

        // 업데이트된 주소 반환
        return ResponseEntity.ok(updatedAddress);
    }
}



    // 주소 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUserAddress(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails loginUser) {
//        userAddressService.deleteUserAddress(id);
//        return ResponseEntity.noContent().build();
//    }
//}
