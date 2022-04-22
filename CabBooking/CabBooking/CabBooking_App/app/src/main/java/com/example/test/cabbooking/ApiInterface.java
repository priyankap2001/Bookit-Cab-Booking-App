package com.example.test.cabbooking;


import com.example.test.cabbooking.model.Booking;
import com.example.test.cabbooking.model.BookingList;
import com.example.test.cabbooking.model.Driver;
import com.example.test.cabbooking.model.RespLogin;
import com.example.test.cabbooking.model.ResponseMsg;
import com.example.test.cabbooking.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    String Url="http://demoproject.in/cabbooking_service/Service1.svc/";
    //String Url = "http://192.168.0.104/cabbooking_service/Service1.svc/";

    @GET("login/{email}/{password}")
    @Headers({"Content-Type: application/json"})
    Call<RespLogin> login(@Path("email") String email, @Path("password") String password);

    @POST("register")
    @Headers({"Content-Type: application/json"})
    Call<ResponseMsg> register(@Body User user);

    @GET("forgotPassword/{EmailID}/")
    @Headers({"Content-Type: application/json"})
    Call<ResponseMsg> forgotPassword(@Path("EmailID") String EmailID);

    @GET("updateLocation/{UserID}/{Latitude}/{Longitude}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseMsg> updateLocation(@Path("UserID") String UserID, @Path("Latitude") String Latitude,
                                     @Path("Longitude") String Longitude);

    @GET("getNearbyDriver/{SourceLatitude}/{SourceLongitude}/{DestLatitude}/{DestLongitude}")
    @Headers({"Content-Type: application/json"})
    Call<List<Driver>> getNearbyDriver(@Path("SourceLatitude") String SourceLatitude,
                                       @Path("SourceLongitude") String SourceLongitude,
                                       @Path("DestLatitude") String DestLatitude,
                                       @Path("DestLongitude") String DestLongitude);

    @POST("bookCab")
    @Headers({"Content-Type: application/json"})
    Call<ResponseMsg> bookCab(@Body Booking b);

    @GET("getBookingList/{UserID}/{TypeID}")
    @Headers({"Content-Type: application/json"})
    Call<List<Booking>> getBookingList(@Path("UserID") String UserID, @Path("TypeID") String TypeID);

    @GET("updateStatus/{BookingID}/{StatusID}/{DriverID}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseMsg> updateStatus(@Path("BookingID") String BookingID, @Path("StatusID") String StatusID,
                                   @Path("DriverID") String DriverID);

    @GET("verifyOTP/{BookingID}/{OTP}")
    @Headers({"Content-Type: application/json"})
    Call<ResponseMsg> verifyOTP(@Path("BookingID") String BookingID, @Path("OTP") String OTP);

    @GET("getBooking")
    @Headers({"Content-Type: application/json"})
    Call<List<BookingList>> getBooking();

    @GET("getUser")
    @Headers({"Content-Type: application/json"})
    Call<List<User>> getUser();


}
