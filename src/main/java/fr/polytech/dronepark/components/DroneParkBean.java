package fr.polytech.dronepark.components;

import fr.polytech.dronepark.exception.DroneNotAvailableException;
import fr.polytech.dronepark.exception.DroneNotFoundException;
import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.dronepark.exception.InvalidDroneIDException;
import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.dronepark.utils.DroneScheduler;
import fr.polytech.entities.Delivery;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;
import org.apache.cxf.common.i18n.Message;
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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class DroneParkBean implements DroneLauncher, ControlledDrone, DroneReviewer {

    private static final Logger log = Logger.getLogger(DroneParkBean.class.getName());
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
     * @param d          a drone
     * @param launchHour
     * @return
     * @throws DroneNotAvailableException
     * @throws Exception
     */
    @Override
    public boolean initializeDroneLaunching(Drone d, GregorianCalendar launchHour, Delivery deliv)
            throws ExternalDroneApiException, DroneNotAvailableException {
        Drone drone = entityManager.merge(d);
        Delivery delivery = entityManager.merge(deliv);
        boolean status;
        // Call the dotnet API
        status = this.droneAPI.launchDrone(drone, launchHour, delivery);
        scheduler.add(drone);
        drone.setDroneStatus(DroneStatus.ON_DELIVERY);
        drone.setCurrentDelivery(delivery);
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

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Drone> root = criteria.from(Drone.class);
        criteria.select(builder.count(criteria.from(Drone.class)));
        criteria.where(builder.equal(root.get("droneId"), droneId));

        Long nb = entityManager.createQuery(criteria).getSingleResult();

        if (nb == 0) {
            Drone drone = new Drone(droneId);
            entityManager.persist(drone);
        } else {
            throw new InvalidDroneIDException(droneId);
        }
    }

    @Override
    public void setDroneInCharge(String droneId) throws DroneNotFoundException {
        Drone drone = this.findById(droneId);
        drone = entityManager.merge(drone);
        drone.setDroneStatus(DroneStatus.ON_CHARGE);
    }

    @Override
    public void putDroneInRevision(String droneId) throws DroneNotFoundException {
        Drone drone = this.findById(droneId);
        drone = entityManager.merge(drone);
        drone.setDroneStatus(DroneStatus.ON_REPAIR);
        drone.setFlightTime(0);
    }

    @Override
    public void setDroneAvailable(String droneId) throws DroneNotFoundException {
        Drone drone = this.findById(droneId);
        drone = entityManager.merge(drone);
        drone.setDroneStatus(DroneStatus.AVAILABLE);

    }

    private Drone findById(String id) throws DroneNotFoundException {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Drone> criteria = builder.createQuery(Drone.class);
        Root<Drone> root = criteria.from(Drone.class);
        criteria.select(root).where(builder.equal(root.get("droneId"), id));

        TypedQuery<Drone> query = entityManager.createQuery(criteria);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.log(Level.FINEST, "No result for [" + id + "]", e);
            throw new DroneNotFoundException(id);
        }
    }
}
