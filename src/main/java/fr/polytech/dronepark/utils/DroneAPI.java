package fr.polytech.dronepark.utils;

import java.util.GregorianCalendar;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.json.JSONObject;

import fr.polytech.dronepark.exception.ExternalDroneApiException;
import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneStatus;

public class DroneAPI {
    private String url;

    public DroneAPI(String host, String port) {
        this.url = "http://" + host + ":" + port;
    }

    public DroneAPI() {
        this("localhost", "9090");
    }

    public DroneStatus getDroneStatus(Drone drone) throws ExternalDroneApiException {
        // Retrieving the drone status
        DroneStatus status = null;
        try {
            String response = WebClient.create(url).path("/drone/" + drone.getDroneId() + "/status").get(String.class);
            status = DroneStatus.valueOf(new JSONObject(response).getString("status"));
        } catch (Exception e) {
            throw new ExternalDroneApiException(url + "/drone/" + drone.getDroneId() + "/status", e);
        }
        return status;
    }

    public boolean launchDrone(Drone drone, GregorianCalendar launchHour) throws ExternalDroneApiException {

        try {
            DroneStatus status = getDroneStatus(drone);
            if (status != null && status != DroneStatus.BACK_FROM_DELIVERY) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        String launchHourString = launchHour.get(GregorianCalendar.HOUR) + ":"
                + launchHour.get(GregorianCalendar.MINUTE);

        // Build request
        JSONObject request = new JSONObject().put("id", drone.getDroneId()).put("hour", launchHourString);

        // Launch
        try {
            WebClient.create(url).path("/drone/launch").accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("Content-Type", MediaType.APPLICATION_JSON).post(request.toString(), String.class);
        } catch (Exception e) {
            throw new ExternalDroneApiException(url + "/drone/launch", e);
        }
        return true;
    }

}
