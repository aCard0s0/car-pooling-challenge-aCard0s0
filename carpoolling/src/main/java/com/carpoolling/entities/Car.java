package com.carpoolling.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 *
 * @author acard0s0
 * 
 *  This class represent a car within the system.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Car.findByAvailability", query = "SELECT c FROM Car c WHERE c.available = :available")
})
public class Car implements Serializable {
    
    @Id
    private Long id;
    @NotNull
    private Integer seats;
    
    // This field is ignore when sumitted as a body request.
    @JsonIgnore
    private boolean available;

    public Car() {
        this.available = true;
    }
    
    public Car(Long id, Integer seats) {
        this.id = id;
        this.seats = seats;
        this.available = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
}
