package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.smalldata.ohmageomh.domain.Integration;
import io.smalldata.ohmageomh.service.IntegrationService;
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
 * REST controller for managing Integration.
 */
@RestController
@RequestMapping("/api")
public class IntegrationResource {

    private final Logger log = LoggerFactory.getLogger(IntegrationResource.class);

    private static final String ENTITY_NAME = "integration";

    private final IntegrationService integrationService;

    public IntegrationResource(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    /**
     * POST  /integrations : Create a new integration.
     *
     * @param integration the integration to create
     * @return the ResponseEntity with status 201 (Created) and with body the new integration, or with status 400 (Bad Request) if the integration has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/integrations")
    @Timed
    public ResponseEntity<Integration> createIntegration(@Valid @RequestBody Integration integration) throws URISyntaxException {
        log.debug("REST request to save Integration : {}", integration);
        if (integration.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new integration cannot already have an ID")).body(null);
        }
        Integration result = integrationService.save(integration);
        return ResponseEntity.created(new URI("/api/integrations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /integrations : Updates an existing integration.
     *
     * @param integration the integration to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated integration,
     * or with status 400 (Bad Request) if the integration is not valid,
     * or with status 500 (Internal Server Error) if the integration couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/integrations")
    @Timed
    public ResponseEntity<Integration> updateIntegration(@Valid @RequestBody Integration integration) throws URISyntaxException {
        log.debug("REST request to update Integration : {}", integration);
        if (integration.getId() == null) {
            return createIntegration(integration);
        }
        Integration result = integrationService.save(integration);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, integration.getId().toString()))
            .body(result);
    }

    /**
     * GET  /integrations : get all the integrations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of integrations in body
     */
    @GetMapping("/integrations")
    @Timed
    public ResponseEntity<List<Integration>> getAllIntegrations(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Integrations");
        Page<Integration> page = integrationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/integrations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /integrations/:id : get the "id" integration.
     *
     * @param id the id of the integration to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the integration, or with status 404 (Not Found)
     */
    @GetMapping("/integrations/{id}")
    @Timed
    public ResponseEntity<Integration> getIntegration(@PathVariable Long id) {
        log.debug("REST request to get Integration : {}", id);
        Integration integration = integrationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(integration));
    }

    /**
     * DELETE  /integrations/:id : delete the "id" integration.
     *
     * @param id the id of the integration to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/integrations/{id}")
    @Timed
    public ResponseEntity<Void> deleteIntegration(@PathVariable Long id) {
        log.debug("REST request to delete Integration : {}", id);
        integrationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/integrations?query=:query : search for the integration corresponding
     * to the query.
     *
     * @param query the query of the integration search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/integrations")
    @Timed
    public ResponseEntity<List<Integration>> searchIntegrations(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Integrations for query {}", query);
        Page<Integration> page = integrationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/integrations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
