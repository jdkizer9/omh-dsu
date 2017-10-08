package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;

import io.smalldata.ohmageomh.domain.DataType;
import io.smalldata.ohmageomh.repository.DataTypeRepository;
import io.smalldata.ohmageomh.service.DataTypeService;
import io.smalldata.ohmageomh.repository.search.DataTypeSearchRepository;
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
 * Test class for the DataTypeResource REST controller.
 *
 * @see DataTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OhmageApp.class)
public class DataTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SCHEMA_NAMESPACE = "AAAAAAAAAA";
    private static final String UPDATED_SCHEMA_NAMESPACE = "BBBBBBBBBB";

    private static final String DEFAULT_SCHEMA_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SCHEMA_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SCHEMA_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_SCHEMA_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_CSV_MAPPER = "AAAAAAAAAA";
    private static final String UPDATED_CSV_MAPPER = "BBBBBBBBBB";

    @Autowired
    private DataTypeRepository dataTypeRepository;

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private DataTypeSearchRepository dataTypeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDataTypeMockMvc;

    private DataType dataType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DataTypeResource dataTypeResource = new DataTypeResource(dataTypeService);
        this.restDataTypeMockMvc = MockMvcBuilders.standaloneSetup(dataTypeResource)
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
    public static DataType createEntity(EntityManager em) {
        DataType dataType = new DataType();
        dataType.setName(DEFAULT_NAME);
        dataType.setDescription(DEFAULT_DESCRIPTION);
        dataType.setSchemaNamespace(DEFAULT_SCHEMA_NAMESPACE);
        dataType.setSchemaName(DEFAULT_SCHEMA_NAME);
        dataType.setSchemaVersion(DEFAULT_SCHEMA_VERSION);
        dataType.setCsvMapper(DEFAULT_CSV_MAPPER);
        return dataType;
    }

    @Before
    public void initTest() {
        dataTypeSearchRepository.deleteAll();
        dataType = createEntity(em);
    }

    @Test
    @Transactional
    public void createDataType() throws Exception {
        int databaseSizeBeforeCreate = dataTypeRepository.findAll().size();

        // Create the DataType
        restDataTypeMockMvc.perform(post("/api/data-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataType)))
            .andExpect(status().isCreated());

        // Validate the DataType in the database
        List<DataType> dataTypeList = dataTypeRepository.findAll();
        assertThat(dataTypeList).hasSize(databaseSizeBeforeCreate + 1);
        DataType testDataType = dataTypeList.get(dataTypeList.size() - 1);
        assertThat(testDataType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDataType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDataType.getSchemaNamespace()).isEqualTo(DEFAULT_SCHEMA_NAMESPACE);
        assertThat(testDataType.getSchemaName()).isEqualTo(DEFAULT_SCHEMA_NAME);
        assertThat(testDataType.getSchemaVersion()).isEqualTo(DEFAULT_SCHEMA_VERSION);
        assertThat(testDataType.getCsvMapper()).isEqualTo(DEFAULT_CSV_MAPPER);

        // Validate the DataType in Elasticsearch
        DataType dataTypeEs = dataTypeSearchRepository.findOne(testDataType.getId());
        assertThat(dataTypeEs).isEqualToComparingFieldByField(testDataType);
    }

    @Test
    @Transactional
    public void createDataTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dataTypeRepository.findAll().size();

        // Create the DataType with an existing ID
        dataType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDataTypeMockMvc.perform(post("/api/data-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataType)))
            .andExpect(status().isBadRequest());

        // Validate the DataType in the database
        List<DataType> dataTypeList = dataTypeRepository.findAll();
        assertThat(dataTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataTypeRepository.findAll().size();
        // set the field null
        dataType.setName(null);

        // Create the DataType, which fails.

        restDataTypeMockMvc.perform(post("/api/data-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataType)))
            .andExpect(status().isBadRequest());

        List<DataType> dataTypeList = dataTypeRepository.findAll();
        assertThat(dataTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDataTypes() throws Exception {
        // Initialize the database
        dataTypeRepository.saveAndFlush(dataType);

        // Get all the dataTypeList
        restDataTypeMockMvc.perform(get("/api/data-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].schemaNamespace").value(hasItem(DEFAULT_SCHEMA_NAMESPACE.toString())))
            .andExpect(jsonPath("$.[*].schemaName").value(hasItem(DEFAULT_SCHEMA_NAME.toString())))
            .andExpect(jsonPath("$.[*].schemaVersion").value(hasItem(DEFAULT_SCHEMA_VERSION.toString())))
            .andExpect(jsonPath("$.[*].csvMapper").value(hasItem(DEFAULT_CSV_MAPPER.toString())));
    }

    @Test
    @Transactional
    public void getDataType() throws Exception {
        // Initialize the database
        dataTypeRepository.saveAndFlush(dataType);

        // Get the dataType
        restDataTypeMockMvc.perform(get("/api/data-types/{id}", dataType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dataType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.schemaNamespace").value(DEFAULT_SCHEMA_NAMESPACE.toString()))
            .andExpect(jsonPath("$.schemaName").value(DEFAULT_SCHEMA_NAME.toString()))
            .andExpect(jsonPath("$.schemaVersion").value(DEFAULT_SCHEMA_VERSION.toString()))
            .andExpect(jsonPath("$.csvMapper").value(DEFAULT_CSV_MAPPER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDataType() throws Exception {
        // Get the dataType
        restDataTypeMockMvc.perform(get("/api/data-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDataType() throws Exception {
        // Initialize the database
        dataTypeService.save(dataType);

        int databaseSizeBeforeUpdate = dataTypeRepository.findAll().size();

        // Update the dataType
        DataType updatedDataType = dataTypeRepository.findOne(dataType.getId());
        updatedDataType.setName(UPDATED_NAME);
        updatedDataType.setDescription(UPDATED_DESCRIPTION);
        updatedDataType.setSchemaNamespace(UPDATED_SCHEMA_NAMESPACE);
        updatedDataType.setSchemaName(UPDATED_SCHEMA_NAME);
        updatedDataType.setSchemaVersion(UPDATED_SCHEMA_VERSION);
        updatedDataType.setCsvMapper(UPDATED_CSV_MAPPER);

        restDataTypeMockMvc.perform(put("/api/data-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDataType)))
            .andExpect(status().isOk());

        // Validate the DataType in the database
        List<DataType> dataTypeList = dataTypeRepository.findAll();
        assertThat(dataTypeList).hasSize(databaseSizeBeforeUpdate);
        DataType testDataType = dataTypeList.get(dataTypeList.size() - 1);
        assertThat(testDataType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDataType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDataType.getSchemaNamespace()).isEqualTo(UPDATED_SCHEMA_NAMESPACE);
        assertThat(testDataType.getSchemaName()).isEqualTo(UPDATED_SCHEMA_NAME);
        assertThat(testDataType.getSchemaVersion()).isEqualTo(UPDATED_SCHEMA_VERSION);
        assertThat(testDataType.getCsvMapper()).isEqualTo(UPDATED_CSV_MAPPER);

        // Validate the DataType in Elasticsearch
        DataType dataTypeEs = dataTypeSearchRepository.findOne(testDataType.getId());
        assertThat(dataTypeEs).isEqualToComparingFieldByField(testDataType);
    }

    @Test
    @Transactional
    public void updateNonExistingDataType() throws Exception {
        int databaseSizeBeforeUpdate = dataTypeRepository.findAll().size();

        // Create the DataType

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDataTypeMockMvc.perform(put("/api/data-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataType)))
            .andExpect(status().isCreated());

        // Validate the DataType in the database
        List<DataType> dataTypeList = dataTypeRepository.findAll();
        assertThat(dataTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDataType() throws Exception {
        // Initialize the database
        dataTypeService.save(dataType);

        int databaseSizeBeforeDelete = dataTypeRepository.findAll().size();

        // Get the dataType
        restDataTypeMockMvc.perform(delete("/api/data-types/{id}", dataType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean dataTypeExistsInEs = dataTypeSearchRepository.exists(dataType.getId());
        assertThat(dataTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<DataType> dataTypeList = dataTypeRepository.findAll();
        assertThat(dataTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDataType() throws Exception {
        // Initialize the database
        dataTypeService.save(dataType);

        // Search the dataType
        restDataTypeMockMvc.perform(get("/api/_search/data-types?query=id:" + dataType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].schemaNamespace").value(hasItem(DEFAULT_SCHEMA_NAMESPACE.toString())))
            .andExpect(jsonPath("$.[*].schemaName").value(hasItem(DEFAULT_SCHEMA_NAME.toString())))
            .andExpect(jsonPath("$.[*].schemaVersion").value(hasItem(DEFAULT_SCHEMA_VERSION.toString())))
            .andExpect(jsonPath("$.[*].csvMapper").value(hasItem(DEFAULT_CSV_MAPPER.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DataType.class);
        DataType dataType1 = new DataType();
        dataType1.setId(1L);
        DataType dataType2 = new DataType();
        dataType2.setId(dataType1.getId());
        assertThat(dataType1).isEqualTo(dataType2);
        dataType2.setId(2L);
        assertThat(dataType1).isNotEqualTo(dataType2);
        dataType1.setId(null);
        assertThat(dataType1).isNotEqualTo(dataType2);
    }
}
