package fr.polytech.dronepark.exception;

import javax.xml.ws.WebFault;
import java.io.Serializable;

@WebFault(targetNamespace = "http://www.polytech.unice.fr/si/4a/isa/dronedelivery/drone")
public class InvalidDroneIDException extends Exception implements Serializable {

    String badDroneID;

    public InvalidDroneIDException(String badDroneID){
        this.badDroneID = badDroneID;
    }

    public InvalidDroneIDException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Entered drone ID : " + badDroneID + "is no good !";
    }
}
