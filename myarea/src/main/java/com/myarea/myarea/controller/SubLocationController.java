package com.myarea.myarea.controller;

import com.myarea.myarea.dto.SubLocationDto;
import com.myarea.myarea.dto.SubSubLocationDto;
import com.myarea.myarea.entity.SubLocation;
import com.myarea.myarea.repository.SubLocationRepository;
import com.myarea.myarea.repository.SubSubLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sublocations")
@RequiredArgsConstructor
public class SubLocationController {
    @Autowired
    private SubLocationRepository subLocationRepository;
    @Autowired
    private SubSubLocationRepository subSubLocationRepository;

    // 시/도 전체 조회
    @GetMapping
    public List<SubLocation> getAllSublocations() {
        return subLocationRepository.findAll();
    }

    // 해당 시/도의 구/군 목록 조회
    @GetMapping("/{sublocId}/subsublocations")
    public List<SubSubLocationDto> getSubSubLocations(@PathVariable Long sublocId) {
        return subSubLocationRepository.findBySublocation_SublocId(sublocId)
                .stream()
                .map(ss -> new SubSubLocationDto(
                        ss.getSubsubId(),
                        ss.getAddress(),
                        ss.getLatitude(),
                        ss.getLongitude()))
                .collect(Collectors.toList());
    }
}

