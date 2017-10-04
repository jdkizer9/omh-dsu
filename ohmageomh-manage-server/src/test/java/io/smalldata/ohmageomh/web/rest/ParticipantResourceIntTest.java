package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;
import io.smalldata.ohmageomh.domain.Participant;
import io.smalldata.ohmageomh.repository.ParticipantRepository;
import io.smalldata.ohmageomh.service.ParticipantService;
import io.smalldata.ohmageomh.repository.search.ParticipantSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ParticipantResource REST controller.
 *
 * @see ParticipantResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OhmageApp.class)
@WebAppConfiguration
public class ParticipantResourceIntTest {

    private static final String DEFAULT_DSU_ID = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DSU_ID = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_LABEL = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private ParticipantRepository participantRepository;

    @Inject
    private ParticipantService participantService;

    @Inject
    private ParticipantSearchRepository participantSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restParticipantMockMvc;

    private Participant participant;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ParticipantResource participantResource = new ParticipantResource();
        ReflectionTestUtils.setField(participantResource, "participantService", participantService);
        this.restParticipantMockMvc = MockMvcBuilders.standaloneSetup(participantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        participantSearchRepository.deleteAll();
        participant = new Participant();
        participant.setDsuId(DEFAULT_DSU_ID);
        participant.setLabel(DEFAULT_LABEL);
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
        List<Participant> participants = participantRepository.findAll();
        assertThat(participants).hasSize(databaseSizeBeforeCreate + 1);
        Participant testParticipant = participants.get(participants.size() - 1);
        assertThat(testParticipant.getDsuId()).isEqualTo(DEFAULT_DSU_ID);
        assertThat(testParticipant.getLabel()).isEqualTo(DEFAULT_LABEL);

        // Validate the Participant in ElasticSearch
        Participant participantEs = participantSearchRepository.findOne(testParticipant.getId());
        assertThat(participantEs).isEqualToComparingFieldByField(testParticipant);
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

        List<Participant> participants = participantRepository.findAll();
        assertThat(participants).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParticipants() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participants
        restParticipantMockMvc.perform(get("/api/participants?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
        Participant updatedParticipant = new Participant();
        updatedParticipant.setId(participant.getId());
        updatedParticipant.setDsuId(UPDATED_DSU_ID);
        updatedParticipant.setLabel(UPDATED_LABEL);

        restParticipantMockMvc.perform(put("/api/participants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedParticipant)))
                .andExpect(status().isOk());

        // Validate the Participant in the database
        List<Participant> participants = participantRepository.findAll();
        assertThat(participants).hasSize(databaseSizeBeforeUpdate);
        Participant testParticipant = participants.get(participants.size() - 1);
        assertThat(testParticipant.getDsuId()).isEqualTo(UPDATED_DSU_ID);
        assertThat(testParticipant.getLabel()).isEqualTo(UPDATED_LABEL);

        // Validate the Participant in ElasticSearch
        Participant participantEs = participantSearchRepository.findOne(testParticipant.getId());
        assertThat(participantEs).isEqualToComparingFieldByField(testParticipant);
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

        // Validate ElasticSearch is empty
        boolean participantExistsInEs = participantSearchRepository.exists(participant.getId());
        assertThat(participantExistsInEs).isFalse();

        // Validate the database is empty
        List<Participant> participants = participantRepository.findAll();
        assertThat(participants).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchParticipant() throws Exception {
        // Initialize the database
        participantService.save(participant);

        // Search the participant
        restParticipantMockMvc.perform(get("/api/_search/participants?query=id:" + participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].dsuId").value(hasItem(DEFAULT_DSU_ID.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }
}
