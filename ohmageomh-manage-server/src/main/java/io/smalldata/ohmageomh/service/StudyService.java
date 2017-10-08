package io.smalldata.ohmageomh.service;

import io.smalldata.ohmageomh.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Study.
 */
public interface StudyService {

    /**
     * Save a study.
     *
     * @param study the entity to save
     * @return the persisted entity
     */
    Study save(Study study);

    /**
     *  Get all the studies.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Study> findAll(Pageable pageable);

    /**
     *  Get the "id" study.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Study findOne(Long id);

    /**
     *  Delete the "id" study.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the study corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Study> search(String query, Pageable pageable);
}
