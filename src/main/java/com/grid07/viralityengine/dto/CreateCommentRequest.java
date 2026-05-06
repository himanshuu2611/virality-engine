package com.grid07.viralityengine.dto;

import lombok.Data;

@Data
public class CreateCommentRequest {

    private Long authorId;

    private String content;

    private String botId;

    private String humanId;

    private int depthLevel;

    public Object getUserId() {
        return getUserId();
    }

    public Long getPostId() {
        return getPostId();
    }
}