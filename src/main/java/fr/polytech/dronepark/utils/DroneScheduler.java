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

import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.entities.DeliveryStatus;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;

/**
 * DroneReturnChecker
 */
@Singleton
@Lock(LockType.WRITE)
public class DroneScheduler {
    private static final Logger log = Logger.getLogger(DroneScheduler.class.getName());

    private Set<Drone> drones = new HashSet<>();
    private DroneAPI droneAPI;

    public DroneScheduler() {
        droneAPI = new DroneAPI();
    }

    public DroneScheduler(DroneAPI droneAPI) {
        this.droneAPI = droneAPI;
    }

    public void add(Drone d) {
        this.drones.add(d);
        log.log(Level.INFO, "Drone [" + d.getDroneId() + "] in Delivery");
    }

    @Schedule(hour = "*", minute = "*", second = "1")
    public void processReturn() throws ExternalDroneApiException {
        for (Iterator<Drone> it = drones.iterator(); it.hasNext();) {
            DeliveryStatus status = droneAPI.getDeliveryStatus(it.next());
            if (status == DeliveryStatus.FAILED) {
                log.log(Level.INFO, "Drone [" + it.next().getDroneId() + "] has failed the delivery");
                it.remove();
            } else if (status == DeliveryStatus.DELIVERED) {
                log.log(Level.INFO, "Drone [" + it.next().getDroneId() + "] has delivered successfully");
                it.remove();
            }
        }
    }
}
