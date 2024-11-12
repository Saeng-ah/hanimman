package org.devkirby.hanimman.controller;

import lombok.RequiredArgsConstructor;
import org.devkirby.hanimman.dto.TogetherDTO;
import org.devkirby.hanimman.dto.TogetherImageDTO;
import org.devkirby.hanimman.dto.TogetherRequest;
import org.devkirby.hanimman.service.TogetherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/together")
@RequiredArgsConstructor
public class TogetherController {
    private final TogetherService togetherService;

    @PostMapping
    public void createTogether(@RequestBody TogetherRequest togetherRequest) {
        togetherService.create(togetherRequest.getTogetherDTO(), togetherRequest.getTogetherImageDTO());
    }

    @GetMapping("/{id}")
    public TogetherDTO readTogether(@PathVariable Integer id) {
        return togetherService.read(id);
    }

    @PutMapping("/{id}")
    public void updateTogether(@PathVariable Integer id, @RequestBody TogetherDTO togetherDTO) {
        togetherDTO.setId(id);
        togetherService.update(togetherDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteTogether(@PathVariable Integer id) {
        togetherService.delete(id);
    }

    @GetMapping
    public Page<TogetherDTO> listAllTogethers(@PageableDefault(size = 10)Pageable pageable) {
        return togetherService.listAll(pageable);
    }

    @GetMapping("/search")
    public Page<TogetherDTO> searchTogethers(@RequestParam String keyword, @PageableDefault(size = 10) Pageable pageable) {
        return togetherService.searchByKeywords(keyword, pageable);
    }
}
