package org.devkirby.hanimman.service;

import org.devkirby.hanimman.dto.ShareDTO;
import org.devkirby.hanimman.dto.TogetherDTO;
import org.devkirby.hanimman.dto.TogetherImageDTO;
import org.devkirby.hanimman.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TogetherService {
    void create(TogetherDTO togetherDTO, TogetherImageDTO togetherImageDTO);
    TogetherDTO read(Integer id, User loginUser);
    void update(TogetherDTO togetherDTO);
    void delete(Integer id);

    Page<TogetherDTO> listAll(Pageable pageable);;

    Page<TogetherDTO> searchByKeywords(String keyword, Pageable pageable);

    Page<TogetherDTO> listNotEnd(Pageable pageable);
}
