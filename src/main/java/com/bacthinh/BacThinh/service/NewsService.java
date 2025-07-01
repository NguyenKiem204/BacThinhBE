package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.NewsRequest;
import com.bacthinh.BacThinh.dto.request.NewsUpdateRequest;
import com.bacthinh.BacThinh.dto.response.NewsResponse;
import com.bacthinh.BacThinh.entity.NewsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsService {
    NewsResponse createNews(NewsRequest newsRequest);
    NewsResponse updateNews(Long id, NewsUpdateRequest newsUpdateRequest);
    void deleteNews(Long newsId);
    Page<NewsResponse> getAllNews(Pageable pageable);

    Page<NewsResponse> searchNews(String keyword, Pageable pageable);
    Page<NewsResponse> advancedSearch(String keyword, Long categoryId, NewsStatus status,
                                      Long authorId, String startDate, String endDate, Pageable pageable);
    NewsResponse getNewsById(Long id);
    Page<NewsResponse> getByCategory(Long categoryId, Pageable pageable);
    Page<NewsResponse> getByStatus(NewsStatus status, Pageable pageable);
    Page<NewsResponse> getByCategoryAndStatus(Long categoryId, NewsStatus status, Pageable pageable);
    Page<NewsResponse> getByAuthor(Long authorId, Pageable pageable);
    NewsResponse getNewsBySlug(String slug);

    Page<NewsResponse> getPublishedNews(Pageable pageable);
    Page<NewsResponse> getTrendingNews(Pageable pageable);
    Page<NewsResponse> getLatestNews(Pageable pageable);

    NewsResponse changeNewsStatus(Long id, NewsStatus status);

    void incrementViewCount(Long id);
    NewsResponse getNewsWithViewIncrement(Long id);
}
