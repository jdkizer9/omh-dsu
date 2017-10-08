package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;

import io.smalldata.ohmageomh.domain.Study;
import io.smalldata.ohmageomh.repository.StudyRepository;
import io.smalldata.ohmageomh.service.StudyService;
import io.smalldata.ohmageomh.repository.search.StudySearchRepository;
import io.smalldata.ohmageomh.web.rest.errors.ExceptionTranslator;

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

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StudyResource REST controller.
 *
 * @see StudyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OhmageApp.class)
public class StudyResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_REMOVE_GPS = false;
    private static final Boolean UPDATED_REMOVE_GPS = true;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudySearchRepository studySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restStudyMockMvc;

    private Study study;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StudyResource studyResource = new StudyResource(studyService);
        this.restStudyMockMvc = MockMvcBuilders.standaloneSetup(studyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Study createEntity(EntityManager em) {
        Study study = new Study();
        study.setName(DEFAULT_NAME);
        study.setRemoveGps(DEFAULT_REMOVE_GPS);
        study.setStartDate(DEFAULT_START_DATE);
        study.setEndDate(DEFAULT_END_DATE);
        return study;
    }

    @Before
    public void initTest() {
        studySearchRepository.deleteAll();
        study = createEntity(em);
    }

    @Test
    @Transactional
    public void createStudy() throws Exception {
        int databaseSizeBeforeCreate = studyRepository.findAll().size();

        // Create the Study
        restStudyMockMvc.perform(post("/api/studies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(study)))
            .andExpect(status().isCreated());

        // Validate the Study in the database
        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(databaseSizeBeforeCreate + 1);
        Study testStudy = studyList.get(studyList.size() - 1);
        assertThat(testStudy.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStudy.isRemoveGps()).isEqualTo(DEFAULT_REMOVE_GPS);
        assertThat(testStudy.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testStudy.getEndDate()).isEqualTo(DEFAULT_END_DATE);

        // Validate the Study in Elasticsearch
        Study studyEs = studySearchRepository.findOne(testStudy.getId());
        assertThat(studyEs).isEqualToComparingFieldByField(testStudy);
    }

    @Test
    @Transactional
    public void createStudyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = studyRepository.findAll().size();

        // Create the Study with an existing ID
        study.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudyMockMvc.perform(post("/api/studies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(study)))
            .andExpect(status().isBadRequest());

        // Validate the Study in the database
        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = studyRepository.findAll().size();
        // set the field null
        study.setName(null);

        // Create the Study, which fails.

        restStudyMockMvc.perform(post("/api/studies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(study)))
            .andExpect(status().isBadRequest());

        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRemoveGpsIsRequired() throws Exception {
        int databaseSizeBeforeTest = studyRepository.findAll().size();
        // set the field null
        study.setRemoveGps(null);

        // Create the Study, which fails.

        restStudyMockMvc.perform(post("/api/studies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(study)))
            .andExpect(status().isBadRequest());

        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStudies() throws Exception {
        // Initialize the database
        studyRepository.saveAndFlush(study);

        // Get all the studyList
        restStudyMockMvc.perform(get("/api/studies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(study.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].removeGps").value(hasItem(DEFAULT_REMOVE_GPS.booleanValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    public void getStudy() throws Exception {
        // Initialize the database
        studyRepository.saveAndFlush(study);

        // Get the study
        restStudyMockMvc.perform(get("/api/studies/{id}", study.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(study.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.removeGps").value(DEFAULT_REMOVE_GPS.booleanValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStudy() throws Exception {
        // Get the study
        restStudyMockMvc.perform(get("/api/studies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStudy() throws Exception {
        // Initialize the database
        studyService.save(study);

        int databaseSizeBeforeUpdate = studyRepository.findAll().size();

        // Update the study
        Study updatedStudy = studyRepository.findOne(study.getId());
        updatedStudy.setName(UPDATED_NAME);
        updatedStudy.setRemoveGps(UPDATED_REMOVE_GPS);
        updatedStudy.setStartDate(UPDATED_START_DATE);
        updatedStudy.setEndDate(UPDATED_END_DATE);

        restStudyMockMvc.perform(put("/api/studies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedStudy)))
            .andExpect(status().isOk());

        // Validate the Study in the database
        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(databaseSizeBeforeUpdate);
        Study testStudy = studyList.get(studyList.size() - 1);
        assertThat(testStudy.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStudy.isRemoveGps()).isEqualTo(UPDATED_REMOVE_GPS);
        assertThat(testStudy.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testStudy.getEndDate()).isEqualTo(UPDATED_END_DATE);

        // Validate the Study in Elasticsearch
        Study studyEs = studySearchRepository.findOne(testStudy.getId());
        assertThat(studyEs).isEqualToComparingFieldByField(testStudy);
    }

    @Test
    @Transactional
    public void updateNonExistingStudy() throws Exception {
        int databaseSizeBeforeUpdate = studyRepository.findAll().size();

        // Create the Study

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restStudyMockMvc.perform(put("/api/studies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(study)))
            .andExpect(status().isCreated());

        // Validate the Study in the database
        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteStudy() throws Exception {
        // Initialize the database
        studyService.save(study);

        int databaseSizeBeforeDelete = studyRepository.findAll().size();

        // Get the study
        restStudyMockMvc.perform(delete("/api/studies/{id}", study.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean studyExistsInEs = studySearchRepository.exists(study.getId());
        assertThat(studyExistsInEs).isFalse();

        // Validate the database is empty
        List<Study> studyList = studyRepository.findAll();
        assertThat(studyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStudy() throws Exception {
        // Initialize the database
        studyService.save(study);

        // Search the study
        restStudyMockMvc.perform(get("/api/_search/studies?query=id:" + study.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(study.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].removeGps").value(hasItem(DEFAULT_REMOVE_GPS.booleanValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Study.class);
        Study study1 = new Study();
        study1.setId(1L);
        Study study2 = new Study();
        study2.setId(study1.getId());
        assertThat(study1).isEqualTo(study2);
        study2.setId(2L);
        assertThat(study1).isNotEqualTo(study2);
        study1.setId(null);
        assertThat(study1).isNotEqualTo(study2);
    }
}
