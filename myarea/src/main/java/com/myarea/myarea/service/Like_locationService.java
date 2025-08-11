package com.myarea.myarea.service;

import com.myarea.myarea.dto.Like_locationDto;
import com.myarea.myarea.dto.Like_postDto;
import com.myarea.myarea.dto.SubLocationDto;
import com.myarea.myarea.entity.*;
import com.myarea.myarea.repository.Like_locationRepository;
import com.myarea.myarea.repository.Like_postRepository;
import com.myarea.myarea.repository.SubLocationRepository;
import com.myarea.myarea.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class Like_locationService {
    @Autowired
    private Like_locationRepository like_locationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubLocationRepository sublocationRepository;

    public List<SubLocationDto> getLike_location(Long user_id) {
        List<Long> sublocation_ids = like_locationRepository.findByUser_id(user_id)
                .stream()
                .map(like -> like.getSubloc_id().getSublocId())
                .collect(Collectors.toList());

        return sublocationRepository.findAllById(sublocation_ids).stream()
                .map(SubLocationDto::toDto)
                .collect(Collectors.toList());
    }

    public Like_locationDto addLike_location(Long user_id, Long sublocation_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new IllegalArgumentException("로그인 해야 합니다"));
        SubLocation sublocation = sublocationRepository.findById(sublocation_id).orElseThrow(() -> new IllegalArgumentException("해당 지역이 없습니다."));

        Like_locationDto dto = new Like_locationDto();
        Like_location like_location = Like_location.addLike_location(dto, user, sublocation);
        Like_location addLike_location = like_locationRepository.save(like_location);
        return Like_locationDto.toDto(addLike_location);
    }

    public Like_locationDto deleteLike_location(Long user_id, Long sublocation_id) {
        Like_location target = like_locationRepository.findById(user_id, sublocation_id);

        like_locationRepository.delete(target);
        return Like_locationDto.toDto(target);
    }
}
