package io.smalldata.ohmageomh.repository.search;

import io.smalldata.ohmageomh.domain.Integration;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Integration entity.
 */
public interface IntegrationSearchRepository extends ElasticsearchRepository<Integration, Long> {
}
