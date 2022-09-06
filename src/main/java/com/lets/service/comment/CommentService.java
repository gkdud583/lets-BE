package com.lets.service.comment;

import com.lets.domain.comment.Comment;
import com.lets.domain.comment.CommentRepository;
import com.lets.domain.post.Post;
import com.lets.domain.post.PostRepository;
import com.lets.domain.user.User;
import com.lets.domain.user.UserRepository;
import com.lets.exception.CustomException;
import com.lets.exception.ErrorCode;
import com.lets.security.UserPrincipal;
import com.lets.web.dto.comment.CommentResponseDto;
import com.lets.web.dto.comment.CommentSaveRequestDto;
import com.lets.web.dto.comment.CommentUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //댓글 저장
    @Transactional
    public Comment save(UserPrincipal principal, Long postId, CommentSaveRequestDto commentSaveRequestDto){
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.createComment(user, post, commentSaveRequestDto.getContent());
        Comment savedComment = commentRepository.save(comment);
        return savedComment;
    }

    //댓글 수정
    @Transactional
    public Comment update(Long commentId, CommentUpdateRequestDto commentUpdateRequestDto){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        commentUpdateRequestDto.changeComment(comment);
        return comment;
    }

    //댓글 지우기
    @Transactional
    public void delete(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        commentRepository.delete(comment);
    }
}
