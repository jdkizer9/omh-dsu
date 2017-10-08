package io.smalldata.ohmageomh.service.impl;

import io.smalldata.ohmageomh.service.StudyService;
import io.smalldata.ohmageomh.domain.Study;
import io.smalldata.ohmageomh.repository.StudyRepository;
import io.smalldata.ohmageomh.repository.search.StudySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Study.
 */
@Service
@Transactional
public class StudyServiceImpl implements StudyService{

    private final Logger log = LoggerFactory.getLogger(StudyServiceImpl.class);

    private final StudyRepository studyRepository;

    private final StudySearchRepository studySearchRepository;

    public StudyServiceImpl(StudyRepository studyRepository, StudySearchRepository studySearchRepository) {
        this.studyRepository = studyRepository;
        this.studySearchRepository = studySearchRepository;
    }

    /**
     * Save a study.
     *
     * @param study the entity to save
     * @return the persisted entity
     */
    @Override
    public Study save(Study study) {
        log.debug("Request to save Study : {}", study);
        Study result = studyRepository.save(study);
        studySearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the studies.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Study> findAll(Pageable pageable) {
        log.debug("Request to get all Studies");
        return studyRepository.findAll(pageable);
    }

    /**
     *  Get one study by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Study findOne(Long id) {
        log.debug("Request to get Study : {}", id);
        return studyRepository.findOneWithEagerRelationships(id);
    }

    /**
     *  Delete the  study by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Study : {}", id);
        studyRepository.delete(id);
        studySearchRepository.delete(id);
    }

    /**
     * Search for the study corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Study> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Studies for query {}", query);
        Page<Study> result = studySearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
