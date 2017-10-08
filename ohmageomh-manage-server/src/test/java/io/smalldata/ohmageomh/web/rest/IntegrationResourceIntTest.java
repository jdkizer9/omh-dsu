package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;

import io.smalldata.ohmageomh.domain.Integration;
import io.smalldata.ohmageomh.repository.IntegrationRepository;
import io.smalldata.ohmageomh.service.IntegrationService;
import io.smalldata.ohmageomh.repository.search.IntegrationSearchRepository;
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
 * Test class for the IntegrationResource REST controller.
 *
 * @see IntegrationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OhmageApp.class)
public class IntegrationResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private IntegrationRepository integrationRepository;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private IntegrationSearchRepository integrationSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restIntegrationMockMvc;

    private Integration integration;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IntegrationResource integrationResource = new IntegrationResource(integrationService);
        this.restIntegrationMockMvc = MockMvcBuilders.standaloneSetup(integrationResource)
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
    public static Integration createEntity(EntityManager em) {
        Integration integration = new Integration();
        integration.setName(DEFAULT_NAME);
        integration.setDescription(DEFAULT_DESCRIPTION);
        return integration;
    }

    @Before
    public void initTest() {
        integrationSearchRepository.deleteAll();
        integration = createEntity(em);
    }

    @Test
    @Transactional
    public void createIntegration() throws Exception {
        int databaseSizeBeforeCreate = integrationRepository.findAll().size();

        // Create the Integration
        restIntegrationMockMvc.perform(post("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isCreated());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeCreate + 1);
        Integration testIntegration = integrationList.get(integrationList.size() - 1);
        assertThat(testIntegration.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIntegration.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Integration in Elasticsearch
        Integration integrationEs = integrationSearchRepository.findOne(testIntegration.getId());
        assertThat(integrationEs).isEqualToComparingFieldByField(testIntegration);
    }

    @Test
    @Transactional
    public void createIntegrationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = integrationRepository.findAll().size();

        // Create the Integration with an existing ID
        integration.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntegrationMockMvc.perform(post("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isBadRequest());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = integrationRepository.findAll().size();
        // set the field null
        integration.setName(null);

        // Create the Integration, which fails.

        restIntegrationMockMvc.perform(post("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isBadRequest());

        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllIntegrations() throws Exception {
        // Initialize the database
        integrationRepository.saveAndFlush(integration);

        // Get all the integrationList
        restIntegrationMockMvc.perform(get("/api/integrations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integration.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getIntegration() throws Exception {
        // Initialize the database
        integrationRepository.saveAndFlush(integration);

        // Get the integration
        restIntegrationMockMvc.perform(get("/api/integrations/{id}", integration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(integration.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingIntegration() throws Exception {
        // Get the integration
        restIntegrationMockMvc.perform(get("/api/integrations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);

        int databaseSizeBeforeUpdate = integrationRepository.findAll().size();

        // Update the integration
        Integration updatedIntegration = integrationRepository.findOne(integration.getId());
        updatedIntegration.setName(UPDATED_NAME);
        updatedIntegration.setDescription(UPDATED_DESCRIPTION);

        restIntegrationMockMvc.perform(put("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedIntegration)))
            .andExpect(status().isOk());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeUpdate);
        Integration testIntegration = integrationList.get(integrationList.size() - 1);
        assertThat(testIntegration.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIntegration.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Integration in Elasticsearch
        Integration integrationEs = integrationSearchRepository.findOne(testIntegration.getId());
        assertThat(integrationEs).isEqualToComparingFieldByField(testIntegration);
    }

    @Test
    @Transactional
    public void updateNonExistingIntegration() throws Exception {
        int databaseSizeBeforeUpdate = integrationRepository.findAll().size();

        // Create the Integration

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restIntegrationMockMvc.perform(put("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isCreated());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);

        int databaseSizeBeforeDelete = integrationRepository.findAll().size();

        // Get the integration
        restIntegrationMockMvc.perform(delete("/api/integrations/{id}", integration.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean integrationExistsInEs = integrationSearchRepository.exists(integration.getId());
        assertThat(integrationExistsInEs).isFalse();

        // Validate the database is empty
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);

        // Search the integration
        restIntegrationMockMvc.perform(get("/api/_search/integrations?query=id:" + integration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integration.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Integration.class);
        Integration integration1 = new Integration();
        integration1.setId(1L);
        Integration integration2 = new Integration();
        integration2.setId(integration1.getId());
        assertThat(integration1).isEqualTo(integration2);
        integration2.setId(2L);
        assertThat(integration1).isNotEqualTo(integration2);
        integration1.setId(null);
        assertThat(integration1).isNotEqualTo(integration2);
    }
}
