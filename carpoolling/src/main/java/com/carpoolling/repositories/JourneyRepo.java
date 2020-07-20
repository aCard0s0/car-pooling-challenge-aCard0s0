package com.carpoolling.repositories;

import com.carpoolling.entities.Journey;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author acard0s0
 * 
 *  This interface provide us with access to general CRUD operation already 
 * made available by Spring Boot Framework.
 */
@Repository
public interface JourneyRepo extends CrudRepository<Journey, Long>{
    
    List<Journey> findByStatus(String status);
    
    Journey queryFind(Long id);
}

