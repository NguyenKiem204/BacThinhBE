package com.bacthinh.BacThinh.mapper;

import com.bacthinh.BacThinh.dto.request.NewsRequest;
import com.bacthinh.BacThinh.dto.request.NewsUpdateRequest;
import com.bacthinh.BacThinh.dto.response.NewsResponse;
import com.bacthinh.BacThinh.entity.Category;
import com.bacthinh.BacThinh.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "category", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", ignore = true)
    News toNews(NewsRequest dto);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", expression = "java(getAuthorFullName(news))")
    NewsResponse toResponse(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateNewsFromDto(NewsUpdateRequest dto, @MappingTarget News news);

    default Category mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
    default String getAuthorFullName(News news) {
        if (news == null || news.getAuthor() == null || news.getAuthor().getResident() == null) {
            return null;
        }
        return news.getAuthor().getResident().getFullName();
    }
}
