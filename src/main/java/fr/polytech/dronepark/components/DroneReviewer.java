package fr.polytech.dronepark.components;

import javax.ejb.Local;

@Local
public interface DroneReviewer {


    boolean setDroneInCharge(String droneId);
}
