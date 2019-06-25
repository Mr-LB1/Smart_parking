package javaBean;

public class Reservation {
    private int PlaceId;

    public int getPlaceId() {
        return PlaceId;
    }

    public void setPlaceId(int placeId) {
        PlaceId = placeId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getNaviGation() {
        return NaviGation;
    }

    public void setNaviGation(int naviGation) {
        NaviGation = naviGation;
    }

    public String getFromData() {
        return FromData;
    }

    public void setFromData(String fromData) {
        FromData = fromData;
    }

    public String getToData() {
        return ToData;
    }

    public void setToData(String toData) {
        ToData = toData;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getChangeDate() {
        return ChangeDate;
    }

    public void setChangeDate(String changeDate) {
        ChangeDate = changeDate;
    }
    public int getRevervaTionId() {
        return RevervaTionId;
    }

    public void setRevervaTionId(int revervaTionId) {
        RevervaTionId = revervaTionId;
    }

    public String getParkName() {
        return ParkName;
    }

    public void setParkName(String parkName) {
        ParkName = parkName;
    }
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    private int       RevervaTionId;
    private int       UserId;
    private int       NaviGation;
    private String UserName;
    private String ParkName;
    private String FromData;
    private String ToData;
    private String CreateDate;
    private String ChangeDate;
}