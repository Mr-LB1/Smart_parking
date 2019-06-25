package javaBean;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
    private int    UserId;
    private String UserName;
    private String SafeCardNumber;
    private String UserCarNumber;
    private String AccountPassWord;
    private String PhoneNumber;
    private Date   CreatDate;
    private Date   ChangeDate;
    private String UserRealName;

    public String toString() {
        return "User [UserName=" + UserName + ", SafeCardNumber=" + SafeCardNumber + ", UserCardNumber=" + UserCarNumber
                + ", AccountPassword=" + AccountPassWord + ", PhoneNumber=" + PhoneNumber + "]";
    }
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSafeCardNumber() {
        return SafeCardNumber;
    }

    public void setSafeCardNumber(String safeCardNumber) {
        SafeCardNumber = safeCardNumber;
    }

    public String getUserCarNumber() { return UserCarNumber; }

    public void setUserCarNumber(String userCarNumber) {
        UserCarNumber = userCarNumber;
    }

    public String getAccountPassWord() {
        return AccountPassWord;
    }

    public void setAccountPassWord(String accountPassWord) {
        AccountPassWord = accountPassWord;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public Date getCreatDate() {
        return CreatDate;
    }

    public void setCreatDate(Date creatDate) {
        CreatDate = creatDate;
    }

    public Date getChangeDate() {
        return ChangeDate;
    }

    public void setChangeDate(Date changeDate) {
        ChangeDate = changeDate;
    }
    public String getUserRealName() { return UserRealName; }

    public void setUserRealName(String userRealName) { UserRealName = userRealName; }
    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }
}
