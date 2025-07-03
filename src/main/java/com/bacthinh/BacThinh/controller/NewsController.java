package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.request.NewsRequest;
import com.bacthinh.BacThinh.dto.request.NewsUpdateRequest;
import com.bacthinh.BacThinh.dto.response.ApiResponse;
import com.bacthinh.BacThinh.dto.response.NewsResponse;
import com.bacthinh.BacThinh.dto.response.ResidentResponse;
import com.bacthinh.BacThinh.entity.NewsStatus;
import com.bacthinh.BacThinh.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "News Controller", description = "Manage News: create, update, search, delete, filter")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {
    private final NewsService newsService;

    @Operation(summary = "Create a News", description = "ADMIN creates a news.")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<NewsResponse>> createNews(@RequestBody @Valid NewsRequest newsRequest) {
        NewsResponse response = newsService.createNews(newsRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "News created successfully!"));
    }

    @Operation(summary = "Update a News", description = "ADMIN updates a news by ID.")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> updateNews(
            @PathVariable Long id,
            @RequestBody @Valid NewsUpdateRequest newsUpdateRequest) {
        NewsResponse response = newsService.updateNews(id, newsUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "News updated successfully!"));
    }

    @Operation(summary = "Delete a News", description = "ADMIN deletes a news by ID.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok(ApiResponse.success(null, "News deleted successfully!"));
    }

    @Operation(summary = "Get News by ID", description = "Get a specific news by ID and increment view count.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponse>> getNewsById(@PathVariable Long id) {
        NewsResponse response = newsService.getNewsById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "News retrieved successfully!"));
    }

    @Operation(summary = "Get News by Slug", description = "Get a specific news by slug and increment view count.")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<NewsResponse>> getNewsBySlug(@PathVariable String slug) {
        NewsResponse response = newsService.getNewsBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response, "News retrieved successfully!"));
    }

    @Operation(summary = "Get all news", description = "Get all news with pagination and sorting.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NewsResponse> newsList = newsService.getAllNews(pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "News retrieved successfully!"));
    }

    @Operation(summary = "Search news", description = "Search news by keyword.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> searchNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsResponse> newsList = newsService.searchNews(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "Search results retrieved successfully!"));
    }

    @Operation(summary = "Get news by category", description = "Get news filtered by category ID.")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getNewsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsResponse> newsList = newsService.getByCategory(categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "News by category retrieved successfully!"));
    }

    @Operation(summary = "Get news by status", description = "Get news filtered by status (DRAFT, PUBLISHED, ARCHIVED).")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getNewsByStatus(
            @PathVariable NewsStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsResponse> newsList = newsService.getByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "News by status retrieved successfully!"));
    }

    @Operation(summary = "Get news by category and status", description = "Get news filtered by both category and status.")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getNewsByCategoryAndStatus(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsResponse> newsList = newsService.getByCategoryAndStatus(categoryId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "Filtered news retrieved successfully!"));
    }

    @Operation(summary = "Get trending news", description = "Get news sorted by view count (most viewed first).")
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getTrendingNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sort = Sort.by("views").descending().and(Sort.by("createdAt").descending());
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsResponse> newsList = newsService.getTrendingNews(pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "Trending news retrieved successfully!"));
    }
    @Operation(summary = "Get latest news", description = "Get the most recent published news.")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getLatestNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsResponse> newsList = newsService.getLatestNews(pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "Latest news retrieved successfully!"));
    }

    @Operation(summary = "Change news status", description = "ADMIN changes the status of a news (DRAFT/PUBLISHED/ARCHIVED).")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<NewsResponse>> changeNewsStatus(
            @PathVariable Long id,
            @RequestParam NewsStatus status) {
        NewsResponse response = newsService.changeNewsStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "News status updated successfully!"));
    }

    @Operation(summary = "Increment view count", description = "Increment the view count for a specific news.")
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long id) {
        newsService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(null, "View count incremented successfully!"));
    }

    @Operation(summary = "Get news by author", description = "Get news filtered by author ID.")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> getNewsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NewsResponse> newsList = newsService.getByAuthor(authorId, pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "News by author retrieved successfully!"));
    }

    @Operation(summary = "Advanced search", description = "Advanced search with multiple filters.")
    @GetMapping("/advanced-search")
    public ResponseEntity<ApiResponse<Page<NewsResponse>>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NewsResponse> newsList = newsService.advancedSearch(
                keyword, categoryId, status, authorId, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(newsList, "Advanced search results retrieved successfully!"));
    }
}
