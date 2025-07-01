package com.bacthinh.BacThinh.dto.request;

import com.bacthinh.BacThinh.entity.NewsStatus;
import lombok.Data;

@Data
public class NewsUpdateRequest {

    private String title;

    private String slug;

    private String summary;

    private String content;

    private String imageUrl;

    private NewsStatus status;

    private Long categoryId;
}

