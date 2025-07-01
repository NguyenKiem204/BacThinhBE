package com.bacthinh.BacThinh.dto.request;

import com.bacthinh.BacThinh.entity.NewsStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewsRequest {
    @NotBlank
    private String title;

    private String slug;

    private String summary;

    @NotBlank
    private String content;

    private String imageUrl;

    @NotNull
    private NewsStatus status;

    @NotNull
    private Long categoryId;
}
