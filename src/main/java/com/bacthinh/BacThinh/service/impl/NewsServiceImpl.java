package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.dto.request.NewsRequest;
import com.bacthinh.BacThinh.dto.request.NewsUpdateRequest;
import com.bacthinh.BacThinh.dto.response.NewsResponse;
import com.bacthinh.BacThinh.entity.Category;
import com.bacthinh.BacThinh.entity.News;
import com.bacthinh.BacThinh.entity.NewsStatus;
import com.bacthinh.BacThinh.entity.User;
import com.bacthinh.BacThinh.exception.NotFoundException;
import com.bacthinh.BacThinh.mapper.NewsMapper;
import com.bacthinh.BacThinh.repository.CategoryRepository;
import com.bacthinh.BacThinh.repository.NewsRepository;
import com.bacthinh.BacThinh.repository.UserRepository;
import com.bacthinh.BacThinh.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NewsResponse createNews(NewsRequest newsRequest) {
        News news = newsMapper.toNews(newsRequest);

        Category category = categoryRepository.findById(newsRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category", newsRequest.getCategoryId()));
        news.setCategory(category);

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User", currentEmail));
        news.setAuthor(author);

        String slug = slugValid(news.getTitle(), news.getSlug(), null);
        news.setSlug(slug);

        news.setViews(0L);

        News savedNews = newsRepository.save(news);
        log.info("News created with ID: {} by user: {}", savedNews.getId(), currentEmail);

        return newsMapper.toResponse(savedNews);
    }

    @Override
    @Transactional
    public NewsResponse updateNews(Long id, NewsUpdateRequest newsUpdateRequest) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News", id));

        newsMapper.updateNewsFromDto(newsUpdateRequest, news);

        if (newsUpdateRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(newsUpdateRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category", newsUpdateRequest.getCategoryId()));
            news.setCategory(category);
        }

        if (newsUpdateRequest.getTitle() != null || newsUpdateRequest.getSlug() != null) {
            String slug = slugValid(
                    newsUpdateRequest.getTitle() != null ? newsUpdateRequest.getTitle() : news.getTitle(),
                    newsUpdateRequest.getSlug(),
                    news.getId()
            );
            news.setSlug(slug);
        }

        News updatedNews = newsRepository.save(news);
        log.info("News updated with ID: {}", updatedNews.getId());

        return newsMapper.toResponse(updatedNews);
    }

    @Override
    @Transactional
    public void deleteNews(Long newsId) {
        if (!newsRepository.existsById(newsId)) {
            throw new NotFoundException("News", newsId);
        }
        newsRepository.deleteById(newsId);
        log.info("News deleted with ID: {}", newsId);
    }

    @Override
    @Transactional
    public NewsResponse getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News", id));

        newsRepository.incrementViewCount(id);
        news.setViews(news.getViews() + 1);

        return newsMapper.toResponse(news);
    }

    @Override
    @Transactional
    public NewsResponse getNewsBySlug(String slug) {
        News news = newsRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("News with slug", slug));

        // Increment view count
        newsRepository.incrementViewCount(news.getId());
        news.setViews(news.getViews() + 1);

        return newsMapper.toResponse(news);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponse> getAllNews(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> searchNews(String keyword, Pageable pageable) {
        return newsRepository.findByKeyword(keyword, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> advancedSearch(String keyword, Long categoryId, NewsStatus status,
                                             Long authorId, String startDate, String endDate, Pageable pageable) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null && !startDate.isBlank()) {
            startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
        }
        if (endDate != null && !endDate.isBlank()) {
            endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
        }

        return newsRepository.findByAdvancedSearch(keyword, categoryId, status, authorId,
                        startDateTime, endDateTime, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> getByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category", categoryId);
        }
        return newsRepository.findByCategory_Id(categoryId, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> getByStatus(NewsStatus status, Pageable pageable) {
        return newsRepository.findByStatus(status, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> getByCategoryAndStatus(Long categoryId, NewsStatus status, Pageable pageable) {
        if (categoryId != null && status != null) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new NotFoundException("Category", categoryId);
            }
            return newsRepository.findByCategory_IdAndStatus(categoryId, status, pageable)
                    .map(newsMapper::toResponse);
        } else if (categoryId != null) {
            return getByCategory(categoryId, pageable);
        } else if (status != null) {
            return getByStatus(status, pageable);
        } else {
            return getAllNews(pageable);
        }
    }

    @Override
    public Page<NewsResponse> getByAuthor(Long authorId, Pageable pageable) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Author", authorId);
        }
        return newsRepository.findByAuthor_Id(authorId, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> getPublishedNews(Pageable pageable) {
        return newsRepository.findByStatus(NewsStatus.PUBLISHED, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> getTrendingNews(Pageable pageable) {
        return newsRepository.findByStatusOrderByViewsDescCreatedAtDesc(NewsStatus.PUBLISHED, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> getLatestNews(Pageable pageable) {
        return newsRepository.findByStatusOrderByCreatedAtDesc(NewsStatus.PUBLISHED, pageable)
                .map(newsMapper::toResponse);
    }

    @Override
    @Transactional
    public NewsResponse changeNewsStatus(Long id, NewsStatus status) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News", id));

        news.setStatus(status);
        News updatedNews = newsRepository.save(news);

        log.info("News status changed to {} for ID: {}", status, id);
        return newsMapper.toResponse(updatedNews);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new NotFoundException("News", id);
        }
        newsRepository.incrementViewCount(id);
    }

    @Override
    @Transactional
    public NewsResponse getNewsWithViewIncrement(Long id) {
        return getNewsById(id);
    }

    private String slugValid(String title, String slugRequest, Long excludeId) {
        String baseSlug;
        if (slugRequest != null && !slugRequest.isBlank()) {
            baseSlug = generateSlug(slugRequest);
        } else {
            baseSlug = generateSlug(title);
        }

        String finalSlug = baseSlug;
        int i = 1;
        while (isSlugExists(finalSlug, excludeId)) {
            finalSlug = baseSlug + "-" + i++;
        }

        return finalSlug;
    }

    private boolean isSlugExists(String slug, Long excludeId) {
        if (excludeId == null) {
            return newsRepository.existsBySlug(slug);
        } else {
            return newsRepository.existsBySlugAndIdNot(slug, excludeId);
        }
    }

    private String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            return "news-" + System.currentTimeMillis();
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        slug = slug.replaceAll("[Đ]", "D").replaceAll("[đ]", "d");

        return slug.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public Page<NewsResponse> getRelatedNews(Long newsId, Pageable pageable) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News", newsId));

        return newsRepository.findRelatedNews(news.getCategory().getId(), newsId,
                        NewsStatus.PUBLISHED, pageable)
                .map(newsMapper::toResponse);
    }

    public Page<NewsResponse> getPopularNewsSince(LocalDateTime since, Pageable pageable) {
        return newsRepository.findPopularNewsSince(NewsStatus.PUBLISHED, since, pageable)
                .map(newsMapper::toResponse);
    }

    public Page<NewsResponse> getNewsByDateRange(String startDate, String endDate, Pageable pageable) {
        LocalDateTime startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
        LocalDateTime endDateTime = LocalDateTime.parse(endDate + "T23:59:59");

        return newsRepository.findByDateRange(startDateTime, endDateTime, pageable)
                .map(newsMapper::toResponse);
    }

    public Long getNewsCountByStatus(NewsStatus status) {
        return newsRepository.countByStatus(status);
    }

    public Long getNewsCountByCategory(Long categoryId) {
        return newsRepository.countByCategory(categoryId);
    }

    public Long getNewsCountByAuthor(Long authorId) {
        return newsRepository.countByAuthor(authorId);
    }
}