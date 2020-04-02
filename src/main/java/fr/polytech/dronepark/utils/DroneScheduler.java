package fr.polytech.dronepark.utils;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.polytech.entities.Drone;

/**
 * DroneReturnChecker
 */
@Singleton
@Lock(LockType.WRITE)
public class DroneScheduler {

    @PersistenceContext
    private EntityManager entityManager;

    DroneSchedulerController controller;

    public DroneScheduler() {
        this(new DroneAPI());
    }

    public DroneScheduler(DroneAPI droneAPI) {
        controller = new DroneSchedulerController(droneAPI);
    }

    public void add(Drone d) throws Exception {
        controller.add(d, entityManager);
    }

    @Schedule(hour = "*", minute = "*", second = "1")
    public void processReturn() throws Exception {
        controller.runProcess(entityManager);
    }
}
