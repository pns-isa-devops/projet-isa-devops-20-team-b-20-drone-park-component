package fr.polytech.dronepark.business;

import arquillian.AbstractDroneParkTest;
import fr.polytech.dronepark.components.ControlledDrone;
import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.entities.Delivery;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;
import fr.polytech.entities.Parcel;
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
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * DronePark
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class DroneParkTest extends AbstractDroneParkTest {

    Drone drone;
    Delivery delivery;
    @EJB
    private ControlledDrone controlledDrone;
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private UserTransaction utx;

    @Before
    public void setUpContext() throws Exception {
        initDate();
        initMock();
    }

    @After
    public void cleanUpContext() throws Exception {
        utx.begin();
        drone = entityManager.merge(drone);
        entityManager.remove(drone);
       /* delivery = entityManager.merge(delivery);
        entityManager.remove(delivery);*/
        utx.commit();
    }

    private void initDate() {
        drone = new Drone("123");
        entityManager.persist(drone);
        // Create a delivery
        delivery = new Delivery("DELIVERY22");
        delivery.setParcel(new Parcel("123456789A", "Rue test", "DHS", "AlexHey"));
        //  entityManager.persist(delivery);
    }

    private void initMock() throws ExternalDroneApiException {
        drone.setDroneStatus(DroneStatus.AVAILABLE);
        DroneAPI mocked = mock(DroneAPI.class);
        controlledDrone.useDroneParkReference(mocked);
        when(mocked.launchDrone(drone, new GregorianCalendar())).thenReturn(true);
    }


    @Test
    public void initializeDroneLaunchingTest() throws Exception {
        Drone droneTest = entityManager.merge(drone);
        droneTest.setDroneStatus(DroneStatus.AVAILABLE);

        assertEquals(DroneStatus.AVAILABLE, droneTest.getDroneStatus());
        controlledDrone.initializeDroneLaunching(droneTest, new GregorianCalendar(), delivery);
        assertEquals(DroneStatus.ON_DELIVERY, droneTest.getDroneStatus());
    }

    @Test
    public void initializeDroneLaunchingOtherDateTest() throws Exception {
        Drone droneTest = entityManager.merge(drone);
        droneTest.setDroneStatus(DroneStatus.AVAILABLE);

        assertEquals(DroneStatus.AVAILABLE, droneTest.getDroneStatus());
        controlledDrone.initializeDroneLaunching(droneTest, new GregorianCalendar(2020, 3, 19, 18, 37), delivery);
        assertEquals(DroneStatus.ON_DELIVERY, droneTest.getDroneStatus());
    }

    @Test
    public void addDrone() {
        Drone stored = entityManager.find(Drone.class, drone.getId());
        assertNotNull(stored);
        this.controlledDrone.addDrone("000");
        Query query = entityManager.createQuery("select d from Drone d where d.droneId='000'");
        assertEquals(new Drone("000"), query.getSingleResult());
    }


}
