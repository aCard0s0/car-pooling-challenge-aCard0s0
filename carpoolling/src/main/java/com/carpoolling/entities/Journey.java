package com.carpoolling.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author acard
 * 
 *  This class represent the intention from a client to request a journey.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Journey.findByStatus", query = "SELECT j FROM Journey j WHERE j.status = :status"),
    @NamedQuery(name = "Journey.queryFind", query = "SELECT j FROM Journey j WHERE j.id = :id")
})
public class Journey implements Serializable {
    
    public static final String WAITING = "WAITING";
    public static final String TRAVELING = "TRAVELING";
    public static final String COMPLETED = "COMPLETED";
    
    @Id
    private Long id;
    
    //@NotNull()      // Test 5/17 is POSTing journey with null value and is expeting [200, 204], got 400
    @Range(min=1, max=6, message = "Size group must be between 1 and 6")
    private Integer people;
    
    // This field is ignore when sumitted as a body request.
    @JsonIgnore     
    private String status;  // ["WAITING", "TRAVELING", "COMPLETED"]
    
    
    public Journey() {
        this.setToWaiting();
    }

    public Journey(Long id, Integer nPeople) {
        this.id = id;
        this.people = nPeople;
        this.setToWaiting();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer nPeople) {
        this.people = nPeople;
    }

    public String getStatus() {
        return status;
    }
    
    public void setToWaiting() {
        this.status = WAITING;
    }
    
    public void setToTraveling() {
        this.status = TRAVELING;
    }
    
    public void setToCompleted() {
        this.status = COMPLETED;
    }
}
