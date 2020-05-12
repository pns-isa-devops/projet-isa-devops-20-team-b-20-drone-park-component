package fr.polytech.dronepark.components;

import fr.polytech.dronepark.exception.DroneCannotChangeStateException;
import fr.polytech.dronepark.exception.DroneNotFoundException;
import fr.polytech.dronepark.exception.InvalidDroneIDException;
import fr.polytech.entities.Drone;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DroneReviewer {

    /**
     * Adds a drone to the drone park.
     */
    void addDrone(String droneId) throws InvalidDroneIDException;

    /**
     * Returns the list of added drones.
     * 
     * @return a list of drones.
     */
    List<Drone> getDrones();

    /**
     * Set the drone with the id "drone_id" in charge state
     * 
     * @param droneId
     * @return
     */
    void setDroneInCharge(String droneId) throws DroneNotFoundException, DroneCannotChangeStateException;

    /**
     * Set the drone with the id "drone_id" in repair state
     * 
     * @param droneId
     * @return
     */
    void setDroneInRevision(String droneId) throws DroneNotFoundException, DroneCannotChangeStateException;

    /**
     * Set the drone with the id "drone_id" in available state
     * 
     * @param droneId
     * @return
     */
    void setDroneAvailable(String droneId) throws DroneNotFoundException;

}
