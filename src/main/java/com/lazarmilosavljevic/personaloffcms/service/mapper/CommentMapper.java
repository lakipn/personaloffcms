package com.lazarmilosavljevic.personaloffcms.service.mapper;

import com.lazarmilosavljevic.personaloffcms.domain.*;
import com.lazarmilosavljevic.personaloffcms.service.dto.CommentDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Comment and its DTO CommentDTO.
 */
@Mapper(componentModel = "spring", uses = {BlogPostMapper.class})
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {

    @Mapping(source = "blogPost.id", target = "blogPostId")
    @Mapping(source = "blogPost.caption", target = "blogPostCaption")
    CommentDTO toDto(Comment comment);

    @Mapping(source = "blogPostId", target = "blogPost")
    Comment toEntity(CommentDTO commentDTO);

    default Comment fromId(Long id) {
        if (id == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(id);
        return comment;
    }
}
