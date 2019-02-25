package com.lazarmilosavljevic.personaloffcms.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the Comment entity.
 */
public class CommentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 128)
    private String name;

    
    @Lob
    private String body;

    @NotNull
    @Size(max = 256)
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;


    private Long blogPostId;

    private String blogPostCaption;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getBlogPostId() {
        return blogPostId;
    }

    public void setBlogPostId(Long blogPostId) {
        this.blogPostId = blogPostId;
    }

    public String getBlogPostCaption() {
        return blogPostCaption;
    }

    public void setBlogPostCaption(String blogPostCaption) {
        this.blogPostCaption = blogPostCaption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommentDTO commentDTO = (CommentDTO) o;
        if (commentDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), commentDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", body='" + getBody() + "'" +
            ", email='" + getEmail() + "'" +
            ", blogPost=" + getBlogPostId() +
            ", blogPost='" + getBlogPostCaption() + "'" +
            "}";
    }
}
