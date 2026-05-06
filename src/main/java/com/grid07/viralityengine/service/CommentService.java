package com.grid07.viralityengine.service;

import com.grid07.viralityengine.dto.CreateCommentRequest;
import com.grid07.viralityengine.entity.Comment;
import com.grid07.viralityengine.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final GuardrailService guardrailService;
    private final CommentRepository commentRepository;

    @Transactional
    public Comment createComment(CreateCommentRequest request) {

        // FIRST Redis validation
        guardrailService.checkHorizontalCap(request.getPostId());

        // ONLY THEN database insert
        Comment comment = new Comment();

        comment.setPostId(request.getPostId());
        comment.setContent(request.getContent());

        return commentRepository.save(comment);
    }
}