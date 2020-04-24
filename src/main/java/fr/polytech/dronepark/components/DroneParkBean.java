package fr.polytech.dronepark.components;

import fr.polytech.dronepark.exception.DroneNotFoundException;
import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.dronepark.exception.InvalidDroneIDException;
import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.dronepark.utils.DroneScheduler;
import fr.polytech.entities.Delivery;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;
import org.apache.cxf.common.i18n.UncheckedException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class DroneParkBean implements DroneLauncher, ControlledDrone, DroneReviewer {

    private static final Logger log = Logger.getLogger(Logger.class.getName());
    @EJB
    DroneScheduler scheduler;
    @PersistenceContext
    private EntityManager entityManager;
    private DroneAPI droneAPI;

    @Override
    public void useDroneParkReference(DroneAPI dronepark) {
        droneAPI = dronepark;
    }

    /**
     * Initializes drone launching by sending the launch signal to the drone at the
     * * right time.
     *
     * @param d a drone
     * @param launchHour
     * @return
     * @throws Exception
     */
    @Override
    public boolean initializeDroneLaunching(Drone d, GregorianCalendar launchHour, Delivery deliv) throws ExternalDroneApiException {
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
    public void addDrone(String droneId) throws InvalidDroneIDException {
        Long nb = (Long) entityManager.createQuery("select count(d) from Drone d where d.droneId='" + droneId + "'").getSingleResult();

        if (nb == 0) {
            Drone drone = new Drone(droneId);
            entityManager.persist(drone);
        } else {
            throw new InvalidDroneIDException(droneId);
        }
    }

    @Override
    public void setDroneInCharge(String droneId) throws DroneNotFoundException {
        setDroneStatus(droneId, DroneStatus.ON_CHARGE);
    }

    private Optional<Drone> findById(String id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Drone> criteria = builder.createQuery(Drone.class);
        Root<Drone> root = criteria.from(Drone.class);
        criteria.select(root).where(builder.equal(root.get("droneId"), id));

        TypedQuery<Drone> query = entityManager.createQuery(criteria);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            log.log(Level.FINEST, "No result for [" + id + "]", e);
            return Optional.empty();
        }
    }


    @Override
    public void putDroneInRevision(String droneId) throws DroneNotFoundException {
        setDroneStatus(droneId, DroneStatus.ON_REPAIR);
    }

    @Override
    public void setDroneAvailable(String droneId) throws DroneNotFoundException {
        setDroneStatus(droneId, DroneStatus.AVAILABLE);
    }

    private void setDroneStatus(String droneId, DroneStatus droneStatus) throws DroneNotFoundException {
        if (findById(droneId).isPresent()) {
            Drone drone = findById(droneId).get();
            drone.setDroneStatus(droneStatus);
            entityManager.persist(drone);
        } else {
            throw new DroneNotFoundException(droneId);
        }
    }
}
