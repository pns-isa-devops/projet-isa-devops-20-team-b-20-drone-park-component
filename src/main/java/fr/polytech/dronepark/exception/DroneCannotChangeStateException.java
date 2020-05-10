package fr.polytech.dronepark.exception;

import fr.polytech.entities.DroneStatus;

import javax.xml.ws.WebFault;
import java.io.Serializable;

@WebFault(targetNamespace = "http://www.polytech.unice.fr/si/4a/isa/dronedelivery/drone")
public class DroneCannotChangeStateException extends Exception implements Serializable {

    private String droneId;
    private DroneStatus droneStatus;

    public DroneCannotChangeStateException(String droneId, DroneStatus droneStatus) {
        this.droneId = droneId;
        this.droneStatus = droneStatus;
    }

    @Override
    public String getMessage() {
        String status;
        switch (droneStatus) {
            case ON_CHARGE:
                status = "on charge";
                break;
            case ON_DELIVERY:
                status = "on delivery";
                break;
            case ON_REPAIR:
                status = "on review";
                break;
            default:
                status = "available";
        }
        return "The drone " + droneId + " is not available. It is currently " + status + ".";
    }

}

