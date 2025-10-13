package com.sparta.foodorder.domain.ai.infrastructure.client.gemini;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequestDto {
    private List<Content> contents;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    public void createRequestDto(String text) {
        this.contents = new ArrayList<>();

        Part part = new Part(text);
        List<Part> parts = new ArrayList<>();
        parts.add(part);

        Content content = new Content(parts);
        contents.add(content);
    }
}
