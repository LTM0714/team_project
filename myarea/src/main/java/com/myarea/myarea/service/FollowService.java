package com.myarea.myarea.service;

import com.myarea.myarea.dto.FollowDto;
import com.myarea.myarea.dto.FollowUserDto;
import com.myarea.myarea.dto.SignupRequestDto;
import com.myarea.myarea.entity.Follow;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.repository.FollowRepository;
import com.myarea.myarea.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;

    public List<FollowUserDto> getFollow(Long user_id) {
        List<Long> user_ids = followRepository.findByUser_id(user_id)
                .stream()
                .map(id -> id.getFollowed_user_id().getId())
                .collect(Collectors.toList());

        return userRepository.findAllById(user_ids).stream()
                .map(FollowUserDto::toDto)
                .collect(Collectors.toList());
    }

    public FollowDto addFollow(Long user_id, Long followed_user_id) {
        if(user_id.equals(followed_user_id)) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        User myuser = userRepository.findById(user_id).orElseThrow(() -> new IllegalArgumentException("로그인 해야 합니다"));
        User followed_user = userRepository.findById(followed_user_id).orElseThrow(() -> new IllegalArgumentException("대상이 없습니다."));

        FollowDto dto = new FollowDto();
        Follow follow = Follow.addFollow(dto, myuser, followed_user);
        Follow addFollow = followRepository.save(follow);
        return FollowDto.toDto(addFollow);
    }

    public FollowDto deleteFollow(Long user_id, Long followed_user_id) {
        Follow target = followRepository.findById(user_id, followed_user_id);

        followRepository.delete(target);
        return FollowDto.toDto(target);
    }
}
