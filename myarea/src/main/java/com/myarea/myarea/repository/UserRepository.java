package com.myarea.myarea.repository;

import com.myarea.myarea.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인 email을 갖는 객체가 존재하는지 => 존재하면 true 리턴
    boolean existsByEmail(String email);

    // 로그인 email을 갖는 객체 반환
    User findByEmail(String email);
}