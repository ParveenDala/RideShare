package com.parveendala.rideshare.network;


import com.parveendala.rideshare.model.Direction;
import com.parveendala.rideshare.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/************************
 * Parveen Dala
 * 25th May, 2019
 * BikePool - Bengaluru
 */
public interface ApiService {
    @GET(Constants.DIRECTION_API_URL)
    Call<Direction> getDirection(@Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("waypoints") String waypoints,
                                 @Query("key") String apiKey);
}
