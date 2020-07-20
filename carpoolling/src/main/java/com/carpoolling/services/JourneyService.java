package com.carpoolling.services;

import com.carpoolling.controllers.CarPoolingCrtl;
import com.carpoolling.entities.Car;
import com.carpoolling.entities.Journey;
import com.carpoolling.exception.DataNotFound;
import com.carpoolling.repositories.CarRepo;
import com.carpoolling.repositories.JourneyRepo;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author card0s0
 */
@Service
public class JourneyService {
    
    private Logger logger = LoggerFactory.getLogger(CarPoolingCrtl.class);
    
    @Autowired
    private JourneyRepo journeyRepo;
    @Autowired
    private CarRepo carRepo;
    
    private LinkedHashMap<Long, Car> travelingList;

    public JourneyService() {
        this.travelingList = new LinkedHashMap<>();
    }

    public JourneyService(JourneyRepo journeyRepo, CarRepo carRepo, LinkedHashMap<Journey, Car> travelingList) {
        this.journeyRepo = journeyRepo;
        this.carRepo = carRepo;
        this.travelingList = new LinkedHashMap<>();
    }
    
    public Journey create(Journey newJourney) {
        
        Journey storeJourney = journeyRepo.save(newJourney);
        logger.info("One journey added");
        
        this.assignJourneyToCar();
        
        return storeJourney;
    }
    
    public Journey findById(Long id) {
        
        return journeyRepo.queryFind(id);
//        return journeyRepo.findById(id).orElseThrow(
//                () -> new DataNotFound("Journey not found.")
//        );
    }
    
    /**
     *  Set the journey as completed and set the car as available.
     * It will also call the asynchronous function for new journeys.
     * 
     * @param journey, that will be completed.
     */
    public void unregister(Journey journey) {
        
        Car car = this.travelingList.remove(journey.getId());
        
        journey.setToCompleted();
        journeyRepo.save(journey);
        logger.info("Journey ID: "+journey.getId() +" Completed.");
        
        car.setAvailable(true);
        carRepo.save(car);
        logger.info("Car ID: "+car.getId() +" is now available.");
        
        logger.info("Seaching for new group to assign...");
        this.assignJourneyToCar();
    }

    public boolean isWaiting(Journey journey) {
        return journey.getStatus().equals(Journey.WAITING);
    }
    
    public boolean isCompleted(Journey journey) {
        return journey.getStatus().equals(Journey.COMPLETED);
    }
    
    public boolean isTraveling(Journey journey) {
        return journey.getStatus().equals(Journey.TRAVELING);
    }
    
    public Car getCarFromJourney(Journey journey) {
        return this.travelingList.get( journey.getId() );
    }
    
    /**
     *  Asynchronous function check the availability and pending journey 
     * and associate them if possible.
     * 
     */
    @Async
    public void assignJourneyToCar() {
        logger.info("Async function lauched");
        
        // Do we have available cars with seats engough for journey?
        List<Car> cars = carRepo.findByAvailability(true);
        List<Journey> journeys = journeyRepo.findByStatus(Journey.WAITING);
        
        cars.forEach( (Car c) -> {
            for (Journey j: journeys){
                if (c.isAvailable() && j.getStatus().equals(Journey.WAITING) && c.getSeats() >= j.getPeople()) {
                    j.setToTraveling();
                    c.setAvailable(false);
                    j = journeyRepo.save(j);
                    c = carRepo.save(c);
                    this.travelingList.put(j.getId(), c);
                    logger.info("Journey ID: "+ j.getId() +" assign to Car ID: "+ c.getId());
                    break;
                }
            }
        });
    }
    
}
