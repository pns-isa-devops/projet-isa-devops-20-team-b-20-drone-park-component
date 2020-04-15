package fr.polytech.dronepark.components;

import javax.ejb.Local;

@Local
public interface DroneReviewer {


    /**
     * Set the drone with the id "drone_id" in charge state
     * @param droneId
     * @return
     */
    boolean setDroneInCharge(String droneId);

    /**
     * Set the drone with the id "drone_id" in repair state
     * @param droneId
     * @return
     */
    boolean putDroneInRevision(String droneId);

    /**
     * Set the drone with the id "drone_id" in available state
     * @param droneId
     * @return
     */
    boolean setDroneAvailable(String droneId);
}
