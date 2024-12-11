package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
public void calculateFare(Ticket ticket) {
    calculateFare(ticket, false);
}
    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long intTimeMillis  = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long durationInMillis  = outTimeMillis - intTimeMillis;
        //Conversion to hour
        double durationInHours = (double) durationInMillis / (60 * 60 * 1000);

        // Duration less than 30mn
        if (durationInHours < 0.5) {
            ticket.setPrice(0);
            return;
        }
        
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
             ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
             ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
        // reduction of 5%
        if (discount) {
            double Fare = 0;
            Fare += ticket.getPrice();
            ticket.setPrice(Fare * 0.95);
        }
    }
}
