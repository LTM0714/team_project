package com.myarea.myarea.service;


import com.myarea.myarea.entity.SubSubLocation;
import com.myarea.myarea.repository.SubSubLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubSubLocationService {
    @Autowired
    private SubSubLocationRepository subSubLocationRepository;

    // sublocId에 속한 구/군 목록 조회
    public List<SubSubLocation> findBySubLocId(Long sublocId) {
        return subSubLocationRepository.findBySublocation_SublocId(sublocId);
    }

    // subsubId로 구/군 하나 조회
    public SubSubLocation findById(Long subsubId) {
        return subSubLocationRepository.findById(subsubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 SubSubLocation ID"));
    }
}
