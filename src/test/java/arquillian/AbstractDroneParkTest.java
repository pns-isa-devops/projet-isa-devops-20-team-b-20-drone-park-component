package arquillian;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import fr.polytech.dronepark.components.DroneLauncher;
import fr.polytech.dronepark.utils.DroneAPI;

/**
 * AbstractTCFTest
 */
public class AbstractDroneParkTest {

    @Deployment
    public static WebArchive createDeployment() {
        // @formatter:off
        return ShrinkWrap.create(WebArchive.class)
                // Utils
                .addPackage(DroneAPI.class.getPackage())
                // Components and Interfaces
                .addPackage(DroneLauncher.class.getPackage())
                // Libraries
                .addAsLibraries(Maven.resolver()
                            .loadPomFromFile("pom.xml")
                            .importRuntimeDependencies()
                            .resolve()
                            .withTransitivity()
                            .asFile());


        // @formatter:on
    }
}
