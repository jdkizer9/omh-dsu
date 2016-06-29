package io.smalldata.ohmageomh.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smalldata.ohmageomh.data.domain.DataPointSearchCriteria;
import io.smalldata.ohmageomh.data.domain.LastDataPointDate;
import io.smalldata.ohmageomh.data.service.DataPointService;
import io.smalldata.ohmageomh.domain.DataType;
import io.smalldata.ohmageomh.domain.Study;
import io.smalldata.ohmageomh.domain.Survey;
import io.smalldata.ohmageomh.service.ParticipantService;
import io.smalldata.ohmageomh.domain.Participant;
import io.smalldata.ohmageomh.repository.ParticipantRepository;
import io.smalldata.ohmageomh.repository.search.ParticipantSearchRepository;
import io.smalldata.ohmageomh.web.rest.dto.ParticipantSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Participant.
 */
@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService{

    private final Logger log = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    @Inject
    private ParticipantRepository participantRepository;

    @Inject
    private ParticipantSearchRepository participantSearchRepository;

    @Inject
    private DataPointService dataPointService;

    /**
     * Save a participant.
     *
     * @param participant the entity to save
     * @return the persisted entity
     */
    public Participant save(Participant participant) {
        log.debug("Request to save Participant : {}", participant);
        Participant result = participantRepository.save(participant);
        participantSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the participants.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Participant> findAll(Pageable pageable) {
        log.debug("Request to get all Participants");
        Page<Participant> result = participantRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get all the participants in a study.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Participant> findAllByStudy(Study study, Pageable pageable) {
        log.debug("Request to get all Participants");
        Page<Participant> result = participantRepository.findAllByStudies(study, pageable);
        return result;
    }

    /**
     *  Get all the participant summaries in a study.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ParticipantSummaryDTO> findAllSummariesByStudy(Study study, Pageable pageable) {
        log.debug("Request to get all participant summaries");

        // TODO this is pretty inefficient

        // Create initial DTO items
        Page<Participant> page = participantRepository.findAllByStudies(study, pageable);
        List<ParticipantSummaryDTO> dtos = new ArrayList<ParticipantSummaryDTO>();
        for(Participant participant : page) {
            ParticipantSummaryDTO dto = new ParticipantSummaryDTO(participant);
            dtos.add(dto);
        }

        // Get data types and query params
        List<DataType> dataTypes = new ArrayList<DataType>();
        study.getIntegrations().forEach(integration -> integration.getDataTypes().forEach(dataType -> dataTypes.add(dataType)));
        List<String> userIds = page.getContent().stream().map(Participant::getDsuId).collect(Collectors.toList());

        // Update summaries for each data type response
        for(DataType dataType : dataTypes){
            // If the data type is survey responses, handle it specially
            if(dataType.getSchemaNamespace().equals("ohmageomh") && dataType.getSchemaName().equals("survey-response")) {
                // Handle surveys
                for(Survey survey : study.getSurveys()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode definition = mapper.readTree(survey.getDefinition());

                        DataPointSearchCriteria searchCriteria =
                            new DataPointSearchCriteria("dummy", definition.get("schema_id").get("namespace").asText(), definition.get("schema_id").get("name").asText(),
                                definition.get("schema_id").get("version").asText()); // We use a dummy userId, for now

                        List<LastDataPointDate> dates = dataPointService.findLastDataPointDate(userIds, searchCriteria, dataType.getDateField());

                        for(LastDataPointDate date : dates){
                            for(ParticipantSummaryDTO dto : dtos) {
                                if(date.getUserId().equals(dto.getDsuId())) {
                                    if(!dto.getLatestDataPointDates().containsKey(dataType.getId().toString())) {
                                        dto.addLatestDataPointDate(dataType.getId().toString(), date.getDate());
                                    } else {
                                        if(dto.getLatestDataPointDates().get(dataType.getId().toString()).compareTo(date.getDate()) <= 0) {
                                            dto.getLatestDataPointDates().replace(dataType.getId().toString(), date.getDate());
                                        }
                                    }
                                    break;
                                }
                            }
                        }

                    } catch (Exception ex) {
                        break;
                    }
                }
            } else {
                // Handle normal data points
                DataPointSearchCriteria searchCriteria =
                    new DataPointSearchCriteria("dummy", dataType.getSchemaNamespace(), dataType.getSchemaName(),
                        dataType.getSchemaVersion()); // We use a dummy userId, for now

                List<LastDataPointDate> dates = dataPointService.findLastDataPointDate(userIds, searchCriteria, dataType.getDateField());

                for(LastDataPointDate date : dates){
                    for(ParticipantSummaryDTO dto : dtos) {
                        if(date.getUserId().equals(dto.getDsuId())) {
                            dto.addLatestDataPointDate(dataType.getId().toString(), date.getDate());
                            break;
                        }
                    }
                }
            }

        }

        return new PageImpl<ParticipantSummaryDTO>(dtos, pageable, page.getTotalElements());
    }


    /**
     *  Get one participant by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Participant findOne(Long id) {
        log.debug("Request to get Participant : {}", id);
        Participant participant = participantRepository.findOneWithEagerRelationships(id);
        return participant;
    }

    /**
     *  Delete the  participant by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Participant : {}", id);
        participantRepository.delete(id);
        participantSearchRepository.delete(id);
    }

    /**
     * Search for the participant corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Participant> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Participants for query {}", query);
        return participantSearchRepository.search(queryStringQuery(query), pageable);
    }
}
