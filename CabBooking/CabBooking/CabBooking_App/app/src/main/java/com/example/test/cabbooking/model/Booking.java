package com.example.test.cabbooking.model;

public class Booking {

    public String BookingID,UserID,Name,EmailID,MobileNo,DriverID,SourceLatitude,SourceLongitude,DestinationLatitude,
            DestinationLongitude,Distance,Amount,BookingDate,BookingStatusID, Status,DriverLatitude,DriverLongitude,
            OTP;

    public String getBookingID() {
        return BookingID;
    }

    public void setBookingID(String bookingID) {
        BookingID = bookingID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getDriverID() {
        return DriverID;
    }

    public void setDriverID(String driverID) {
        DriverID = driverID;
    }

    public String getSourceLatitude() {
        return SourceLatitude;
    }

    public void setSourceLatitude(String sourceLatitude) {
        SourceLatitude = sourceLatitude;
    }

    public String getSourceLongitude() {
        return SourceLongitude;
    }

    public void setSourceLongitude(String sourceLongitude) {
        SourceLongitude = sourceLongitude;
    }

    public String getDestinationLatitude() {
        return DestinationLatitude;
    }

    public void setDestinationLatitude(String destinationLatitude) {
        DestinationLatitude = destinationLatitude;
    }

    public String getDestinationLongitude() {
        return DestinationLongitude;
    }

    public void setDestinationLongitude(String destinationLongitude) {
        DestinationLongitude = destinationLongitude;
    }

    public String getBookingStatusID() {
        return BookingStatusID;
    }

    public void setBookingStatusID(String bookingStatusID) {
        BookingStatusID = bookingStatusID;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getBookingDate() {
        return BookingDate;
    }

    public void setBookingDate(String bookingDate) {
        BookingDate = bookingDate;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDriverLatitude() {
        return DriverLatitude;
    }

    public void setDriverLatitude(String driverLatitude) {
        DriverLatitude = driverLatitude;
    }

    public String getDriverLongitude() {
        return DriverLongitude;
    }

    public void setDriverLongitude(String driverLongitude) {
        DriverLongitude = driverLongitude;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }
}
