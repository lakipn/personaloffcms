package com.lazarmilosavljevic.personaloffcms.service.mapper;

import com.lazarmilosavljevic.personaloffcms.domain.*;
import com.lazarmilosavljevic.personaloffcms.service.dto.BlogPostDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity BlogPost and its DTO BlogPostDTO.
 */
@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface BlogPostMapper extends EntityMapper<BlogPostDTO, BlogPost> {


    @Mapping(target = "comments", ignore = true)
    BlogPost toEntity(BlogPostDTO blogPostDTO);

    default BlogPost fromId(Long id) {
        if (id == null) {
            return null;
        }
        BlogPost blogPost = new BlogPost();
        blogPost.setId(id);
        return blogPost;
    }
}
