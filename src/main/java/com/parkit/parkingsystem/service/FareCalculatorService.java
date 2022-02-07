package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.dao.TicketDAO;

public class FareCalculatorService {
     
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inMinutes = ticket.getInTime().getTime()/1000/60;
        long outMinutes = ticket.getOutTime().getTime()/1000/60;

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long durationm = outMinutes - inMinutes;
        double rate=1.0;
        
        // gratuit pour les 30 premières minutes
        if (durationm >=30)
            durationm=durationm-30;
        else
            durationm=0;
        
        int duration=(int) durationm/60;
        
//        if (durationm<15)
//        {
//            rate=0.0;
//        }   
//        else 
        if (durationm<60)
        {
            rate=0.75;
            duration=1;
        }
        
        String VehicleRegNumber=ticket.getVehicleRegNumber();
        TicketDAO ticketDAO = new TicketDAO();
        int  TicketsCount=ticketDAO.GetTicketsCount(VehicleRegNumber);   
        double discount=0;
        
        if (TicketsCount>1) 
          discount=5;
    
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice((rate * duration * Fare.CAR_RATE_PER_HOUR) - ((rate * duration * Fare.CAR_RATE_PER_HOUR)*(discount/100)));
                break;
            }
            case BIKE: {
                ticket.setPrice((rate *  duration * Fare.BIKE_RATE_PER_HOUR) - ((rate * duration * Fare.BIKE_RATE_PER_HOUR)*(discount/100)));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
