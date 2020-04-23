package fr.polytech.dronepark.components;

import fr.polytech.dronepark.exception.DroneNotFoundException;
import fr.polytech.dronepark.exception.InvalidDroneIDException;

import javax.ejb.Local;

@Local
public interface DroneReviewer {

    /**
     * Adds a drone to the drone park.
     */
    void addDrone(String droneId) throws InvalidDroneIDException;

    /**
     * Set the drone with the id "drone_id" in charge state
     * @param droneId
     * @return
     */
    void setDroneInCharge(String droneId)throws DroneNotFoundException;

    /**
     * Set the drone with the id "drone_id" in repair state
     * @param droneId
     * @return
     */
    void putDroneInRevision(String droneId)throws DroneNotFoundException;

    /**
     * Set the drone with the id "drone_id" in available state
     * @param droneId
     * @return
     */
    void setDroneAvailable(String droneId) throws DroneNotFoundException;

}
