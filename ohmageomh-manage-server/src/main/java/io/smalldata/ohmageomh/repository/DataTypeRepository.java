package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.DataType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the DataType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataTypeRepository extends JpaRepository<DataType, Long> {

}
