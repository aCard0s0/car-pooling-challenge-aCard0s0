package com.carpoolling.controllers;

import com.carpoolling.entities.Car;
import com.carpoolling.entities.Journey;
import com.carpoolling.exception.BadRequest;
import com.carpoolling.services.CarService;
import com.carpoolling.services.JourneyService;
import java.util.Collection;
import java.util.Iterator;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author acard0s0
 * 
 * This class is responsible to define the accepted HTTP request.
 * 
 */
@RestController
public class CarPoolingCrtl {
    
    private Logger logger = LoggerFactory.getLogger(CarPoolingCrtl.class);
    
    @Autowired
    private CarService carSrv;          // Bean that encapsulate service logic
    
    @Autowired
    private JourneyService journeySrv;  // Bean that encapsulate service logic
    
    /**
     *  Test service.
     * 
     * @return HTTP 200 OK, when server is up and running.
     */
    @GetMapping("/status")
    public ResponseEntity<Void> status() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * Delete all current cars in the system and adds a new list. If journey are
     * waiting, they will be assign to the news cars.
     * 
     * @param newCarList, values to be inserted.
     * @return  HTTP 400 Bad Request, when Exception is catch, 
     *          HTTP 200 OK otherwise.
     */
    @PutMapping( 
            value = "/cars", 
            consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity< String > loadCars(@RequestBody Collection<Car> newCarList) {
        
        try {
            carSrv.deleteAll();    // doesn't delete if execption is catch
            for (Iterator<Car> iterator = newCarList.iterator(); iterator.hasNext();) {
                carSrv.create(iterator.next());
            }
        }catch(Exception e) {
            logger.error("Bad Request: by /cars");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("OK: Added "+newCarList.size()+" by /cars");
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     *  Adds a new journey in the service. If cars are available they will be assign
     * to journeys.
     * 
     * @param newJourney, the group size must be [1, 6] peoples. 
     * @return  HTTP 400 Bad Request, if parameters are not valid,
     *          HTTP 200 OK, otherwise.
     */
    @PostMapping(
            value = "/journey", 
            consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity< String > addJourney(@RequestBody @Valid Journey newJourney) {
        
        // Note: 
        //  @Valid ensures size group is in range [1, 6]
        //  otherwise, ControllerAdvisor throws BadRequest exception.
        journeySrv.create(newJourney);
        return new ResponseEntity<>(HttpStatus.OK);   
    }
    
    /**
     * Only accepts an content type request "application/x-www-form-urlencoded", with an form
     * with key value pair "ID=X".
     * 
     * @param form, must have ID as key and integer as value.
     * @return  HTTP 400 Bad Request, if form does not contain ID. 
     *          HTTP 404 Not Found, if journey ID is not found.
     *          HTTP 200 OK, set journey as completed and associated car as available.
     */
    @PostMapping(
            value = "/dropoff", 
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE )
    public ResponseEntity< String > dropoff(@RequestParam MultiValueMap<String, String> form) {
        
        if (!form.containsKey("ID")) {
            throw new BadRequest("Form does not contain ID key.");
        }

        // throws DataNotFound exception.
        Journey journey = journeySrv.findById( 
                Long.parseLong(form.getFirst("ID")) 
        );
        
        if (journeySrv.isWaiting(journey) || journeySrv.isCompleted(journey)) {
            throw new BadRequest("Journey group does not have assigned car yet.");
        }

        journeySrv.unregister(journey);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    /**
     * Only accepts an content type request "application/x-www-form-urlencoded", with an form
     * with key value pair "ID=X".
     * 
     * @param form, must have ID as key and integer as value.
     * @return  HTTP 400 Bad Request, if form does not contain ID.
     *          HTTP 404 Not Found, if journey ID is not found.
     *          HTTP 204 OK, when car is waiting or journey completed.
     *          HTTP 200 OK, with payload car associated with journey ID.
     */
    @PostMapping(
            value = "/locate", 
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity< Car > locate(@RequestParam MultiValueMap<String, String> form) {
        
        if (!form.containsKey("ID")) {
            throw new BadRequest("Form does not contain ID key.");  // send bad request status
        }
        
        // throws DataNotFound exception.
        Journey journey = journeySrv.findById( 
                Long.parseLong(form.getFirst("ID").trim()) 
        );

        if (journeySrv.isCompleted(journey)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // is traveling
        return new ResponseEntity<>(
                journeySrv.getCarFromJourney(journey), HttpStatus.OK);
    }
}