package fr.polytech.dronepark.business;

import arquillian.AbstractDroneParkTest;
import fr.polytech.dronepark.components.DroneReviewer;
import fr.polytech.dronepark.exception.DroneCannotChangeStateException;
import fr.polytech.dronepark.exception.DroneNotFoundException;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class DroneReviewerTest extends AbstractDroneParkTest {
    @EJB
    DroneReviewer droneReviewer;
    Drone drone;
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private UserTransaction utx;

    @Before
    public void setup() {
        this.drone = new Drone("001");
        entityManager.persist(drone);
    }

    @Test
    public void chargeDrone() throws DroneNotFoundException, DroneCannotChangeStateException {
        drone = entityManager.merge(drone);
        droneReviewer.setDroneInCharge(drone.getDroneId());
        assertEquals(DroneStatus.ON_CHARGE, drone.getDroneStatus());
    }

    @Test
    public void reviseDrone() throws DroneNotFoundException, DroneCannotChangeStateException {
        drone = entityManager.merge(drone);
        drone.setFlightTime(10);
        drone = entityManager.merge(drone);
        droneReviewer.setDroneInRevision(drone.getDroneId());
        assertEquals(DroneStatus.ON_REPAIR, drone.getDroneStatus());
        assertEquals(0, drone.getFlightTime());

    }

    @Test
    public void changeStateOfDroneWhileNotAvailableTest()
            throws DroneNotFoundException, DroneCannotChangeStateException {
        drone = entityManager.merge(drone);
        droneReviewer.setDroneInCharge(drone.getDroneId());
        assertThrows(DroneCannotChangeStateException.class, () -> droneReviewer.setDroneInRevision(drone.getDroneId()));
        assertThrows(DroneCannotChangeStateException.class, () -> droneReviewer.setDroneInCharge(drone.getDroneId()));

        droneReviewer.setDroneAvailable(drone.getDroneId());

        droneReviewer.setDroneInRevision(drone.getDroneId());
        assertThrows(DroneCannotChangeStateException.class, () -> droneReviewer.setDroneInRevision(drone.getDroneId()));
        assertThrows(DroneCannotChangeStateException.class, () -> droneReviewer.setDroneInCharge(drone.getDroneId()));

        droneReviewer.getDrones().get(0).setDroneStatus(DroneStatus.ON_DELIVERY);

        assertThrows(DroneCannotChangeStateException.class, () -> droneReviewer.setDroneInRevision(drone.getDroneId()));
        assertThrows(DroneCannotChangeStateException.class, () -> droneReviewer.setDroneInCharge(drone.getDroneId()));
    }

    @Test
    public void setDroneAvailable() throws DroneNotFoundException, DroneCannotChangeStateException {
        drone = entityManager.merge(drone);
        droneReviewer.setDroneInRevision(drone.getDroneId());
        droneReviewer.setDroneAvailable(drone.getDroneId());
        assertEquals(DroneStatus.AVAILABLE, drone.getDroneStatus());
    }

    @After
    public void clean() throws Exception {
        utx.begin();
        this.drone = entityManager.merge(drone);
        entityManager.remove(drone);
        drone = null;
        utx.commit();

    }

}
