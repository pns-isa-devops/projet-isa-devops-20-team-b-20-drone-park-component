package fr.polytech.dronepark.exception;

import java.io.Serializable;

import javax.xml.ws.WebFault;
import java.lang.Exception;

@WebFault(targetNamespace = "http://www.polytech.unice.fr/si/4a/isa/dronedelivery/drone")
public class DroneNotFoundException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    private String droneId;

    public DroneNotFoundException(String droneId) {
        this.droneId = droneId;
    }


    public DroneNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Drone "+droneId+" not found";
    }
}
