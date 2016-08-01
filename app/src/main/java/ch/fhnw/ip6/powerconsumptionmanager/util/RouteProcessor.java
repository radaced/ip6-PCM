package ch.fhnw.ip6.powerconsumptionmanager.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ch.fhnw.ip6.powerconsumptionmanager.R;
import ch.fhnw.ip6.powerconsumptionmanager.model.chargeplan.RouteInformation;
import okhttp3.Response;

public class RouteProcessor {
    private static final String TAG = "RouteProcessor";

    /**
     * Processes the route information to two locations and stores the loaded information for further use
     * @param response The response from the OkHttp request
     * @param appContext The application context
     * @return true when no successful, false when errors occured
     * @throws IOException
     */
    public static boolean processRoutes(Response response, PowerConsumptionManagerAppContext appContext) throws IOException {
        boolean success = true;
        try {
            JSONObject routeJson = new JSONObject(response.body().string());
            // Navigate through JSON-tree
            JSONArray routesArray = routeJson.getJSONArray("routes");
            // Check if routes exist
            if(!routesArray.isNull(0)) {
                JSONObject route = routesArray.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);

                // Extract data
                JSONObject distance = leg.getJSONObject("distance");
                String distanceText = distance.getString("text");
                JSONObject duration = leg.getJSONObject("duration");
                String durationText = duration.getString("text");

                // Store in application context
                appContext.setRouteInformation(new RouteInformation(durationText, distanceText));
            } else {
                appContext.setRouteInformation(new RouteInformation(
                    appContext.getString(R.string.text_route_information_no_route),
                    ""
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception while processing routes data.");
            success = false;
        }

        return success;
    }
}
