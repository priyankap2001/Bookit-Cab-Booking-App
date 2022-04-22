using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Net.Mail;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace WcfService1
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "Service1" in code, svc and config file together.
    // NOTE: In order to launch WCF Test Client for testing this service, please select Service1.svc or Service1.svc.cs at the Solution Explorer and start debugging.
    public class Service1 : IService1
    {
        string constr = ConfigurationManager.ConnectionStrings["connect"].ConnectionString;
        SqlConnection con;
        SqlCommand cmd;
        SqlDataAdapter da;
        DataTable dt;

        public RespMessage register(User user)
        {
            try
            {
                con = new SqlConnection(constr);

                cmd = new SqlCommand("select * from mstUser where EmailID=@email", con);
                cmd.Parameters.AddWithValue("@email", user.EmailID);
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    return new RespMessage { Status = "409", Message = "Email id already exists" };
                }
                else
                {
                    if (con.State != ConnectionState.Open)
                        con.Open();
                    cmd = new SqlCommand("insert into mstUser([Name],[EmailID],[Password],[MobileNo],[UserTypeID]," +
                        "[VehicleNo],[StatusID]) values (@name,@email,@pass,@contact,@type,@vehicleNo, case when @type = 1 then null else 1 end)", con);
                    cmd.Parameters.AddWithValue("@name", user.Name);
                    cmd.Parameters.AddWithValue("@email", user.EmailID);
                    cmd.Parameters.AddWithValue("@pass", user.Password);
                    cmd.Parameters.AddWithValue("@contact", user.MobileNo);
                    cmd.Parameters.AddWithValue("@type", user.UserTypeID);
                    cmd.Parameters.AddWithValue("@vehicleNo", user.VehicleNo);
                    int result = cmd.ExecuteNonQuery();
                    if (result == 1)
                        return new RespMessage { Status = "200", Message = "Registration successful" };
                    else
                        return new RespMessage { Status = "409", Message = "Something went wrong" };
                }
            }

            catch (Exception ex)
            {
                return new RespMessage { Status = "409", Message = ex.ToString() };
            }
        }

        public RespLogin login(string EmailID, string Password)
        {
            try
            {
                con = new SqlConnection(constr);
                cmd = new SqlCommand("select [UserID], [UserTypeID] from mstUser where EmailID=@email and Password=@pass", con);
                cmd.Parameters.AddWithValue("@email", EmailID);
                cmd.Parameters.AddWithValue("@pass", Password);
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    return new RespLogin
                    {
                        Status = "200",
                        UserID = dt.Rows[0]["UserID"].ToString(),
                        UserTypeID = dt.Rows[0]["UserTypeID"].ToString(),
                        Message = "Login successful"
                    };
                }
                else
                {
                    return new RespLogin
                    {
                        Status = "409",
                        UserID = "0",
                        Message = "Invalid email id or password"
                    };
                }
            }
            catch (Exception ex)
            {
                return new RespLogin
                {
                    Status = "409",
                    UserID = "0",
                    Message = ex.ToString()
                };
            }
        }

        public RespMessage updateLocation(string UserID, string Latitude, string Longitude)
        {
            try
            {
                con = new SqlConnection(constr);
                if (con.State != ConnectionState.Open)
                    con.Open();
                cmd = new SqlCommand("update mstUser set [Latitude]=@Latitude,[Longitude]=@Longitude,[UpdatedOn]= getdate()" +
                    "where UserID = @UserID", 
                    con);
                cmd.Parameters.AddWithValue("@UserID", UserID);
                cmd.Parameters.AddWithValue("@Latitude",Latitude);
                cmd.Parameters.AddWithValue("@Longitude", Longitude);
                int result = cmd.ExecuteNonQuery();
                if (result == 1)
                    return new RespMessage { Status = "200", Message = "Record saved" };
                else
                    return new RespMessage { Status = "409", Message = "Something went wrong" };
            }
            catch(Exception ex)
            {
                return new RespMessage { Status = "409", Message = ex.ToString() };
            }
        }

        public List<Driver> getNearbyDriver(string SourceLatitude, string SourceLongitude, string DestLatitude, string DestLongitude)
        {
            List<Driver> list = new List<Driver>();
            try
            {
                con = new SqlConnection(constr);
                cmd = new SqlCommand("usp_getNearbyDriver", con);
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.Parameters.AddWithValue("@iSourceLat", Convert.ToDouble(SourceLatitude));
                cmd.Parameters.AddWithValue("@iSourceLon", Convert.ToDouble(SourceLongitude));
                cmd.Parameters.AddWithValue("@iDestLat", Convert.ToDouble(DestLatitude));
                cmd.Parameters.AddWithValue("@iDestLon", Convert.ToDouble(DestLongitude));
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if(dt.Rows.Count > 0)
                {
                    for (int i =0; i<dt.Rows.Count; i++)
                    {
                        Driver d = new Driver
                        {
                            UserID = dt.Rows[i]["UserID"].ToString(),
                            Name = dt.Rows[i]["Name"].ToString(),
                            MobileNo = dt.Rows[i]["MobileNo"].ToString(),
                            VehicleNo = dt.Rows[i]["VehicleNo"].ToString(),
                            Distance = dt.Rows[i]["Distance"].ToString(),
                            TravelDistance = dt.Rows[i]["TravelDistance"].ToString(),
                            Amount = dt.Rows[i]["Amount"].ToString()
                        };
                        list.Add(d);
                    }
                }        
            }
            catch(Exception ex)
            {

            }
            return list;
        }

        public RespMessage bookCab(Booking b)
        {
            try
            {
                con = new SqlConnection(constr);
                if (con.State != ConnectionState.Open)
                    con.Open();
                cmd = new SqlCommand("insert into trnBooking([UserID],[DriverID],[SourceLatitude],[SourceLongitude]," +
                    "[DestinationLatitude],[DestinationLongitude],[BookingStatusID],[Distance],[Amount],[BookingDate]) " +
                    "values (@UserID,@DriverID,@SourceLatitude,@SourceLongitude,@DestinationLatitude,@DestinationLongitude," +
                    "@BookingStatusID,@Distance,@Amount,getdate())", con);
                cmd.Parameters.AddWithValue("@UserID", b.UserID);
                cmd.Parameters.AddWithValue("@DriverID", b.DriverID);
                cmd.Parameters.AddWithValue("@SourceLatitude", b.SourceLatitude);
                cmd.Parameters.AddWithValue("@SourceLongitude", b.SourceLongitude);
                cmd.Parameters.AddWithValue("@DestinationLatitude", b.DestinationLatitude);
                cmd.Parameters.AddWithValue("@DestinationLongitude", b.DestinationLongitude);
                cmd.Parameters.AddWithValue("@BookingStatusID",1);
                cmd.Parameters.AddWithValue("@Distance", b.Distance);
                cmd.Parameters.AddWithValue("@Amount", b.Amount);
                int result = cmd.ExecuteNonQuery();
                if (result == 1)
                    return new RespMessage { Status = "200", Message = "Booking successful" };
                else
                    return new RespMessage { Status = "409", Message = "Something went wrong" };
            }
            catch(Exception ex)
            {
                return new RespMessage { Status = "409", Message = ex.ToString() };
            }
        }

        public List<BookingDetail> getBookingList(string UserID, string TypeID)
        {
            List<BookingDetail> list = new List<BookingDetail>();
            try
            {
                con = new SqlConnection(constr);
                cmd = new SqlCommand("usp_getBookingList", con);
                cmd.CommandType = CommandType.StoredProcedure;
                cmd.Parameters.AddWithValue("@iUserID", UserID);
                cmd.Parameters.AddWithValue("@iTypeID", TypeID);
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if(dt.Rows.Count > 0)
                {
                    for (int i=0; i < dt.Rows.Count; i++)
                    {
                        BookingDetail b = new BookingDetail
                        {
                            BookingID = dt.Rows[i]["BookingID"].ToString(),
                            UserID = dt.Rows[i]["UserID"].ToString(),
                            Name = dt.Rows[i]["Name"].ToString(),
                            EmailID = dt.Rows[i]["EmailID"].ToString(),
                            MobileNo = dt.Rows[i]["MobileNo"].ToString(),
                            SourceLatitude = dt.Rows[i]["SourceLatitude"].ToString(),
                            SourceLongitude = dt.Rows[i]["SourceLongitude"].ToString(),
                            DestinationLatitude = dt.Rows[i]["DestinationLatitude"].ToString(),
                            DestinationLongitude = dt.Rows[i]["DestinationLongitude"].ToString(),
                            Distance = dt.Rows[i]["Distance"].ToString(),
                            Amount = dt.Rows[i]["Amount"].ToString(),
                            BookingDate = dt.Rows[i]["BookingDate"].ToString(),
                            BookingStatusID = dt.Rows[i]["BookingStatusID"].ToString(),
                            Status = dt.Rows[i]["Status"].ToString(),
                            DriverLatitude = dt.Rows[i]["DriverLatitude"].ToString(),
                            DriverLongitude = dt.Rows[i]["DriverLongitude"].ToString(),
                            OTP = dt.Rows[i]["OTP"].ToString()
                        };
                        list.Add(b);
                    }                  
                }
            }
            catch(Exception ex)
            {

            }
            return list;
        }

        public RespMessage updateStatus(string BookingID, string StatusID, string DriverID)
        {
            try
            {
                int DriverStatusID;
                string OTP="";
                if (StatusID == "3")
                    OTP = GenerateRandomNo();

                con = new SqlConnection(constr);
                if (con.State != ConnectionState.Open)
                    con.Open();
                cmd = new SqlCommand("update trnBooking set BookingStatusID=@BookingStatusID,OTP= case when @BookingStatusID = 3 then @OTP else OTP end where BookingID=@BookingID", con);
                cmd.Parameters.AddWithValue("@BookingStatusID", StatusID);
                cmd.Parameters.AddWithValue("@BookingID", BookingID);
                cmd.Parameters.AddWithValue("@OTP", OTP);
                int result = cmd.ExecuteNonQuery();
                if (result == 1)
                {
                    if (StatusID == "2" || StatusID == "5")
                        DriverStatusID = 1;
                    else
                        DriverStatusID = 2;

                    if (con.State != ConnectionState.Open)
                        con.Open();

                    cmd = new SqlCommand("update mstUser set StatusID = @StatusID where UserID = @DriverID", con);
                    cmd.Parameters.AddWithValue("@StatusID", DriverStatusID);
                    cmd.Parameters.AddWithValue("@DriverID", DriverID);
                    int updtResult = cmd.ExecuteNonQuery();
                    if (updtResult == 1)
                        return new RespMessage { Status = "200", Message = "Booking Status updated" };
                    else
                        return new RespMessage { Status = "409", Message = "Something went wrong while updating driver status" };
                }
                else
                    return new RespMessage { Status = "409", Message = "Something went wrong" };
            }
            catch (Exception ex)
            {
                return new RespMessage { Status = "409", Message = ex.ToString() };
            }
        }

        public RespMessage verifyOTP(string BookingID, string OTP)
        {
            try
            {
                con = new SqlConnection(constr);
                cmd = new SqlCommand("select * from trnBooking where BookingID=@BookingID and OTP=@OTP", con);
                cmd.Parameters.AddWithValue("@BookingID", BookingID);
                cmd.Parameters.AddWithValue("@OTP", OTP);
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if(dt.Rows.Count > 0)
                    return new RespMessage { Status = "200", Message = "Verification Successful" };
                else
                    return new RespMessage { Status = "409", Message = "Vefication Failed" };
            }
            catch(Exception ex)
            {
                return new RespMessage { Status = "409", Message = ex.ToString() };
            }
        }

        public List<BookingList> getBooking()
        {
            List<BookingList> list = new List<BookingList>();
            try
            {
                con = new SqlConnection(constr);
                cmd = new SqlCommand("usp_get_trnBooking", con);
                cmd.CommandType = CommandType.StoredProcedure;
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if(dt.Rows.Count > 0)
                {
                    for (int i = 0; i < dt.Rows.Count; i++)
                    {
                        BookingList b = new BookingList
                        {
                            BookingID = dt.Rows[i]["BookingID"].ToString(),
                            UserID = dt.Rows[i]["UserID"].ToString(),
                            Name = dt.Rows[i]["Name"].ToString(),
                            MobileNo = dt.Rows[i]["MobileNo"].ToString(),
                            DriverID = dt.Rows[i]["DriverID"].ToString(),
                            DriverName = dt.Rows[i]["DriverName"].ToString(),
                            DriverMobileNo = dt.Rows[i]["DriverMobileNo"].ToString(),
                            Distance = dt.Rows[i]["Distance"].ToString(),
                            BookingDate = dt.Rows[i]["BookingDate"].ToString(),
                            Amount = dt.Rows[i]["Amount"].ToString(),
                            Status = dt.Rows[i]["Status"].ToString()
                        };
                        list.Add(b);
                    }
                }
            }
            catch(Exception ex)
            {

            }
            return list;
        }

        public List<User> getUser()
        {
            List<User> list = new List<User>();
            try
            {
                con = new SqlConnection(constr);
                cmd = new SqlCommand("select u.[UserID], u.[Name], u.[MobileNo], case when u.[UserTypeID] = 1 then 'User' when u.[UserTypeID] = 2 then 'Driver' end as [UserType] from mstUser u where u.[UserTypeID] <> 3", con);
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    for (int i = 0; i < dt.Rows.Count; i++)
                    {
                        User u = new User
                        {
                            UserID = dt.Rows[i]["UserID"].ToString(),
                            Name = dt.Rows[i]["Name"].ToString(),
                            MobileNo = dt.Rows[i]["MobileNo"].ToString(),
                            UserType = dt.Rows[i]["UserType"].ToString(),
                        };
                        list.Add(u);
                    }
                }
            }
            catch (Exception ex)
            {

            }
            return list;
        }

        public RespMessage forgotPassword(string EmailID)
        {
            try
            {
                con = new SqlConnection(constr);
                cmd = new SqlCommand("select * from mstUser where EmailID = @iEmailID", con);
                cmd.Parameters.AddWithValue("@iEmailID", EmailID);
                da = new SqlDataAdapter(cmd);
                dt = new DataTable();
                da.Fill(dt);
                if (dt.Rows.Count > 0)
                {
                    string Password = dt.Rows[0]["Password"].ToString();
                    if (sendMail(EmailID, "Your password is:" + Password))
                    {
                        return new RespMessage { Message = "Please check your email" };
                    }
                    else
                    {
                        return new RespMessage { Message = "Error while sending email" };
                    }
                }
                else
                {
                    return new RespMessage { Message = "Email ID not found" };
                }
            }
            catch (Exception ex)
            {
                return new RespMessage { Message = ex.ToString() };
            }
        }

        private bool sendMail(string email, string body)     // send email when status updates
        {
            try
            {
                MailMessage mail = new MailMessage();
                SmtpClient SmtpServer = new SmtpClient("demoproject.in");

                mail.From = new MailAddress("test@demoproject.in");
                //recipient address
                mail.To.Add(new MailAddress(email));    // receiver's email address
                mail.Subject = "New Notification";
                mail.Body = body;
                SmtpServer.Port = 25;
                SmtpServer.Credentials = new System.Net.NetworkCredential("test@demoproject.in", "Password@123");
                SmtpServer.Send(mail);
                return true;
            }
            catch (Exception ex)
            {
                return false;
                throw ex;
            }
        }

        public string GenerateRandomNo()
        {
            int _min = 1000;
            int _max = 9999;
            Random _rdm = new Random();
            return _rdm.Next(_min, _max).ToString();
        }
    }
}
