package com.example.taehun.totalmanager.Firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIDService";
    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Refresh token" + token);
        //sendRegistarionToServer(token);
    }

    private void sendRegistarionToServer(String token){

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder().add("Token", token).build();
        Request request = new Request.Builder().url("http://xognsxo1491.cafe24.com/register.php").post(body).build();

        try{
            client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
