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

import org.apache.cxf.common.i18n.UncheckedException;

import fr.polytech.dronepark.exception.ExternalDroneApiException;
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
     * @param d
     * @param launchHour
     * @return
     * @throws Exception
     */
    @Override
    public boolean initializeDroneLaunching(Drone d, GregorianCalendar launchHour) throws Exception {
        Drone drone = entityManager.merge(d);
        boolean status;
        // Call the dotnet API
        status = this.droneAPI.launchDrone(drone, launchHour);
        scheduler.add(drone);
        drone.setDroneStatus(DroneStatus.ON_DELIVERY);
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
}
