package fr.polytech.dronepark.components;

import javax.ejb.Local;

import fr.polytech.dronepark.utils.DroneAPI;

/**
 * ControlledDrone
 */
@Local
public interface ControlledDrone extends DroneLauncher {

    void useDroneParkReference(DroneAPI dronepark);

}
