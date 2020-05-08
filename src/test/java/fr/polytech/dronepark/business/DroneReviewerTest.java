package fr.polytech.dronepark.business;

import arquillian.AbstractDroneParkTest;
import fr.polytech.dronepark.components.DroneReviewer;
import fr.polytech.dronepark.exception.DroneNotFoundException;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.runner.RunWith;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.*;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class DroneReviewerTest extends AbstractDroneParkTest {
    @EJB
    DroneReviewer droneReviewer;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction utx;

    Drone drone;

    @Before
    public void setup(){
        this.drone = new Drone("001");
        entityManager.persist(drone);
    }

    @Test
    public void chargeDrone() throws DroneNotFoundException {
        drone = entityManager.merge(drone);
        droneReviewer.setDroneInCharge(drone.getDroneId());
        assertEquals(DroneStatus.ON_CHARGE,drone.getDroneStatus());
    }

    @Test
    public void reviseDrone()throws DroneNotFoundException{
        drone = entityManager.merge(drone);
        drone.setFlightTime(10);
        drone = entityManager.merge(drone);
        droneReviewer.putDroneInRevision(drone.getDroneId());
        assertEquals(DroneStatus.ON_REPAIR,drone.getDroneStatus());
        assertEquals(0,drone.getFlightTime());

    }
    @Test
    public void setDroneAvailable() throws DroneNotFoundException{
        drone = entityManager.merge(drone);
        droneReviewer.putDroneInRevision(drone.getDroneId());
        droneReviewer.setDroneAvailable(drone.getDroneId());
        assertEquals(DroneStatus.AVAILABLE,drone.getDroneStatus());
    }

    @After
    public void clean() throws Exception {
        utx.begin();
        this.drone  = entityManager.merge(drone);
        entityManager.remove(drone);
        drone = null;
        utx.commit();

    }



}
