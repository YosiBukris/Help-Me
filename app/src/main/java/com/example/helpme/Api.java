package com.example.helpme;

import com.squareup.okhttp.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAACHiao00:APA91bEVwwas40hOZ0D7Z99myfE6XQTj-A6UpxhfDNFv8HsrDUBLErA1p3BRyeRx-3k18hYxvodYrZ5xR3JVNg5zyY5N90DmYaqy4LZtUxRuZc6yE1K2ktf1kn9OciS9J0juCS-guatu"
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body Sender body);
}
