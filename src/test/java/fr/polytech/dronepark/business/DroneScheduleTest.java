package fr.polytech.dronepark.business;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.polytech.entities.Delivery;
import fr.polytech.entities.Parcel;
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

    Delivery del1;
    Delivery del2;
    Delivery del3;
    Delivery del4;

    @Before
    public void setup() throws Exception {
        DroneAPI mocked = mock(DroneAPI.class);
        schedule = new DroneSchedulerController(mocked);
        d1 = new Drone("123");
        d2 = new Drone("456");
        d3 = new Drone("789");
        d4 = new Drone("101");
        del1 = new Delivery("D123123123");
        del2 = new Delivery("D456456456");
        del3 = new Delivery("D789789789");
        del4 = new Delivery("DABCABCABC");

        Parcel p1 = new Parcel("PAR1231234", "1 Rue test", "DHM", "AlexHey");
        Parcel p2 = new Parcel("PAR5675678", "2 Rue test", "DHM", "AlexHoy");
        Parcel p3 = new Parcel("PAAAAAAAAA", "3 Rue test", "DHM", "AlexHay");
        Parcel p4 = new Parcel("PABBBBBBBB", "4 Rue test", "DHM", "AlexHiy");

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.persist(p3);
        entityManager.persist(p4);

        del1.setParcel(p1);
        del2.setParcel(p2);
        del3.setParcel(p3);
        del4.setParcel(p4);

        entityManager.persist(del1);
        entityManager.persist(del2);
        entityManager.persist(del3);
        entityManager.persist(del4);

        d1.setCurrentDelivery(del1);
        d2.setCurrentDelivery(del2);
        d3.setCurrentDelivery(del3);
        d4.setCurrentDelivery(del4);

        when(mocked.getDeliveryStatus(d1)).thenReturn(DeliveryStatus.FAILED);
        when(mocked.getDeliveryStatus(d2)).thenReturn(DeliveryStatus.DELIVERED);
        when(mocked.getDeliveryStatus(d3)).thenReturn(DeliveryStatus.ONGOING);
        when(mocked.getDeliveryStatus(d4)).thenReturn(DeliveryStatus.NOT_DELIVERED);
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

        Delivery del1Stored = entityManager.merge(del1);
        assertEquals(DeliveryStatus.FAILED, del1Stored.getStatus());

        schedule.add(d2, entityManager);
        schedule.add(d4, entityManager);
        schedule.runProcess(entityManager);

        Delivery del2Stored = entityManager.merge(del2);
        assertEquals(DeliveryStatus.DELIVERED, del2Stored.getStatus());

        // Default, not modified yet
        Delivery del3Stored = entityManager.merge(del3);
        assertEquals(DeliveryStatus.NOT_DELIVERED, del3Stored.getStatus());

        Delivery del4Stored = entityManager.merge(del4);
        assertEquals(DeliveryStatus.NOT_DELIVERED, del4Stored.getStatus());

        assertNull(d2.getCurrentDelivery());
        assertNotNull(d3.getCurrentDelivery());
        assertNotNull(d4.getCurrentDelivery());

        schedule.add(d3, entityManager);
        schedule.runProcess(entityManager);

        assertNotNull(d3.getCurrentDelivery());
    }

}
