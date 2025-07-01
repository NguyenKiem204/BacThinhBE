package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.Category;
import com.bacthinh.BacThinh.entity.News;
import com.bacthinh.BacThinh.entity.NewsStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findById(Long id);
    Optional<News> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
    boolean existsById(Long id);

    Page<News> findByCategory_Id(Long categoryId, Pageable pageable);
    Page<News> findByStatus(NewsStatus status, Pageable pageable);
    Page<News> findByAuthor_Id(Long authorId, Pageable pageable);
    Page<News> findByCategory_IdAndStatus(Long categoryId, NewsStatus status, Pageable pageable);
    Page<News> findByAuthor_IdAndStatus(Long authorId, NewsStatus status, Pageable pageable);
    Page<News> findByCategory_IdAndAuthor_Id(Long categoryId, Long authorId, Pageable pageable);
    Page<News> findByCategory_IdAndStatusAndAuthor_Id(Long categoryId, NewsStatus status, Long authorId, Pageable pageable);
    Page<News> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
                SELECT n FROM News n
                JOIN n.category c
                WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<News> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT n FROM News n
            JOIN n.category c
            JOIN n.author a
            WHERE (:keyword IS NULL OR 
                   LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(n.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:categoryId IS NULL OR n.category.id = :categoryId)
              AND (:status IS NULL OR n.status = :status)
              AND (:authorId IS NULL OR n.author.id = :authorId)
              AND (:startDate IS NULL OR n.createdAt >= :startDate)
              AND (:endDate IS NULL OR n.createdAt <= :endDate)
            """)
    Page<News> findByAdvancedSearch(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") NewsStatus status,
            @Param("authorId") Long authorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    Page<News> findByStatusOrderByViewsDescCreatedAtDesc(NewsStatus status, Pageable pageable);
    Page<News> findByStatusOrderByCreatedAtDesc(NewsStatus status, Pageable pageable);

    @Query("""
            SELECT n FROM News n
            WHERE n.status = :status
              AND n.createdAt >= :startDate
            ORDER BY n.views DESC, n.createdAt DESC
            """)
    Page<News> findPopularNewsSince(@Param("status") NewsStatus status,
                                    @Param("startDate") LocalDateTime startDate,
                                    Pageable pageable);

    @Query("""
            SELECT n FROM News n
            WHERE n.createdAt >= :startDate
              AND n.createdAt <= :endDate
            ORDER BY n.createdAt DESC
            """)
    Page<News> findByDateRange(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.views = n.views + 1 WHERE n.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.status = :status WHERE n.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") NewsStatus status);

    @Query("SELECT COUNT(n) FROM News n WHERE n.status = :status")
    Long countByStatus(@Param("status") NewsStatus status);

    @Query("SELECT COUNT(n) FROM News n WHERE n.category.id = :categoryId")
    Long countByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(n) FROM News n WHERE n.author.id = :authorId")
    Long countByAuthor(@Param("authorId") Long authorId);

    @Query("""
            SELECT n FROM News n
            WHERE n.category.id = :categoryId
              AND n.status = :status
              AND n.createdAt >= :since
            ORDER BY n.createdAt DESC
            """)
    Page<News> findRecentByCategoryAndStatus(@Param("categoryId") Long categoryId,
                                             @Param("status") NewsStatus status,
                                             @Param("since") LocalDateTime since,
                                             Pageable pageable);
    @Query("""
            SELECT n FROM News n
            WHERE n.category.id = :categoryId
              AND n.id != :excludeId
              AND n.status = :status
            ORDER BY n.createdAt DESC
            """)
    Page<News> findRelatedNews(@Param("categoryId") Long categoryId,
                               @Param("excludeId") Long excludeId,
                               @Param("status") NewsStatus status,
                               Pageable pageable);

    @Query("""
            SELECT new map(
                COUNT(n) as totalNews,
                SUM(n.views) as totalViews,
                AVG(n.views) as averageViews,
                MAX(n.views) as maxViews
            )
            FROM News n
            WHERE n.status = :status
            """)
    Object getNewsStatistics(@Param("status") NewsStatus status);
}

