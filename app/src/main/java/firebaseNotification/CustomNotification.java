package firebaseNotification;

import android.content.Context;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import osama.restaurantserver.Home;

/**
 * Created by osama on 10/16/2017.
 */

public class CustomNotification {
    String token;
    private String url;
    Context view;
    public CustomNotification(String token , String url, Context view) {
        this.token = token;
        this.url = url;
        this.view = view;
    }

    public void send_notification(String title,String message){
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, this.url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("fcm_token",token);
                return params;
            }
        };
        MySingleton.getInstance(view).addToRequestque(stringRequest);
    }
}
