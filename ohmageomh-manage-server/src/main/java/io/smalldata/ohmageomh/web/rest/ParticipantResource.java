package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.smalldata.ohmageomh.domain.Participant;
import io.smalldata.ohmageomh.service.ParticipantService;
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
 * REST controller for managing Participant.
 */
@RestController
@RequestMapping("/api")
public class ParticipantResource {

    private final Logger log = LoggerFactory.getLogger(ParticipantResource.class);

    private static final String ENTITY_NAME = "participant";

    private final ParticipantService participantService;

    public ParticipantResource(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * POST  /participants : Create a new participant.
     *
     * @param participant the participant to create
     * @return the ResponseEntity with status 201 (Created) and with body the new participant, or with status 400 (Bad Request) if the participant has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/participants")
    @Timed
    public ResponseEntity<Participant> createParticipant(@Valid @RequestBody Participant participant) throws URISyntaxException {
        log.debug("REST request to save Participant : {}", participant);
        if (participant.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new participant cannot already have an ID")).body(null);
        }
        Participant result = participantService.save(participant);
        return ResponseEntity.created(new URI("/api/participants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /participants : Updates an existing participant.
     *
     * @param participant the participant to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated participant,
     * or with status 400 (Bad Request) if the participant is not valid,
     * or with status 500 (Internal Server Error) if the participant couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/participants")
    @Timed
    public ResponseEntity<Participant> updateParticipant(@Valid @RequestBody Participant participant) throws URISyntaxException {
        log.debug("REST request to update Participant : {}", participant);
        if (participant.getId() == null) {
            return createParticipant(participant);
        }
        Participant result = participantService.save(participant);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, participant.getId().toString()))
            .body(result);
    }

    /**
     * GET  /participants : get all the participants.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of participants in body
     */
    @GetMapping("/participants")
    @Timed
    public ResponseEntity<List<Participant>> getAllParticipants(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Participants");
        Page<Participant> page = participantService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/participants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /participants/:id : get the "id" participant.
     *
     * @param id the id of the participant to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the participant, or with status 404 (Not Found)
     */
    @GetMapping("/participants/{id}")
    @Timed
    public ResponseEntity<Participant> getParticipant(@PathVariable Long id) {
        log.debug("REST request to get Participant : {}", id);
        Participant participant = participantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(participant));
    }

    /**
     * DELETE  /participants/:id : delete the "id" participant.
     *
     * @param id the id of the participant to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/participants/{id}")
    @Timed
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        log.debug("REST request to delete Participant : {}", id);
        participantService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/participants?query=:query : search for the participant corresponding
     * to the query.
     *
     * @param query the query of the participant search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/participants")
    @Timed
    public ResponseEntity<List<Participant>> searchParticipants(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Participants for query {}", query);
        Page<Participant> page = participantService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/participants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
