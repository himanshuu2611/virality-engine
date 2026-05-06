package com.grid07.viralityengine.service;

import com.grid07.viralityengine.dto.CreateCommentRequest;
import com.grid07.viralityengine.dto.CreatePostRequest;
import com.grid07.viralityengine.entity.Comment;
import com.grid07.viralityengine.entity.Post;
import com.grid07.viralityengine.repository.CommentRepository;
import com.grid07.viralityengine.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ViralityService viralityService;
    private final GuardrailService guardrailService;

    public Post createPost(CreatePostRequest request) {

        Post post = Post.builder()
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }


    public Comment addComment(
            Long postId,
            CreateCommentRequest request
    ) {


        guardrailService.validateBotReply(
                postId,
                request.getDepthLevel(),
                request.getBotId(),
                request.getHumanId()
        );


        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .depthLevel(request.getDepthLevel())
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment =
                commentRepository.save(comment);


        viralityService.incrementViralityScore(
                postId,
                50
        );

        return savedComment;
    }
}