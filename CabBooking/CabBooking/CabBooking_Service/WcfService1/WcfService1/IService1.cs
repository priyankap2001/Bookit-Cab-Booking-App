using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace WcfService1
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the interface name "IService1" in both code and config file together.
    [ServiceContract]
    public interface IService1
    {

        [OperationContract]
        [WebInvoke(Method = "POST", UriTemplate = "register", ResponseFormat = WebMessageFormat.Json)]
        RespMessage register(User user);

        [OperationContract]
        [WebGet(UriTemplate = "login/{email}/{pass}", ResponseFormat = WebMessageFormat.Json)]
        RespLogin login(string email, string pass);

        [OperationContract]
        [WebGet(UriTemplate = "updateLocation/{UserID}/{Latitude}/{Longitude}", ResponseFormat = WebMessageFormat.Json)]
        RespMessage updateLocation(string UserID, string Latitude, string Longitude);

        [OperationContract]
        [WebGet(UriTemplate = "getNearbyDriver/{SourceLatitude}/{SourceLongitude}/{DestLatitude}/{DestLongitude}", ResponseFormat = WebMessageFormat.Json)]
        List<Driver> getNearbyDriver(string SourceLatitude, string SourceLongitude, string DestLatitude, string DestLongitude);

        [OperationContract]
        [WebInvoke(Method = "POST", UriTemplate = "bookCab", ResponseFormat = WebMessageFormat.Json)]
        RespMessage bookCab(Booking b);

        [OperationContract]
        [WebGet(UriTemplate = "getBookingList/{UserID}/{TypeID}", ResponseFormat = WebMessageFormat.Json)]
        List<BookingDetail> getBookingList(string UserID, string TypeID);

        [OperationContract]
        [WebGet(UriTemplate = "updateStatus/{BookingID}/{StatusID}/{DriverID}", ResponseFormat = WebMessageFormat.Json)]
        RespMessage updateStatus(string BookingID, string StatusID, string DriverID);

        [OperationContract]
        [WebGet(UriTemplate = "verifyOTP/{BookingID}/{OTP}", ResponseFormat = WebMessageFormat.Json)]
        RespMessage verifyOTP(string BookingID, string OTP);

        [OperationContract]
        [WebGet(UriTemplate = "getBooking", ResponseFormat = WebMessageFormat.Json)]
        List<BookingList> getBooking();

        [OperationContract]
        [WebGet(UriTemplate = "getUser", ResponseFormat = WebMessageFormat.Json)]
        List<User> getUser();

        [OperationContract]
        [WebGet(UriTemplate = "forgotPassword/{EmailID}/", ResponseFormat = WebMessageFormat.Json)]
        RespMessage forgotPassword(string EmailID);

        // TODO: Add your service operations here
    }


    // Use a data contract as illustrated in the sample below to add composite types to service operations.
    [DataContract]
    public class RespMessage
    {
        [DataMember]
        public string Status { get; set; }

        [DataMember]
        public string Message { get; set; }
    }

    public class RespLogin
    {

        [DataMember]
        public string Status { get; set; }

        [DataMember]
        public string UserID { get; set; }

        [DataMember]
        public string UserTypeID { get; set; }

        [DataMember]
        public string Message { get; set; }
    }

    public class User
    {
        [DataMember]
        public string UserID { get; set; }

        [DataMember]
        public string Name { get; set; }

        [DataMember]
        public string EmailID { get; set; }

        [DataMember]
        public string Password { get; set; }

        [DataMember]
        public string MobileNo { get; set; }

        [DataMember]
        public string VehicleNo { get; set; }

        [DataMember]
        public string UserTypeID { get; set; }

        [DataMember]
        public string UserType { get; set; }
    }

    public class Driver
    {
        [DataMember]
        public string UserID { get; set; }

        [DataMember]
        public string Name { get; set; }

        [DataMember]
        public string MobileNo { get; set; }

        [DataMember]
        public string VehicleNo { get; set; }

        [DataMember]
        public string Distance { get; set; }

        [DataMember]
        public string TravelDistance { get; set; }

        [DataMember]
        public string Amount { get; set; }
    }

    public class Booking
    {
        [DataMember]
        public string BookingID { get; set; }

        [DataMember]
        public string UserID { get; set; }

        [DataMember]
        public string DriverID { get; set; }

        [DataMember]
        public string SourceLatitude { get; set; }

        [DataMember]
        public string SourceLongitude { get; set; }

        [DataMember]
        public string DestinationLatitude { get; set; }

        [DataMember]
        public string DestinationLongitude { get; set; }

        [DataMember]
        public string BookingStatusID { get; set; }

        [DataMember]
        public string Distance { get; set; }

        [DataMember]
        public string Amount { get; set; }

        [DataMember]
        public string BookingDate { get; set; }
    }

    public class BookingDetail
    {
        [DataMember]
        public string BookingID { get; set; }

        [DataMember]
        public string UserID { get; set; }

        [DataMember]
        public string Name { get; set; }

        [DataMember]
        public string EmailID { get; set; }

        [DataMember]
        public string MobileNo { get; set; }

        [DataMember]
        public string SourceLatitude { get; set; }

        [DataMember]
        public string SourceLongitude { get; set; }

        [DataMember]
        public string DestinationLatitude { get; set; }

        [DataMember]
        public string DestinationLongitude { get; set; }

        [DataMember]
        public string Distance { get; set; }

        [DataMember]
        public string Amount { get; set; }

        [DataMember]
        public string BookingDate { get; set; }

        [DataMember]
        public string BookingStatusID { get; set; }

        [DataMember]
        public string Status { get; set; }

        [DataMember]
        public string DriverLatitude { get; set; }

        [DataMember]
        public string DriverLongitude { get; set; }

        [DataMember]
        public string OTP { get; set; }
    }

    public class BookingList
    {
        [DataMember]
        public string BookingID { get; set; }

        [DataMember]
        public string UserID { get; set; }

        [DataMember]
        public string Name { get; set; }

        [DataMember]
        public string MobileNo { get; set; }

        [DataMember]
        public string DriverID { get; set; }

        [DataMember]
        public string DriverName { get; set; }

        [DataMember]
        public string DriverMobileNo { get; set; }

        [DataMember]
        public string Distance { get; set; }

        [DataMember]
        public string BookingDate { get; set; }

        [DataMember]
        public string Amount { get; set; }

        [DataMember]
        public string Status { get; set; }
    }
}
