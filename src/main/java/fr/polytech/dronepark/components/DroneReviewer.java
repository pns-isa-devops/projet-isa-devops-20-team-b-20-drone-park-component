package fr.polytech.dronepark.components;

import fr.polytech.dronepark.exception.DroneNotFoundException;

import javax.ejb.Local;

@Local
public interface DroneReviewer {

    /**
     * Adds a drone to the drone park.
     */
    void addDrone(String droneId);

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
