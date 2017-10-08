package io.smalldata.ohmageomh.repository.search;

import io.smalldata.ohmageomh.domain.Study;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Study entity.
 */
public interface StudySearchRepository extends ElasticsearchRepository<Study, Long> {
}
