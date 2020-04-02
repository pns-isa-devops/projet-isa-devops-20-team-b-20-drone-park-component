package fr.polytech.dronepark.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.entities.DeliveryStatus;
import fr.polytech.entities.Drone;

/**
 * DroneReturnChecker
 */
@Singleton
@Lock(LockType.WRITE)
public class DroneScheduler {
    private static final Logger log = Logger.getLogger(DroneScheduler.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    private DroneAPI droneAPI;
    private Set<Drone> drones = new HashSet<>();

    public DroneScheduler() {
        droneAPI = new DroneAPI();
    }

    public DroneScheduler(DroneAPI droneAPI) {
        this.droneAPI = droneAPI;
    }

    public void add(Drone d) {
        Drone drone = entityManager.merge(d);
        log.log(Level.INFO, "Drone [" + d.getDroneId() + "] in Delivery");
        drones.add(drone);
    }

    @Schedule(hour = "*", minute = "*", second = "1")
    public void processReturn() throws ExternalDroneApiException {
        for (Iterator<Drone> it = drones.iterator(); it.hasNext();) {
            DeliveryStatus status = droneAPI.getDeliveryStatus(it.next());
            Drone drone = entityManager.merge(it.next());
            if (status == DeliveryStatus.FAILED) {
                log.log(Level.INFO, "Drone [" + drone.getDroneId() + "] has failed the delivery");
                it.remove();
                entityManager.remove(drone);
            } else if (status == DeliveryStatus.DELIVERED) {
                log.log(Level.INFO, "Drone [" + drone.getDroneId() + "] has delivered successfully");
                it.remove();
                entityManager.remove(drone);
            }
        }
    }
}
