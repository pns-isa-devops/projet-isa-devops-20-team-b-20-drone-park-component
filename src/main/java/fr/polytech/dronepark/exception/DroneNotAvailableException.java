package fr.polytech.dronepark.exception;

import javax.xml.ws.WebFault;
import java.io.Serializable;

@WebFault(targetNamespace = "http://www.polytech.unice.fr/si/4a/isa/dronedelivery/drone")
public class DroneNotAvailableException extends Exception implements Serializable {

    private String droneId;

    public DroneNotAvailableException(String droneId) {
        this.droneId = droneId;
    }

    @Override
    public String getMessage() {
        return "Drone "+droneId+" is not available.";
    }

}
