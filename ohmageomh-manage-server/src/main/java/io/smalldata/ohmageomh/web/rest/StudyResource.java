package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.smalldata.ohmageomh.domain.Study;
import io.smalldata.ohmageomh.service.StudyService;
import io.smalldata.ohmageomh.web.rest.util.HeaderUtil;
import io.smalldata.ohmageomh.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Study.
 */
@RestController
@RequestMapping("/api")
public class StudyResource {

    private final Logger log = LoggerFactory.getLogger(StudyResource.class);

    private static final String ENTITY_NAME = "study";

    private final StudyService studyService;

    public StudyResource(StudyService studyService) {
        this.studyService = studyService;
    }

    /**
     * POST  /studies : Create a new study.
     *
     * @param study the study to create
     * @return the ResponseEntity with status 201 (Created) and with body the new study, or with status 400 (Bad Request) if the study has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/studies")
    @Timed
    public ResponseEntity<Study> createStudy(@Valid @RequestBody Study study) throws URISyntaxException {
        log.debug("REST request to save Study : {}", study);
        if (study.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new study cannot already have an ID")).body(null);
        }
        Study result = studyService.save(study);
        return ResponseEntity.created(new URI("/api/studies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /studies : Updates an existing study.
     *
     * @param study the study to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated study,
     * or with status 400 (Bad Request) if the study is not valid,
     * or with status 500 (Internal Server Error) if the study couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/studies")
    @Timed
    public ResponseEntity<Study> updateStudy(@Valid @RequestBody Study study) throws URISyntaxException {
        log.debug("REST request to update Study : {}", study);
        if (study.getId() == null) {
            return createStudy(study);
        }
        Study result = studyService.save(study);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, study.getId().toString()))
            .body(result);
    }

    /**
     * GET  /studies : get all the studies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of studies in body
     */
    @GetMapping("/studies")
    @Timed
    public ResponseEntity<List<Study>> getAllStudies(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Studies");
        Page<Study> page = studyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/studies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /studies/:id : get the "id" study.
     *
     * @param id the id of the study to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the study, or with status 404 (Not Found)
     */
    @GetMapping("/studies/{id}")
    @Timed
    public ResponseEntity<Study> getStudy(@PathVariable Long id) {
        log.debug("REST request to get Study : {}", id);
        Study study = studyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(study));
    }

    /**
     * DELETE  /studies/:id : delete the "id" study.
     *
     * @param id the id of the study to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/studies/{id}")
    @Timed
    public ResponseEntity<Void> deleteStudy(@PathVariable Long id) {
        log.debug("REST request to delete Study : {}", id);
        studyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/studies?query=:query : search for the study corresponding
     * to the query.
     *
     * @param query the query of the study search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/studies")
    @Timed
    public ResponseEntity<List<Study>> searchStudies(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Studies for query {}", query);
        Page<Study> page = studyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/studies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
