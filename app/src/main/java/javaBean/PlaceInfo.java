package javaBean;

import java.util.List;

public class PlaceInfo {
    private String name;
    private Double distance;
    private String address;
    private List<ParkingInfo> parkingInfo;

    public List<ParkingInfo> getParkingInfo() {
        return parkingInfo;
    }
    public void setParkingInfo(List<ParkingInfo> parkingInfo) {
        this.parkingInfo = parkingInfo;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

}
