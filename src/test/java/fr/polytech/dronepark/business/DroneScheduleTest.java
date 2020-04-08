package fr.polytech.dronepark.business;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import arquillian.AbstractDroneParkTest;
import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.dronepark.utils.DroneSchedulerController;
import fr.polytech.entities.DeliveryStatus;
import fr.polytech.entities.Drone;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class DroneScheduleTest extends AbstractDroneParkTest {

    @PersistenceContext
    private EntityManager entityManager;

    private DroneSchedulerController schedule;

    Drone d1;
    Drone d2;
    Drone d3;
    Drone d4;

    @Before
    public void setup() throws Exception {
        DroneAPI mocked = mock(DroneAPI.class);
        schedule = new DroneSchedulerController(mocked);
        d1 = new Drone("123");
        d2 = new Drone("456");
        d3 = new Drone("789");
        d4 = new Drone("101");
        when(mocked.getDeliveryStatus(d1)).thenReturn(DeliveryStatus.FAILED);
        when(mocked.getDeliveryStatus(d2)).thenReturn(DeliveryStatus.DELIVERED);
        when(mocked.getDeliveryStatus(d3)).thenReturn(DeliveryStatus.ONGOING);
        when(mocked.getDeliveryStatus(d4)).thenReturn(DeliveryStatus.DELIVERED);
    }

    @Test
    public void droneScheduleTest() throws Exception {
        entityManager.persist(d1);
        entityManager.persist(d2);
        entityManager.persist(d3);
        entityManager.persist(d4);

        assertNotNull((Drone) entityManager.find(Drone.class, d1.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d2.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d3.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d4.getId()));

        schedule.add(d1, entityManager);
        schedule.runProcess(entityManager);

        assertNotNull((Drone) entityManager.find(Drone.class, d1.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d2.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d3.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d4.getId()));

        schedule.add(d2, entityManager);
        schedule.add(d4, entityManager);
        schedule.runProcess(entityManager);
        schedule.runProcess(entityManager);

        assertNotNull((Drone) entityManager.find(Drone.class, d1.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d2.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d3.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d4.getId()));

        schedule.add(d3, entityManager);
        schedule.runProcess(entityManager);

        assertNotNull((Drone) entityManager.find(Drone.class, d1.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d2.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d3.getId()));
        assertNotNull((Drone) entityManager.find(Drone.class, d4.getId()));
    }
}
