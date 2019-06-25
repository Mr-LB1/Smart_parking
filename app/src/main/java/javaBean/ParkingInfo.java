package javaBean;

import java.io.Serializable;

public class ParkingInfo implements Serializable {
    private int    PlaceId;
    private String PlaceName;
    private Double ParkPlaceLat;
    private Double ParkPlaceLng;
    private String TotalCarNumber;
    private String FreeNumber;
    private String FreeTime;
    private String Charge;

    public int getPlaceId() {
        return PlaceId;
    }
    public void setPlaceId(int placeId) {
        PlaceId = placeId;
    }
    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public Double getParkPlaceLat() {
        return ParkPlaceLat;
    }

    public void setParkPlaceLat(Double parkPlaceLat) {
        ParkPlaceLat = parkPlaceLat;
    }

    public Double getParkPlaceLng() {
        return ParkPlaceLng;
    }

    public void setParkPlaceLng(Double parkPlaceLng) {
        ParkPlaceLng = parkPlaceLng;
    }

    public String getTotalCarNumber() {
        return TotalCarNumber;
    }
    public void setTotalCarNumber(String totalCarNumber) {
        TotalCarNumber = totalCarNumber;
    }
    public String getFreeNumber() {
        return FreeNumber;
    }
    public void setFreeNumber(String freeNumber) {
        FreeNumber = freeNumber;
    }
    public String getFreeTime() {
        return FreeTime;
    }

    public void setFreeTime(String freeTime) {
        FreeTime = freeTime;
    }

    public String getCharge() {
        return Charge;
    }

    public void setCharge(String charge) {
        Charge = charge;
    }
}
