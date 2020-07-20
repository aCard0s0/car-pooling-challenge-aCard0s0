package com.carpoolling.repositories;

import com.carpoolling.entities.Car;
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
public interface CarRepo extends CrudRepository<Car, Long>{
    
    List<Car> findByAvailability(boolean available);
}
