package com.lazarmilosavljevic.personaloffcms.repository;

import com.lazarmilosavljevic.personaloffcms.domain.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the BlogPost entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor<BlogPost> {

    @Query(value = "select distinct blog_post from BlogPost blog_post left join fetch blog_post.tags",
        countQuery = "select count(distinct blog_post) from BlogPost blog_post")
    Page<BlogPost> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct blog_post from BlogPost blog_post left join fetch blog_post.tags")
    List<BlogPost> findAllWithEagerRelationships();

    @Query("select blog_post from BlogPost blog_post left join fetch blog_post.tags where blog_post.id =:id")
    Optional<BlogPost> findOneWithEagerRelationships(@Param("id") Long id);

}
