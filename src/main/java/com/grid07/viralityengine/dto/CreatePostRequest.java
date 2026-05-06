package com.grid07.viralityengine.dto;

import lombok.Data;

@Data
public class CreatePostRequest {

    private Long authorId;

    private String content;
}