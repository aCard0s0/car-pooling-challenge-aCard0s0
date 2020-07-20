package com.carpoolling.services;

import com.carpoolling.entities.Car;
import com.carpoolling.exception.DataNotFound;
import com.carpoolling.repositories.CarRepo;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author acard0s0
 */
@Service
public class CarService {
    
    @Autowired
    private CarRepo carRepo;
    @Autowired
    private JourneyService journeySrv;
    
    /**
     *  Creates a new car object in the database.
     * 
     * @param newCar
     * @return 
     */
    public Car create(Car newCar) {
        
        // save method update if exist and create if not.
        Car car = carRepo.save(newCar);
        
        // new car can be added at anytime, therefore it check for new assignments.
        journeySrv.assignJourneyToCar();
        
        return car;
    }
    
    public Collection<Car> findAll() {
        return (Collection<Car>) carRepo.findAll();
    }

    public Car findById(Long id) {
        
        return carRepo.findById(id).orElseThrow(
                () -> new DataNotFound("Car not found")
        );
    }

    public void deleteAll() {
        carRepo.deleteAll();
    }

}
