package com.example.foodliappserver.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;

import com.example.foodliappserver.Model.Request;
import com.example.foodliappserver.Model.User;
import com.example.foodliappserver.Remote.APIService;
import com.example.foodliappserver.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Common {
    public static final String BASE_URL = "https://fcm.googleapis.com/";
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static Request currentRequest;
    public  static  String  topicName = "News";
    public  static  String  PHONE_TEXT = "userPhone";
    public static User currentUser;

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);

    }

   public static final int PICK_IMAGE_REQUEST = 1;
    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "Processing";
        else if (status.equals("2"))
            return "Processed";
        else if (status.equals("3"))
            return "On it's way";
        else
            return "Delivered";

    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){
                for (int i = 0; i < info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;

                    }
                }

            }

        }
        return false;
    }
    // this function converts currency to number base on locale
    public static BigDecimal formatCurrency(String amount, Locale locale) throws ParseException, java.text.ParseException {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        if (format instanceof DecimalFormat){
            ((DecimalFormat)format).setParseBigDecimal(true);
              }
        return ((BigDecimal)format.parse(amount.replace("₦", "").replace(",", "")));

    }
}
