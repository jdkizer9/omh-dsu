package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;

import io.smalldata.ohmageomh.domain.Participant;
import io.smalldata.ohmageomh.repository.ParticipantRepository;
import io.smalldata.ohmageomh.service.ParticipantService;
import io.smalldata.ohmageomh.repository.search.ParticipantSearchRepository;
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
 * Test class for the ParticipantResource REST controller.
 *
 * @see ParticipantResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OhmageApp.class)
public class ParticipantResourceIntTest {

    private static final String DEFAULT_DSU_ID = "AAAAAAAAAA";
    private static final String UPDATED_DSU_ID = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ParticipantSearchRepository participantSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restParticipantMockMvc;

    private Participant participant;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ParticipantResource participantResource = new ParticipantResource(participantService);
        this.restParticipantMockMvc = MockMvcBuilders.standaloneSetup(participantResource)
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
    public static Participant createEntity(EntityManager em) {
        Participant participant = new Participant();
        participant.setDsuId(DEFAULT_DSU_ID);
        participant.setLabel(DEFAULT_LABEL);
        return participant;
    }

    @Before
    public void initTest() {
        participantSearchRepository.deleteAll();
        participant = createEntity(em);
    }

    @Test
    @Transactional
    public void createParticipant() throws Exception {
        int databaseSizeBeforeCreate = participantRepository.findAll().size();

        // Create the Participant
        restParticipantMockMvc.perform(post("/api/participants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participant)))
            .andExpect(status().isCreated());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeCreate + 1);
        Participant testParticipant = participantList.get(participantList.size() - 1);
        assertThat(testParticipant.getDsuId()).isEqualTo(DEFAULT_DSU_ID);
        assertThat(testParticipant.getLabel()).isEqualTo(DEFAULT_LABEL);

        // Validate the Participant in Elasticsearch
        Participant participantEs = participantSearchRepository.findOne(testParticipant.getId());
        assertThat(participantEs).isEqualToComparingFieldByField(testParticipant);
    }

    @Test
    @Transactional
    public void createParticipantWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = participantRepository.findAll().size();

        // Create the Participant with an existing ID
        participant.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantMockMvc.perform(post("/api/participants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participant)))
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDsuIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = participantRepository.findAll().size();
        // set the field null
        participant.setDsuId(null);

        // Create the Participant, which fails.

        restParticipantMockMvc.perform(post("/api/participants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participant)))
            .andExpect(status().isBadRequest());

        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParticipants() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList
        restParticipantMockMvc.perform(get("/api/participants?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].dsuId").value(hasItem(DEFAULT_DSU_ID.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }

    @Test
    @Transactional
    public void getParticipant() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get the participant
        restParticipantMockMvc.perform(get("/api/participants/{id}", participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(participant.getId().intValue()))
            .andExpect(jsonPath("$.dsuId").value(DEFAULT_DSU_ID.toString()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingParticipant() throws Exception {
        // Get the participant
        restParticipantMockMvc.perform(get("/api/participants/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParticipant() throws Exception {
        // Initialize the database
        participantService.save(participant);

        int databaseSizeBeforeUpdate = participantRepository.findAll().size();

        // Update the participant
        Participant updatedParticipant = participantRepository.findOne(participant.getId());
        updatedParticipant.setDsuId(UPDATED_DSU_ID);
        updatedParticipant.setLabel(UPDATED_LABEL);

        restParticipantMockMvc.perform(put("/api/participants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedParticipant)))
            .andExpect(status().isOk());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate);
        Participant testParticipant = participantList.get(participantList.size() - 1);
        assertThat(testParticipant.getDsuId()).isEqualTo(UPDATED_DSU_ID);
        assertThat(testParticipant.getLabel()).isEqualTo(UPDATED_LABEL);

        // Validate the Participant in Elasticsearch
        Participant participantEs = participantSearchRepository.findOne(testParticipant.getId());
        assertThat(participantEs).isEqualToComparingFieldByField(testParticipant);
    }

    @Test
    @Transactional
    public void updateNonExistingParticipant() throws Exception {
        int databaseSizeBeforeUpdate = participantRepository.findAll().size();

        // Create the Participant

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restParticipantMockMvc.perform(put("/api/participants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(participant)))
            .andExpect(status().isCreated());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteParticipant() throws Exception {
        // Initialize the database
        participantService.save(participant);

        int databaseSizeBeforeDelete = participantRepository.findAll().size();

        // Get the participant
        restParticipantMockMvc.perform(delete("/api/participants/{id}", participant.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean participantExistsInEs = participantSearchRepository.exists(participant.getId());
        assertThat(participantExistsInEs).isFalse();

        // Validate the database is empty
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchParticipant() throws Exception {
        // Initialize the database
        participantService.save(participant);

        // Search the participant
        restParticipantMockMvc.perform(get("/api/_search/participants?query=id:" + participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].dsuId").value(hasItem(DEFAULT_DSU_ID.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Participant.class);
        Participant participant1 = new Participant();
        participant1.setId(1L);
        Participant participant2 = new Participant();
        participant2.setId(participant1.getId());
        assertThat(participant1).isEqualTo(participant2);
        participant2.setId(2L);
        assertThat(participant1).isNotEqualTo(participant2);
        participant1.setId(null);
        assertThat(participant1).isNotEqualTo(participant2);
    }
}
