package org.devkirby.hanimman.controller;

import lombok.RequiredArgsConstructor;
import org.devkirby.hanimman.dto.FaqDTO;
import org.devkirby.hanimman.dto.FaqFileDTO;
import org.devkirby.hanimman.dto.FaqRequest;
import org.devkirby.hanimman.entity.User;
import org.devkirby.hanimman.service.FaqService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/faq")
@RequiredArgsConstructor
public class FaqController {
    private final FaqService faqService;

    @PostMapping
    public Map<String, Object > createFaq(@RequestBody FaqDTO faqDTO, @AuthenticationPrincipal User loginUser) throws IOException {
        Map<String, Object> map = new HashMap<>();
        if(faqDTO.getTitle().length() > 255 || faqDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("제목의 길이는 1자 이상, 255자 이하여야 합니다. 현재 길이: "
                    + faqDTO.getTitle().length());
        }else if (faqDTO.getContent().length() > 65535) {
            throw new IllegalArgumentException("내용의 길이는 65535자 이하여야 합니다. 현재 길이: "
                    + faqDTO.getContent().length());
        }else{
            faqDTO.setUserId(loginUser.getId());
            faqService.create(faqDTO);
            map.put("code", 200);
            map.put("msg", "자주 묻는 질문 작성에 성공했습니다.");
            return map;
        }
    }

    @GetMapping("/{id}")
    public FaqDTO readFaq(@PathVariable Integer id) {
        return faqService.read(id);
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateFaq(@PathVariable Integer id, @RequestBody FaqDTO faqDTO) throws IOException {
        Map<String, Object> map = new HashMap<>();
        faqDTO.setId(id);
        if(faqDTO.getTitle().length() > 255 || faqDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("제목의 길이는 1자 이상, 255자 이하여야 합니다. 현재 길이: "
                    + faqDTO.getTitle().length());
        }else if (faqDTO.getContent().length() > 65535) {
            throw new IllegalArgumentException("내용의 길이는 65535자 이하여야 합니다. 현재 길이: "
                    + faqDTO.getContent().length());
        }else{
            faqService.update(faqDTO);
            map.put("code", 200);
            map.put("msg", "자주 묻는 질문 수정에 성공했습니다.");
            return map;
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteFaq(@PathVariable Integer id, @AuthenticationPrincipal User loginUser) {
        Map<String, Object> map = new HashMap<>();
        if(!loginUser.getId().equals(faqService.read(id).getUserId())) {
            throw new IllegalArgumentException("자주묻는 질문 삭제는 관리자만 가능합니다.");
        }else {
            faqService.delete(id);
            map.put("code", 200);
            map.put("msg", "자주 묻는 질문 삭제에 성공했습니다.");
            return map;
        }
    }

    @GetMapping
    public Page<FaqDTO> listAllFaqs(@PageableDefault(size = 10) Pageable pageable) {
        return faqService.listAll(pageable);
    }

    @GetMapping("/search")
    public Page<FaqDTO> searchFaqs(@RequestParam String keyword, @PageableDefault(size = 10) Pageable pageable) {
        return faqService.searchByKeywords(keyword, pageable);
    }
}