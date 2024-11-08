package org.devkirby.hanimman.service;

import lombok.RequiredArgsConstructor;
import org.devkirby.hanimman.dto.ShareDTO;
import org.devkirby.hanimman.entity.Share;
import org.devkirby.hanimman.repository.ShareRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final ShareRepository shareRepository;
    private final ModelMapper modelMapper;

    @Override
    public void create(ShareDTO shareDTO) {
        Share share = modelMapper.map(shareDTO, Share.class);
        Share result = shareRepository.save(share);
    }

    @Override
    public ShareDTO read(Integer id) {
        Share result = shareRepository.findById(id).orElseThrow();
        return modelMapper.map(result, ShareDTO.class);
    }

    @Override
    public void update(ShareDTO shareDTO) {
        shareDTO.setModifiedAt(Instant.now());
        Share share = modelMapper.map(shareDTO, Share.class);
        shareRepository.save(share);
    }

    @Override
    public void delete(Integer id) {
        Share share = shareRepository.findById(id).orElseThrow();
        share.setDeletedAt(Instant.now());
        shareRepository.save(share);
    }

    @Override
    public Page<ShareDTO> listAll(Pageable pageable) {
        return shareRepository.findAll(pageable)
                .map(share -> modelMapper.map(share, ShareDTO.class));
    }
}
