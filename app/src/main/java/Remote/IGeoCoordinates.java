package Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by osama on 10/20/2017.
 */

public interface IGeoCoordinates {
    @GET("maps/api/geocode/json")
    Call<String> geGeoCode(@Query("address") String address);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin , @Query("destination") String destination);
}
