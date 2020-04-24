package fr.polytech.dronepark.exception;

import javax.xml.ws.WebFault;
import java.io.Serializable;

@WebFault(targetNamespace = "http://www.polytech.unice.fr/si/4a/isa/dronedelivery/drone")
public class ExternalDroneApiException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public ExternalDroneApiException() {
    }

    public ExternalDroneApiException(String n) {
        super(n);
    }

    public ExternalDroneApiException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "ExternalDroneApiException on " + getMessage() + " ->" + getCause().toString();
    }
}
