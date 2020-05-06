package fr.polytech.dronepark.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

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
            switch (status){
                case FAILED:
                    log.log(Level.INFO, "Drone [" + drone.getDroneId() + "] is back with the delivery (not delivered)");
                    break;
                case NOT_DELIVERED:
                    log.log(Level.INFO, "Drone [" + drone.getDroneId() + "] hasn't been delivered yet");
                    break;
                case DELIVERED:
                    log.log(Level.INFO, "Drone [" + drone.getDroneId() + "] has delivered successfully");
                    break;
                default:
                    log.log(Level.INFO, "Drone [" + drone.getDroneId() + "] hasn't returned any information");
            }
            it.remove();
            currentDelivery.setStatus(status);
            drone.setCurrentDelivery(null);
            drone.setDroneStatus(DroneStatus.AVAILABLE);
            entityManager.persist(drone);
            entityManager.persist(currentDelivery);

        }
    }
}
