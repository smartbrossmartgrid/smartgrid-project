package thesmartbros.sagilbe.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ToolsMap {

	public static String getLocationFromName(double latitude, double longitude) {
		String URL = makeURLforRouteName(latitude, longitude);
		String result = JSONParser.getJSONFromUrl(URL);
		String route = retrieveNameGeo(result);
		if (route == null)
			return latitude+","+longitude;
		else
			return route;
    }
	
	private static String makeURLforRouteName(double latitude, double longitude) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/geocode/json");
        urlString.append("?latlng=");// from
        urlString.append(latitude);
        urlString.append(",");
        urlString.append(longitude);
        urlString.append("&sensor=false");
        return urlString.toString();
    }

	private static String retrieveNameGeo(String result) {
        try {
            JSONArray resultsArray = (new JSONObject(result)).getJSONArray("results");
            JSONObject results = resultsArray.getJSONObject(0);
            String route = results.getString("formatted_address");
            return route;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }	
}
