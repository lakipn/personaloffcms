package com.lazarmilosavljevic.personaloffcms.service.dto;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the BlogPost entity.
 */
public class BlogPostDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String caption;

    private String slug;

    @Lob
    private String body;

    private ZonedDateTime dateAndTime;


    private Set<TagDTO> tags = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ZonedDateTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(ZonedDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BlogPostDTO blogPostDTO = (BlogPostDTO) o;
        if (blogPostDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), blogPostDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BlogPostDTO{" +
            "id=" + getId() +
            ", caption='" + getCaption() + "'" +
            ", slug='" + getSlug() + "'" +
            ", body='" + getBody() + "'" +
            ", dateAndTime='" + getDateAndTime() + "'" +
            "}";
    }
}
