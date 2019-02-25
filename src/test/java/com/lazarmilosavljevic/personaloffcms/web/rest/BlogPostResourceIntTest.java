package com.lazarmilosavljevic.personaloffcms.web.rest;

import com.lazarmilosavljevic.personaloffcms.PersonalOffCmsApp;

import com.lazarmilosavljevic.personaloffcms.domain.BlogPost;
import com.lazarmilosavljevic.personaloffcms.domain.Comment;
import com.lazarmilosavljevic.personaloffcms.domain.Tag;
import com.lazarmilosavljevic.personaloffcms.repository.BlogPostRepository;
import com.lazarmilosavljevic.personaloffcms.service.BlogPostService;
import com.lazarmilosavljevic.personaloffcms.service.dto.BlogPostDTO;
import com.lazarmilosavljevic.personaloffcms.service.mapper.BlogPostMapper;
import com.lazarmilosavljevic.personaloffcms.web.rest.errors.ExceptionTranslator;
import com.lazarmilosavljevic.personaloffcms.service.dto.BlogPostCriteria;
import com.lazarmilosavljevic.personaloffcms.service.BlogPostQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


import static com.lazarmilosavljevic.personaloffcms.web.rest.TestUtil.sameInstant;
import static com.lazarmilosavljevic.personaloffcms.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BlogPostResource REST controller.
 *
 * @see BlogPostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PersonalOffCmsApp.class)
public class BlogPostResourceIntTest {

    private static final String DEFAULT_CAPTION = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE_AND_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_AND_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Mock
    private BlogPostRepository blogPostRepositoryMock;

    @Autowired
    private BlogPostMapper blogPostMapper;

    @Mock
    private BlogPostService blogPostServiceMock;

    @Autowired
    private BlogPostService blogPostService;

    @Autowired
    private BlogPostQueryService blogPostQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBlogPostMockMvc;

    private BlogPost blogPost;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BlogPostResource blogPostResource = new BlogPostResource(blogPostService, blogPostQueryService);
        this.restBlogPostMockMvc = MockMvcBuilders.standaloneSetup(blogPostResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlogPost createEntity(EntityManager em) {
        BlogPost blogPost = new BlogPost()
            .caption(DEFAULT_CAPTION)
            .slug(DEFAULT_SLUG)
            .body(DEFAULT_BODY)
            .dateAndTime(DEFAULT_DATE_AND_TIME);
        return blogPost;
    }

    @Before
    public void initTest() {
        blogPost = createEntity(em);
    }

    @Test
    @Transactional
    public void createBlogPost() throws Exception {
        int databaseSizeBeforeCreate = blogPostRepository.findAll().size();

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);
        restBlogPostMockMvc.perform(post("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPostDTO)))
            .andExpect(status().isCreated());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeCreate + 1);
        BlogPost testBlogPost = blogPostList.get(blogPostList.size() - 1);
        assertThat(testBlogPost.getCaption()).isEqualTo(DEFAULT_CAPTION);
        assertThat(testBlogPost.getSlug()).isEqualTo(DEFAULT_SLUG);
        assertThat(testBlogPost.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testBlogPost.getDateAndTime()).isEqualTo(DEFAULT_DATE_AND_TIME);
    }

    @Test
    @Transactional
    public void createBlogPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = blogPostRepository.findAll().size();

        // Create the BlogPost with an existing ID
        blogPost.setId(1L);
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlogPostMockMvc.perform(post("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCaptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = blogPostRepository.findAll().size();
        // set the field null
        blogPost.setCaption(null);

        // Create the BlogPost, which fails.
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        restBlogPostMockMvc.perform(post("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBlogPosts() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList
        restBlogPostMockMvc.perform(get("/api/blog-posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION.toString())))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].dateAndTime").value(hasItem(sameInstant(DEFAULT_DATE_AND_TIME))));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllBlogPostsWithEagerRelationshipsIsEnabled() throws Exception {
        BlogPostResource blogPostResource = new BlogPostResource(blogPostServiceMock, blogPostQueryService);
        when(blogPostServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restBlogPostMockMvc = MockMvcBuilders.standaloneSetup(blogPostResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restBlogPostMockMvc.perform(get("/api/blog-posts?eagerload=true"))
        .andExpect(status().isOk());

        verify(blogPostServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllBlogPostsWithEagerRelationshipsIsNotEnabled() throws Exception {
        BlogPostResource blogPostResource = new BlogPostResource(blogPostServiceMock, blogPostQueryService);
            when(blogPostServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restBlogPostMockMvc = MockMvcBuilders.standaloneSetup(blogPostResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restBlogPostMockMvc.perform(get("/api/blog-posts?eagerload=true"))
        .andExpect(status().isOk());

            verify(blogPostServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getBlogPost() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get the blogPost
        restBlogPostMockMvc.perform(get("/api/blog-posts/{id}", blogPost.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(blogPost.getId().intValue()))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION.toString()))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.dateAndTime").value(sameInstant(DEFAULT_DATE_AND_TIME)));
    }

    @Test
    @Transactional
    public void getAllBlogPostsByCaptionIsEqualToSomething() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where caption equals to DEFAULT_CAPTION
        defaultBlogPostShouldBeFound("caption.equals=" + DEFAULT_CAPTION);

        // Get all the blogPostList where caption equals to UPDATED_CAPTION
        defaultBlogPostShouldNotBeFound("caption.equals=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllBlogPostsByCaptionIsInShouldWork() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where caption in DEFAULT_CAPTION or UPDATED_CAPTION
        defaultBlogPostShouldBeFound("caption.in=" + DEFAULT_CAPTION + "," + UPDATED_CAPTION);

        // Get all the blogPostList where caption equals to UPDATED_CAPTION
        defaultBlogPostShouldNotBeFound("caption.in=" + UPDATED_CAPTION);
    }

    @Test
    @Transactional
    public void getAllBlogPostsByCaptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where caption is not null
        defaultBlogPostShouldBeFound("caption.specified=true");

        // Get all the blogPostList where caption is null
        defaultBlogPostShouldNotBeFound("caption.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlogPostsBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug equals to DEFAULT_SLUG
        defaultBlogPostShouldBeFound("slug.equals=" + DEFAULT_SLUG);

        // Get all the blogPostList where slug equals to UPDATED_SLUG
        defaultBlogPostShouldNotBeFound("slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    public void getAllBlogPostsBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug in DEFAULT_SLUG or UPDATED_SLUG
        defaultBlogPostShouldBeFound("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG);

        // Get all the blogPostList where slug equals to UPDATED_SLUG
        defaultBlogPostShouldNotBeFound("slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    public void getAllBlogPostsBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where slug is not null
        defaultBlogPostShouldBeFound("slug.specified=true");

        // Get all the blogPostList where slug is null
        defaultBlogPostShouldNotBeFound("slug.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlogPostsByDateAndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where dateAndTime equals to DEFAULT_DATE_AND_TIME
        defaultBlogPostShouldBeFound("dateAndTime.equals=" + DEFAULT_DATE_AND_TIME);

        // Get all the blogPostList where dateAndTime equals to UPDATED_DATE_AND_TIME
        defaultBlogPostShouldNotBeFound("dateAndTime.equals=" + UPDATED_DATE_AND_TIME);
    }

    @Test
    @Transactional
    public void getAllBlogPostsByDateAndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where dateAndTime in DEFAULT_DATE_AND_TIME or UPDATED_DATE_AND_TIME
        defaultBlogPostShouldBeFound("dateAndTime.in=" + DEFAULT_DATE_AND_TIME + "," + UPDATED_DATE_AND_TIME);

        // Get all the blogPostList where dateAndTime equals to UPDATED_DATE_AND_TIME
        defaultBlogPostShouldNotBeFound("dateAndTime.in=" + UPDATED_DATE_AND_TIME);
    }

    @Test
    @Transactional
    public void getAllBlogPostsByDateAndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where dateAndTime is not null
        defaultBlogPostShouldBeFound("dateAndTime.specified=true");

        // Get all the blogPostList where dateAndTime is null
        defaultBlogPostShouldNotBeFound("dateAndTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllBlogPostsByDateAndTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where dateAndTime greater than or equals to DEFAULT_DATE_AND_TIME
        defaultBlogPostShouldBeFound("dateAndTime.greaterOrEqualThan=" + DEFAULT_DATE_AND_TIME);

        // Get all the blogPostList where dateAndTime greater than or equals to UPDATED_DATE_AND_TIME
        defaultBlogPostShouldNotBeFound("dateAndTime.greaterOrEqualThan=" + UPDATED_DATE_AND_TIME);
    }

    @Test
    @Transactional
    public void getAllBlogPostsByDateAndTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        // Get all the blogPostList where dateAndTime less than or equals to DEFAULT_DATE_AND_TIME
        defaultBlogPostShouldNotBeFound("dateAndTime.lessThan=" + DEFAULT_DATE_AND_TIME);

        // Get all the blogPostList where dateAndTime less than or equals to UPDATED_DATE_AND_TIME
        defaultBlogPostShouldBeFound("dateAndTime.lessThan=" + UPDATED_DATE_AND_TIME);
    }


    @Test
    @Transactional
    public void getAllBlogPostsByCommentsIsEqualToSomething() throws Exception {
        // Initialize the database
        Comment comments = CommentResourceIntTest.createEntity(em);
        em.persist(comments);
        em.flush();
        blogPost.addComments(comments);
        blogPostRepository.saveAndFlush(blogPost);
        Long commentsId = comments.getId();

        // Get all the blogPostList where comments equals to commentsId
        defaultBlogPostShouldBeFound("commentsId.equals=" + commentsId);

        // Get all the blogPostList where comments equals to commentsId + 1
        defaultBlogPostShouldNotBeFound("commentsId.equals=" + (commentsId + 1));
    }


    @Test
    @Transactional
    public void getAllBlogPostsByTagsIsEqualToSomething() throws Exception {
        // Initialize the database
        Tag tags = TagResourceIntTest.createEntity(em);
        em.persist(tags);
        em.flush();
        blogPost.addTags(tags);
        blogPostRepository.saveAndFlush(blogPost);
        Long tagsId = tags.getId();

        // Get all the blogPostList where tags equals to tagsId
        defaultBlogPostShouldBeFound("tagsId.equals=" + tagsId);

        // Get all the blogPostList where tags equals to tagsId + 1
        defaultBlogPostShouldNotBeFound("tagsId.equals=" + (tagsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBlogPostShouldBeFound(String filter) throws Exception {
        restBlogPostMockMvc.perform(get("/api/blog-posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogPost.getId().intValue())))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].dateAndTime").value(hasItem(sameInstant(DEFAULT_DATE_AND_TIME))));

        // Check, that the count call also returns 1
        restBlogPostMockMvc.perform(get("/api/blog-posts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBlogPostShouldNotBeFound(String filter) throws Exception {
        restBlogPostMockMvc.perform(get("/api/blog-posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBlogPostMockMvc.perform(get("/api/blog-posts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingBlogPost() throws Exception {
        // Get the blogPost
        restBlogPostMockMvc.perform(get("/api/blog-posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBlogPost() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        int databaseSizeBeforeUpdate = blogPostRepository.findAll().size();

        // Update the blogPost
        BlogPost updatedBlogPost = blogPostRepository.findById(blogPost.getId()).get();
        // Disconnect from session so that the updates on updatedBlogPost are not directly saved in db
        em.detach(updatedBlogPost);
        updatedBlogPost
            .caption(UPDATED_CAPTION)
            .slug(UPDATED_SLUG)
            .body(UPDATED_BODY)
            .dateAndTime(UPDATED_DATE_AND_TIME);
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(updatedBlogPost);

        restBlogPostMockMvc.perform(put("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPostDTO)))
            .andExpect(status().isOk());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeUpdate);
        BlogPost testBlogPost = blogPostList.get(blogPostList.size() - 1);
        assertThat(testBlogPost.getCaption()).isEqualTo(UPDATED_CAPTION);
        assertThat(testBlogPost.getSlug()).isEqualTo(UPDATED_SLUG);
        assertThat(testBlogPost.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testBlogPost.getDateAndTime()).isEqualTo(UPDATED_DATE_AND_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingBlogPost() throws Exception {
        int databaseSizeBeforeUpdate = blogPostRepository.findAll().size();

        // Create the BlogPost
        BlogPostDTO blogPostDTO = blogPostMapper.toDto(blogPost);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlogPostMockMvc.perform(put("/api/blog-posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blogPostDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BlogPost in the database
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBlogPost() throws Exception {
        // Initialize the database
        blogPostRepository.saveAndFlush(blogPost);

        int databaseSizeBeforeDelete = blogPostRepository.findAll().size();

        // Delete the blogPost
        restBlogPostMockMvc.perform(delete("/api/blog-posts/{id}", blogPost.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        assertThat(blogPostList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlogPost.class);
        BlogPost blogPost1 = new BlogPost();
        blogPost1.setId(1L);
        BlogPost blogPost2 = new BlogPost();
        blogPost2.setId(blogPost1.getId());
        assertThat(blogPost1).isEqualTo(blogPost2);
        blogPost2.setId(2L);
        assertThat(blogPost1).isNotEqualTo(blogPost2);
        blogPost1.setId(null);
        assertThat(blogPost1).isNotEqualTo(blogPost2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlogPostDTO.class);
        BlogPostDTO blogPostDTO1 = new BlogPostDTO();
        blogPostDTO1.setId(1L);
        BlogPostDTO blogPostDTO2 = new BlogPostDTO();
        assertThat(blogPostDTO1).isNotEqualTo(blogPostDTO2);
        blogPostDTO2.setId(blogPostDTO1.getId());
        assertThat(blogPostDTO1).isEqualTo(blogPostDTO2);
        blogPostDTO2.setId(2L);
        assertThat(blogPostDTO1).isNotEqualTo(blogPostDTO2);
        blogPostDTO1.setId(null);
        assertThat(blogPostDTO1).isNotEqualTo(blogPostDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(blogPostMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(blogPostMapper.fromId(null)).isNull();
    }
}
