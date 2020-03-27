package arquillian;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import fr.polytech.dronepark.components.DroneLauncher;
import fr.polytech.dronepark.utils.DroneAPI;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;

/**
 * AbstractTCFTest
 */
public class AbstractDroneParkTest {

    @Deployment
    public static WebArchive createDeployment() {
        // @formatter:off
        return ShrinkWrap.create(WebArchive.class)
                // Entities
                .addPackage(Drone.class.getPackage())
                .addPackage(DroneStatus.class.getPackage())
                // Utils
                .addPackage(DroneAPI.class.getPackage())
                // Components and Interfaces
                .addPackage(DroneLauncher.class.getPackage());


        // @formatter:on
    }
}
