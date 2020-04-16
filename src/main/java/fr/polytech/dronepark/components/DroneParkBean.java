package fr.polytech.dronepark.components;

import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.polytech.entities.Delivery;
import org.apache.cxf.common.i18n.UncheckedException;

import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.dronepark.utils.DroneScheduler;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;

@Stateless
public class DroneParkBean implements DroneLauncher, ControlledDrone, DroneReviewer {

    private static final Logger log = Logger.getLogger(Logger.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    private DroneAPI droneAPI;

    @EJB
    DroneScheduler scheduler;

    @Override
    public void useDroneParkReference(DroneAPI dronepark) {
        droneAPI = dronepark;
    }

    /**
     * Initializes drone launching by sending the launch signal to the drone at the
     *      * right time.
     *
     * @param d a drone
     * @param launchHour
     * @return
     * @throws Exception
     */
    @Override
    public boolean initializeDroneLaunching(Drone d, GregorianCalendar launchHour, Delivery deliv) throws Exception {
        Drone drone = entityManager.merge(d);
        Delivery delivery = entityManager.merge(deliv);
        boolean status;
        // Call the dotnet API
        status = this.droneAPI.launchDrone(drone, launchHour);
        scheduler.add(drone);
        drone.setDroneStatus(DroneStatus.ON_DELIVERY);
        drone.setCurrentDelivery(delivery);
        entityManager.persist(drone);
        return status;
    }

    @PostConstruct
    /**
     * Init the drone API on localhost
     */
    private void initializeRestPartnership() {
        try {
            Properties prop = new Properties();
            prop.load(this.getClass().getResourceAsStream("/dronepark.properties"));
            droneAPI = new DroneAPI(prop.getProperty("droneparkHostName"), prop.getProperty("droneparkPortNumber"));
        } catch (Exception e) {
            log.log(Level.INFO, "Cannot read dronepark.properties file", e);
            throw new UncheckedException(e);
        }
    }

    @Override
    public void addDrone() {
        Drone drone = new Drone("000");
        entityManager.persist(drone);
    }


    @Override
    public boolean setDroneInCharge(String droneId) {
        // If we use the Drone.droneId field instead of Drone.id use the bellow
       /* Drone drone = (Drone) entityManager.createQuery("SELECT * FROM Drone where Drone.droneId = :value1")
                .setParameter("value1", droneId).getSingleResult();*/
        Drone drone  = entityManager.find(Drone.class,droneId);
        if(drone == null) return false;
        drone.setDroneStatus(DroneStatus.ON_CHARGE);
        entityManager.persist(drone);
        return true;
    }

    @Override
    public boolean putDroneInRevision(String droneId) {
        // If we use the Drone.droneId field instead of Drone.id use the bellow
       /* Drone drone = (Drone) entityManager.createQuery("SELECT * FROM Drone where Drone.droneId = :value1")
                .setParameter("value1", droneId).getSingleResult();*/
        Drone drone  = entityManager.find(Drone.class,droneId);
        if(drone == null) return false;
        drone.setDroneStatus(DroneStatus.ON_REPAIR);
        entityManager.persist(drone);
        return true;
    }

    @Override
    public boolean setDroneAvailable(String droneId) {
        // If we use the Drone.droneId field instead of Drone.id use the bellow
       /* Drone drone = (Drone) entityManager.createQuery("SELECT * FROM Drone where Drone.droneId = :value1")
                .setParameter("value1", droneId).getSingleResult();*/
        Drone drone  = entityManager.find(Drone.class,droneId);
        if(drone == null) return false;
        drone.setDroneStatus(DroneStatus.AVAILABLE);
        entityManager.persist(drone);
        return true;
    }
}
