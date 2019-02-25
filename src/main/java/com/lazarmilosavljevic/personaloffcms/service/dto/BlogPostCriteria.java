package com.lazarmilosavljevic.personaloffcms.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the BlogPost entity. This class is used in BlogPostResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /blog-posts?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BlogPostCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter caption;

    private StringFilter slug;

    private ZonedDateTimeFilter dateAndTime;

    private LongFilter commentsId;

    private LongFilter tagsId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCaption() {
        return caption;
    }

    public void setCaption(StringFilter caption) {
        this.caption = caption;
    }

    public StringFilter getSlug() {
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public ZonedDateTimeFilter getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(ZonedDateTimeFilter dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public LongFilter getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(LongFilter commentsId) {
        this.commentsId = commentsId;
    }

    public LongFilter getTagsId() {
        return tagsId;
    }

    public void setTagsId(LongFilter tagsId) {
        this.tagsId = tagsId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BlogPostCriteria that = (BlogPostCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(caption, that.caption) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(dateAndTime, that.dateAndTime) &&
            Objects.equals(commentsId, that.commentsId) &&
            Objects.equals(tagsId, that.tagsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        caption,
        slug,
        dateAndTime,
        commentsId,
        tagsId
        );
    }

    @Override
    public String toString() {
        return "BlogPostCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (caption != null ? "caption=" + caption + ", " : "") +
                (slug != null ? "slug=" + slug + ", " : "") +
                (dateAndTime != null ? "dateAndTime=" + dateAndTime + ", " : "") +
                (commentsId != null ? "commentsId=" + commentsId + ", " : "") +
                (tagsId != null ? "tagsId=" + tagsId + ", " : "") +
            "}";
    }

}
