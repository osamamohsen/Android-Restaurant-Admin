package firebaseNotification;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by osama on 10/14/2017.
 */

public class MySingleton {
    public static MySingleton mSingletonInstance;
    private static Context mCtx;
    private RequestQueue requestQueue;

    public MySingleton(Context context) {
        this.mCtx = context;
        this.requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return  requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context){
        if(mSingletonInstance == null){
            mSingletonInstance = new MySingleton(context);
        }
        return mSingletonInstance;
    }

    public <T> void addToRequestque(Request<T> request){
        getRequestQueue().add(request);
    }
}
