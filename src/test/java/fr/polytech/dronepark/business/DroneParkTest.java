package fr.polytech.dronepark.business;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.util.GregorianCalendar;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
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
public class DroneParkTest extends AbstractDroneParkTest {

    @EJB
    private ControlledDrone droneLauncher;

    Drone droneAvailable;

    @Before
    public void setUpContext() throws ExternalDroneApiException {
        droneAvailable = new Drone();
        droneAvailable.setDroneStatus(DroneStatus.AVAILABLE);
        DroneAPI mocked = mock(DroneAPI.class);
        droneLauncher.useDroneParkReference(mocked);
        doNothing().when(mocked).launchDrone(droneAvailable, new GregorianCalendar());
    }

    @Test
    public void initializeDroneLaunchingTest() throws ExternalDroneApiException {
        Drone droneTest = new Drone();
        assertEquals(DroneStatus.AVAILABLE, droneTest.getDroneStatus());
        droneLauncher.initializeDroneLaunching(droneTest, new GregorianCalendar());
        assertEquals(DroneStatus.ON_DELIVERY, droneTest.getDroneStatus());
    }

    @Test
    public void initializeDroneLaunchingOtherDateTest() throws ExternalDroneApiException {
        Drone droneTest = new Drone();
        assertEquals(DroneStatus.AVAILABLE, droneTest.getDroneStatus());
        droneLauncher.initializeDroneLaunching(droneTest, new GregorianCalendar(2020, 3, 19, 18, 37));
        assertEquals(DroneStatus.ON_DELIVERY, droneTest.getDroneStatus());
    }
}
