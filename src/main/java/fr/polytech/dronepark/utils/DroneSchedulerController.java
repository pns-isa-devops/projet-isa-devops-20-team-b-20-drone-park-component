package fr.polytech.dronepark.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.entities.Delivery;
import fr.polytech.entities.DeliveryStatus;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;

public class DroneSchedulerController {

    private static final Logger log = Logger.getLogger(DroneSchedulerController.class.getName());

    private DroneAPI droneAPI;
    private Set<Drone> drones = new HashSet<>();

    public DroneSchedulerController(DroneAPI droneAPI) {
        this.droneAPI = droneAPI;
    }

    public void add(Drone d, EntityManager entityManager) {
        Drone drone = entityManager.merge(d);
        log.log(Level.INFO, "Drone [" + d.getDroneId() + "] in Delivery");
        drones.add(drone);
    }

    public void runProcess(EntityManager entityManager) throws ExternalDroneApiException {
        for (Iterator<Drone> it = drones.iterator(); it.hasNext();) {
            Drone drone = entityManager.merge(it.next());
            DeliveryStatus status = droneAPI.getDeliveryStatus(drone);
            Delivery currentDelivery = entityManager.merge(drone.getCurrentDelivery());
            if (status == DeliveryStatus.FAILED || status == DeliveryStatus.DELIVERED) {
                it.remove();
                currentDelivery.setStatus(status);
                drone.setCurrentDelivery(null);
                drone.setDroneStatus(DroneStatus.AVAILABLE);
                log.log(Level.INFO,
                        "Drone [" + drone.getDroneId() + "] "
                                + (status == DeliveryStatus.FAILED ? "is back with the delivery (not delivered)"
                                        : "has delivered successfully"));
            }
        }
    }
}
