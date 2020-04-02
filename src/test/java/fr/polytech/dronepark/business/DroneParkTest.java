package fr.polytech.dronepark.business;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import arquillian.AbstractDroneParkTest;
import fr.polytech.dronepark.components.ControlledDrone;
import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;

/**
 * DronePark
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class DroneParkTest extends AbstractDroneParkTest {

    @EJB
    private ControlledDrone droneLauncher;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction utx;

    Drone drone;

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
        utx.commit();
    }

    private void initDate() {
        drone = new Drone("123");
        entityManager.persist(drone);
    }

    private void initMock() throws ExternalDroneApiException {
        drone.setDroneStatus(DroneStatus.AVAILABLE);
        DroneAPI mocked = mock(DroneAPI.class);
        droneLauncher.useDroneParkReference(mocked);
        when(mocked.launchDrone(drone, new GregorianCalendar())).thenReturn(true);
    }

    @Test
    public void initializeDroneLaunchingTest() throws ExternalDroneApiException {
        Drone droneTest = entityManager.merge(drone);
        droneTest.setDroneStatus(DroneStatus.AVAILABLE);

        assertEquals(DroneStatus.AVAILABLE, droneTest.getDroneStatus());
        droneLauncher.initializeDroneLaunching(droneTest, new GregorianCalendar());
        assertEquals(DroneStatus.ON_DELIVERY, droneTest.getDroneStatus());
    }

    @Test
    public void initializeDroneLaunchingOtherDateTest() throws ExternalDroneApiException {
        Drone droneTest = entityManager.merge(drone);
        droneTest.setDroneStatus(DroneStatus.AVAILABLE);

        assertEquals(DroneStatus.AVAILABLE, droneTest.getDroneStatus());
        droneLauncher.initializeDroneLaunching(droneTest, new GregorianCalendar(2020, 3, 19, 18, 37));
        assertEquals(DroneStatus.ON_DELIVERY, droneTest.getDroneStatus());
    }
}
