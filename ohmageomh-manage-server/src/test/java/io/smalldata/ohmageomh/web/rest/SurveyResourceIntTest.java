package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;

import io.smalldata.ohmageomh.domain.Survey;
import io.smalldata.ohmageomh.repository.SurveyRepository;
import io.smalldata.ohmageomh.service.SurveyService;
import io.smalldata.ohmageomh.repository.search.SurveySearchRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SurveyResource REST controller.
 *
 * @see SurveyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OhmageApp.class)
public class SurveyResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final String DEFAULT_DEFINITION = "AAAAAAAAAA";
    private static final String UPDATED_DEFINITION = "BBBBBBBBBB";

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveySearchRepository surveySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSurveyMockMvc;

    private Survey survey;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SurveyResource surveyResource = new SurveyResource(surveyService);
        this.restSurveyMockMvc = MockMvcBuilders.standaloneSetup(surveyResource)
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
    public static Survey createEntity(EntityManager em) {
        Survey survey = new Survey();
        survey.setName(DEFAULT_NAME);
        survey.setVersion(DEFAULT_VERSION);
        survey.setDescription(DEFAULT_DESCRIPTION);
        survey.setIsPublic(DEFAULT_IS_PUBLIC);
        survey.setDefinition(DEFAULT_DEFINITION);
        return survey;
    }

    @Before
    public void initTest() {
        surveySearchRepository.deleteAll();
        survey = createEntity(em);
    }

    @Test
    @Transactional
    public void createSurvey() throws Exception {
        int databaseSizeBeforeCreate = surveyRepository.findAll().size();

        // Create the Survey
        restSurveyMockMvc.perform(post("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(survey)))
            .andExpect(status().isCreated());

        // Validate the Survey in the database
        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeCreate + 1);
        Survey testSurvey = surveyList.get(surveyList.size() - 1);
        assertThat(testSurvey.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSurvey.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSurvey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSurvey.isIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        assertThat(testSurvey.getDefinition()).isEqualTo(DEFAULT_DEFINITION);

        // Validate the Survey in Elasticsearch
        Survey surveyEs = surveySearchRepository.findOne(testSurvey.getId());
        assertThat(surveyEs).isEqualToComparingFieldByField(testSurvey);
    }

    @Test
    @Transactional
    public void createSurveyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = surveyRepository.findAll().size();

        // Create the Survey with an existing ID
        survey.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSurveyMockMvc.perform(post("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(survey)))
            .andExpect(status().isBadRequest());

        // Validate the Survey in the database
        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setName(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(survey)))
            .andExpect(status().isBadRequest());

        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setVersion(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(survey)))
            .andExpect(status().isBadRequest());

        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsPublicIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setIsPublic(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(survey)))
            .andExpect(status().isBadRequest());

        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDefinitionIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setDefinition(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(survey)))
            .andExpect(status().isBadRequest());

        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSurveys() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(survey);

        // Get all the surveyList
        restSurveyMockMvc.perform(get("/api/surveys?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(survey.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())))
            .andExpect(jsonPath("$.[*].definition").value(hasItem(DEFAULT_DEFINITION.toString())));
    }

    @Test
    @Transactional
    public void getSurvey() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(survey);

        // Get the survey
        restSurveyMockMvc.perform(get("/api/surveys/{id}", survey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(survey.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC.booleanValue()))
            .andExpect(jsonPath("$.definition").value(DEFAULT_DEFINITION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSurvey() throws Exception {
        // Get the survey
        restSurveyMockMvc.perform(get("/api/surveys/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSurvey() throws Exception {
        // Initialize the database
        surveyService.save(survey);

        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();

        // Update the survey
        Survey updatedSurvey = surveyRepository.findOne(survey.getId());
        updatedSurvey.setName(UPDATED_NAME);
        updatedSurvey.setVersion(UPDATED_VERSION);
        updatedSurvey.setDescription(UPDATED_DESCRIPTION);
        updatedSurvey.setIsPublic(UPDATED_IS_PUBLIC);
        updatedSurvey.setDefinition(UPDATED_DEFINITION);

        restSurveyMockMvc.perform(put("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSurvey)))
            .andExpect(status().isOk());

        // Validate the Survey in the database
        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate);
        Survey testSurvey = surveyList.get(surveyList.size() - 1);
        assertThat(testSurvey.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSurvey.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSurvey.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSurvey.isIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
        assertThat(testSurvey.getDefinition()).isEqualTo(UPDATED_DEFINITION);

        // Validate the Survey in Elasticsearch
        Survey surveyEs = surveySearchRepository.findOne(testSurvey.getId());
        assertThat(surveyEs).isEqualToComparingFieldByField(testSurvey);
    }

    @Test
    @Transactional
    public void updateNonExistingSurvey() throws Exception {
        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();

        // Create the Survey

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSurveyMockMvc.perform(put("/api/surveys")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(survey)))
            .andExpect(status().isCreated());

        // Validate the Survey in the database
        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSurvey() throws Exception {
        // Initialize the database
        surveyService.save(survey);

        int databaseSizeBeforeDelete = surveyRepository.findAll().size();

        // Get the survey
        restSurveyMockMvc.perform(delete("/api/surveys/{id}", survey.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean surveyExistsInEs = surveySearchRepository.exists(survey.getId());
        assertThat(surveyExistsInEs).isFalse();

        // Validate the database is empty
        List<Survey> surveyList = surveyRepository.findAll();
        assertThat(surveyList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSurvey() throws Exception {
        // Initialize the database
        surveyService.save(survey);

        // Search the survey
        restSurveyMockMvc.perform(get("/api/_search/surveys?query=id:" + survey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(survey.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())))
            .andExpect(jsonPath("$.[*].definition").value(hasItem(DEFAULT_DEFINITION.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Survey.class);
        Survey survey1 = new Survey();
        survey1.setId(1L);
        Survey survey2 = new Survey();
        survey2.setId(survey1.getId());
        assertThat(survey1).isEqualTo(survey2);
        survey2.setId(2L);
        assertThat(survey1).isNotEqualTo(survey2);
        survey1.setId(null);
        assertThat(survey1).isNotEqualTo(survey2);
    }
}
