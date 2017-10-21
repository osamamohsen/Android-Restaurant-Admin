package firebaseNotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import osama.restaurantserver.R;
/**
 * Created by osama on 10/14/2017.
 */

public class MyFireBaseInstanceIDService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        Log.d("token", "Refreshed token :1 ");
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", "Refreshed token: " + recent_token);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN),recent_token);
        editor.commit();
    }
}
