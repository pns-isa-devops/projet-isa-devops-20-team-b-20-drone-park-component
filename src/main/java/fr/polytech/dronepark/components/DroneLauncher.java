package fr.polytech.dronepark.components;

import java.util.GregorianCalendar;

import javax.ejb.Local;

import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.entities.Delivery;
import fr.polytech.entities.Drone;

@Local
public interface DroneLauncher {

    /**
     * Initializes drone launching by sending the launch signal to the drone at the
     * right time.
     *
     * @param drone
     * @return
     * @throws ExternalDroneApiException
     */
    boolean initializeDroneLaunching(Drone drone, GregorianCalendar launchHour, Delivery delivery) throws ExternalDroneApiException;

}
