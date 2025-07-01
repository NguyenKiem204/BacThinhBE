package com.bacthinh.BacThinh.dto.response;

import com.bacthinh.BacThinh.entity.NewsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {

    private Long id;

    private String title;

    private String slug;

    private String summary;

    private String content;

    private String imageUrl;

    private Long views;

    private NewsStatus status;

    private String categoryName;

    private Long categoryId;

    private String authorName;

    private Long authorId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
