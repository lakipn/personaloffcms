package com.lazarmilosavljevic.personaloffcms.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A BlogPost.
 */
@Entity
@Table(name = "blog_post")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BlogPost implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 160)
    @Column(name = "caption", length = 160, nullable = false)
    private String caption;

    @Column(name = "slug")
    private String slug;

    @Lob
    @Column(name = "jhi_body")
    private String body;

    @Column(name = "date_and_time")
    private ZonedDateTime dateAndTime;

    @OneToMany(mappedBy = "blogPost")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Comment> comments = new HashSet<>();
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "blog_post_tags",
               joinColumns = @JoinColumn(name = "blog_post_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "tags_id", referencedColumnName = "id"))
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public BlogPost caption(String caption) {
        this.caption = caption;
        return this;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSlug() {
        return slug;
    }

    public BlogPost slug(String slug) {
        this.slug = slug;
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBody() {
        return body;
    }

    public BlogPost body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ZonedDateTime getDateAndTime() {
        return dateAndTime;
    }

    public BlogPost dateAndTime(ZonedDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
        return this;
    }

    public void setDateAndTime(ZonedDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public BlogPost comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public BlogPost addComments(Comment comment) {
        this.comments.add(comment);
        comment.setBlogPost(this);
        return this;
    }

    public BlogPost removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setBlogPost(null);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public BlogPost tags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public BlogPost addTags(Tag tag) {
        this.tags.add(tag);
        tag.getBlogPosts(caption)S().add(this);
        return this;
    }

    public BlogPost removeTags(Tag tag) {
        this.tags.remove(tag);
        tag.getBlogPosts(caption)S().remove(this);
        return this;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlogPost blogPost = (BlogPost) o;
        if (blogPost.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), blogPost.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BlogPost{" +
            "id=" + getId() +
            ", caption='" + getCaption() + "'" +
            ", slug='" + getSlug() + "'" +
            ", body='" + getBody() + "'" +
            ", dateAndTime='" + getDateAndTime() + "'" +
            "}";
    }
}
