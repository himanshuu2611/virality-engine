package com.grid07.viralityengine.controller;

import com.grid07.viralityengine.dto.CreateCommentRequest;
import com.grid07.viralityengine.dto.CreatePostRequest;
import com.grid07.viralityengine.entity.Comment;
import com.grid07.viralityengine.entity.Post;
import com.grid07.viralityengine.service.PostService;
import com.grid07.viralityengine.service.ViralityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ViralityService viralityService;

    // ==========================
    // CREATE POST
    // ==========================

    @PostMapping
    public Post createPost(
            @RequestBody CreatePostRequest request
    ) {

        return postService.createPost(request);
    }

    // ==========================
    // ADD COMMENT
    // ==========================

    @PostMapping("/{postId}/comments")
    public Comment addComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request
    ) {

        return postService.addComment(
                postId,
                request
        );
    }

    // ==========================
    // LIKE POST
    // ==========================

    @PostMapping("/{postId}/like")
    public String likePost(
            @PathVariable Long postId
    ) {

        // Human like = +20
        viralityService.incrementViralityScore(
                postId,
                20
        );

        return "Post liked successfully";
    }

    // ==========================
    // GET VIRALITY SCORE
    // ==========================

    @GetMapping("/{postId}/score")
    public String getViralityScore(
            @PathVariable Long postId
    ) {

        return viralityService
                .getViralityScore(postId);
    }
}