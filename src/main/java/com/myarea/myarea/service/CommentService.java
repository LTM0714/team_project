package com.myarea.myarea.service;

import com.myarea.myarea.dto.CommentDto;
import com.myarea.myarea.entity.Comment;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.entity.UserRole;
import com.myarea.myarea.repository.CommentRepository;
import com.myarea.myarea.repository.PostRepository;
import com.myarea.myarea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public List<CommentDto> comments(Long postId) {
        /*// 1. 댓글 조회
        List<Comment> comments = commentRepository.findByPostId(postId);
        // 2. 엔티티 -> dto 변환
        List<CommentDto> dtos = new ArrayList<CommentDto>();
        for (int i = 0; i < comments.size(); i++) {
            Comment c = comments.get(i);
            CommentDto dto = CommentDto.createCommentDto(c);
            dtos.add(dto);
        }*/
        // 3. 결과 반환
        return commentRepository.findByPostId(postId)
                .stream() //댓글 엔티티 목록을 스트림으로 변환
                .map(comment -> CommentDto.createCommentDto(comment)) //엔티티를 DTO로 매핑
                .collect(Collectors.toList()); //스트림을 리스트로 변환
    }

    @Transactional
    public CommentDto create(Long postId, CommentDto dto, Long userId) {
        // 1. 게시글 조회 및 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! 대상 게시글이 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! 로그인 해야 합니다"));
        // 2. 댓글 엔티티 생성
        Comment comment = Comment.createComment(dto, post, user);
        // 3. 댓글 엔티티를 DB에 저장
        Comment created = commentRepository.save(comment);
        // 4. DTO로 변환해 반환
        return CommentDto.createCommentDto(created);
    }

    @Transactional
    public CommentDto update(Long id, CommentDto dto, Long userId) {
        User loginUser = userService.getLoginUserById(userId);

        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 수정 실패! 대상 댓글이 없습니다."));
        // 2. 댓글 수정
        target.patch(dto, loginUser, target);
        // 3. DB로 갱신
        Comment updated = commentRepository.save(target);
        // 4. 댓글 엔티티를 DTO로 변환 및 반환
        return CommentDto.createCommentDto(updated);
    }

    @Transactional
    public CommentDto delete(Long id, Long userId) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 삭제 실패! 대상이 없습니다."));
        // 1-1. 로그인 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! 로그인 해야 합니다"));
        // 1-2. 사용자 권한 확인
        if(!target.getUser().getId().equals(user.getId())){
            if(!user.getUserRole().equals(UserRole.ADMIN)){
                throw new IllegalArgumentException(("댓글 생성 실패! 댓글 삭제 권한이 없습니다."));
            }
        }
        // 2. 댓글 삭제
        commentRepository.delete(target);
        // 3. 삭제 댓글을 DTO로 변환 및 반환
        return CommentDto.createCommentDto(target);
    }
}