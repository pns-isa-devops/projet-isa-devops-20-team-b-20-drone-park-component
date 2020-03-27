package fr.polytech.dronepark.components;

import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.cxf.common.i18n.UncheckedException;

import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.dronepark.utils.DroneScheduler;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;

@Stateless
public class DroneParkBean implements DroneLauncher, ControlledDrone {

    private static final Logger log = Logger.getLogger(Logger.class.getName());

    private DroneAPI droneAPI;

    @EJB
    DroneScheduler scheduler;

    @Override
    public void useDroneParkReference(DroneAPI dronepark) {
        droneAPI = dronepark;
    }

    /**
     * Initializes drone launching by sending the launch signal to the drone at the
     * right time.
     *
     * @param drone
     * @return
     * @throws ExternalDroneApiException
     */
    @Override
    public boolean initializeDroneLaunching(final Drone drone, final GregorianCalendar launchHour)
            throws ExternalDroneApiException {
        // Call the dotnet API
        this.droneAPI.launchDrone(drone, launchHour);
        scheduler.add(drone);
        drone.setDroneStatus(DroneStatus.ON_DELIVERY);
        return false;
    }

    @PostConstruct
    /**
     * Init the drone API on localhost
     */
    private void initializeRestPartnership() {
        try {
            Properties prop = new Properties();
            prop.load(this.getClass().getResourceAsStream("/dronepark.properties"));
            droneAPI = new DroneAPI(prop.getProperty("carrierHostName"), prop.getProperty("carrierPortNumber"));
        } catch (Exception e) {
            log.log(Level.INFO, "Cannot read dronepark.properties file", e);
            throw new UncheckedException(e);
        }
    }

}
