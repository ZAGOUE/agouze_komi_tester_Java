package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    String vehicleRegNumber;

    @BeforeEach
    public void setUpPerTest() {
        vehicleRegNumber = "ABCDEF";

        try {



            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }


    }


    @Test
    public void processExitingVehicleTest() throws Exception {

        Ticket ticket = new Ticket();

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        when(ticketDAO.getNBTicket(vehicleRegNumber)).thenReturn(2);

        parkingService.processExitingVehicle();

        verify(ticketDAO, times(1)).getNBTicket(vehicleRegNumber);


    }

   @Test
    public void testProcessIncomingVehicle() {

       when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService.processIncomingVehicle();


        verify(inputReaderUtil, times(1)).readSelection();


   }
   @Test
    public void processExitingVehicleTestUnableUpdate() throws Exception {
        

       Ticket ticket = new Ticket();
       ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
       ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
       ticket.setVehicleRegNumber("ABCDEF");


       when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
       when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
       when(ticketDAO.updateTicket(ticket)).thenReturn(false);


           parkingService.processExitingVehicle();


       verify(ticketDAO, times(1)).updateTicket(ticket);
       verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));


   }


    @Test
    public void testGetNextParkingNumberIfAvailable() {



        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        assertNotNull(parkingSpot);
        assertEquals(1,parkingSpot.getId());
        assertEquals(ParkingType.CAR,parkingSpot.getParkingType());

   }
   @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {

       when(inputReaderUtil.readSelection()).thenReturn(1);
       when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);



      ParkingSpot  parkingSpot = parkingService.getNextParkingNumberIfAvailable();

       verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
       assertNull(parkingSpot);
   }
   @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {

        when(inputReaderUtil.readSelection()).thenReturn(3);


        ParkingSpot  parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        verify(inputReaderUtil, times(1)).readSelection();
        assertNull(parkingSpot);
   }
}

