package fr.polytech.dronepark.utils;

import fr.polytech.dronepark.exception.DroneNotAvailableException;
import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.entities.Delivery;
import fr.polytech.entities.DeliveryStatus;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;
import org.apache.cxf.jaxrs.client.WebClient;
import org.json.JSONObject;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import java.util.GregorianCalendar;

public class DroneAPI {
    private static final String DRONE_ADDRESS = "/drone/";
    private static final String STATUS_ADDRESS = "/status";
    private String url;

    public DroneAPI(String host, String port) {
        this.url = "http://" + host + ":" + port;
    }

    DroneAPI() {
        this("localhost", "9090");
    }

    private DroneStatus getDroneStatus(Drone drone) throws ExternalDroneApiException {
        // Retrieving the drone status
        DroneStatus status = null;
        try {
            String response = WebClient.create(url).path(DRONE_ADDRESS + drone.getDroneId() + STATUS_ADDRESS).get(String.class);
            status = DroneStatus.valueOf(new JSONObject(response).getString("status"));
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new ExternalDroneApiException(url + DRONE_ADDRESS + drone.getDroneId() + STATUS_ADDRESS, e);
        }
        return status;
    }

    /**
     * retrieve the status of a delivery from a drone job
     *
     * @param drone
     * @return
     * @throws ExternalDroneApiException
     */
    public DeliveryStatus getDeliveryStatus(Drone drone) throws ExternalDroneApiException {
        DeliveryStatus status = null;
        try {
            String response = WebClient.create(url).path(DRONE_ADDRESS + drone.getDroneId() + STATUS_ADDRESS).get(String.class);
            status = DeliveryStatus.valueOf(new JSONObject(response).getString("delivery"));
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new ExternalDroneApiException(url + DRONE_ADDRESS + drone.getDroneId() + STATUS_ADDRESS, e);
        }
        return status;
    }

    public boolean launchDrone(Drone drone, GregorianCalendar launchHour, Delivery delivery)
            throws ExternalDroneApiException, DroneNotAvailableException {
        DroneStatus status = getDroneStatus(drone);
        if (status != null && status != DroneStatus.AVAILABLE) {
            throw new DroneNotAvailableException(drone.getDroneId());
        }
        String launchHourString = launchHour.get(GregorianCalendar.HOUR) + ":"
                + launchHour.get(GregorianCalendar.MINUTE);

        // Build request
        JSONObject request = new JSONObject().put("id", drone.getDroneId()).put("hour", launchHourString)
                .put("destination", delivery.getParcel().getAddress());

        // Launch
        try {
            WebClient.create(url).path("/drone/launch").accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("Content-Type", MediaType.APPLICATION_JSON).post(request.toString(), String.class);
        } catch (Exception e) {
            throw new ExternalDroneApiException(url + DRONE_ADDRESS + drone.getDroneId() + "/launch", e);
        }
        return true;
    }

}
