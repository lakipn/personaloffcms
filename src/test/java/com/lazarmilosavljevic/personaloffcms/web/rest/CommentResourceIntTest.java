package com.lazarmilosavljevic.personaloffcms.web.rest;

import com.lazarmilosavljevic.personaloffcms.PersonalOffCmsApp;

import com.lazarmilosavljevic.personaloffcms.domain.Comment;
import com.lazarmilosavljevic.personaloffcms.domain.BlogPost;
import com.lazarmilosavljevic.personaloffcms.repository.CommentRepository;
import com.lazarmilosavljevic.personaloffcms.service.CommentService;
import com.lazarmilosavljevic.personaloffcms.service.dto.CommentDTO;
import com.lazarmilosavljevic.personaloffcms.service.mapper.CommentMapper;
import com.lazarmilosavljevic.personaloffcms.web.rest.errors.ExceptionTranslator;
import com.lazarmilosavljevic.personaloffcms.service.dto.CommentCriteria;
import com.lazarmilosavljevic.personaloffcms.service.CommentQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.List;


import static com.lazarmilosavljevic.personaloffcms.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CommentResource REST controller.
 *
 * @see CommentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PersonalOffCmsApp.class)
public class CommentResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "^@Y.V";
    private static final String UPDATED_EMAIL = "WF@NS.<";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentQueryService commentQueryService;

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

    private MockMvc restCommentMockMvc;

    private Comment comment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CommentResource commentResource = new CommentResource(commentService, commentQueryService);
        this.restCommentMockMvc = MockMvcBuilders.standaloneSetup(commentResource)
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
    public static Comment createEntity(EntityManager em) {
        Comment comment = new Comment()
            .name(DEFAULT_NAME)
            .body(DEFAULT_BODY)
            .email(DEFAULT_EMAIL);
        return comment;
    }

    @Before
    public void initTest() {
        comment = createEntity(em);
    }

    @Test
    @Transactional
    public void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);
        restCommentMockMvc.perform(post("/api/comments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testComment.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testComment.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void createCommentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();

        // Create the Comment with an existing ID
        comment.setId(1L);
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentMockMvc.perform(post("/api/comments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setName(null);

        // Create the Comment, which fails.
        CommentDTO commentDTO = commentMapper.toDto(comment);

        restCommentMockMvc.perform(post("/api/comments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isBadRequest());

        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setEmail(null);

        // Create the Comment, which fails.
        CommentDTO commentDTO = commentMapper.toDto(comment);

        restCommentMockMvc.perform(post("/api/comments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isBadRequest());

        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllComments() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())));
    }
    
    @Test
    @Transactional
    public void getComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()));
    }

    @Test
    @Transactional
    public void getAllCommentsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where name equals to DEFAULT_NAME
        defaultCommentShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the commentList where name equals to UPDATED_NAME
        defaultCommentShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCommentsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCommentShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the commentList where name equals to UPDATED_NAME
        defaultCommentShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCommentsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where name is not null
        defaultCommentShouldBeFound("name.specified=true");

        // Get all the commentList where name is null
        defaultCommentShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommentsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where email equals to DEFAULT_EMAIL
        defaultCommentShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the commentList where email equals to UPDATED_EMAIL
        defaultCommentShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllCommentsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultCommentShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the commentList where email equals to UPDATED_EMAIL
        defaultCommentShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllCommentsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where email is not null
        defaultCommentShouldBeFound("email.specified=true");

        // Get all the commentList where email is null
        defaultCommentShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommentsByBlogPostIsEqualToSomething() throws Exception {
        // Initialize the database
        BlogPost blogPost = BlogPostResourceIntTest.createEntity(em);
        em.persist(blogPost);
        em.flush();
        comment.setBlogPost(blogPost);
        commentRepository.saveAndFlush(comment);
        Long blogPostId = blogPost.getId();

        // Get all the commentList where blogPost equals to blogPostId
        defaultCommentShouldBeFound("blogPostId.equals=" + blogPostId);

        // Get all the commentList where blogPost equals to blogPostId + 1
        defaultCommentShouldNotBeFound("blogPostId.equals=" + (blogPostId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCommentShouldBeFound(String filter) throws Exception {
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));

        // Check, that the count call also returns 1
        restCommentMockMvc.perform(get("/api/comments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCommentShouldNotBeFound(String filter) throws Exception {
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommentMockMvc.perform(get("/api/comments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingComment() throws Exception {
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Update the comment
        Comment updatedComment = commentRepository.findById(comment.getId()).get();
        // Disconnect from session so that the updates on updatedComment are not directly saved in db
        em.detach(updatedComment);
        updatedComment
            .name(UPDATED_NAME)
            .body(UPDATED_BODY)
            .email(UPDATED_EMAIL);
        CommentDTO commentDTO = commentMapper.toDto(updatedComment);

        restCommentMockMvc.perform(put("/api/comments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testComment.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testComment.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void updateNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMockMvc.perform(put("/api/comments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // Delete the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(comment1.getId());
        assertThat(comment1).isEqualTo(comment2);
        comment2.setId(2L);
        assertThat(comment1).isNotEqualTo(comment2);
        comment1.setId(null);
        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommentDTO.class);
        CommentDTO commentDTO1 = new CommentDTO();
        commentDTO1.setId(1L);
        CommentDTO commentDTO2 = new CommentDTO();
        assertThat(commentDTO1).isNotEqualTo(commentDTO2);
        commentDTO2.setId(commentDTO1.getId());
        assertThat(commentDTO1).isEqualTo(commentDTO2);
        commentDTO2.setId(2L);
        assertThat(commentDTO1).isNotEqualTo(commentDTO2);
        commentDTO1.setId(null);
        assertThat(commentDTO1).isNotEqualTo(commentDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(commentMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(commentMapper.fromId(null)).isNull();
    }
}
