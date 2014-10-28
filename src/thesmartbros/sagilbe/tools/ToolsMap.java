package thesmartbros.sagilbe.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ToolsMap {

	public static String getLocationFromName(double latitude, double longitude) {
		String URL = makeURLforRouteName(latitude, longitude);
		String result = (new JSONParser()).getJSONFromUrl(URL);
		String[] results = retrieveNameGeo(result);
		if (results == null)
			return latitude+","+longitude;
		else
			return results[0]+results[1];
    }
	
	private static String makeURLforRouteName(double latitude, double longitude) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/geocode/json");
        urlString.append("?latlng=");// from
        urlString.append(latitude);
        urlString.append(",");
        urlString.append(longitude);
        urlString.append("&sensor=false");
        return urlString.toString();
    }

	private static String[] retrieveNameGeo(String result) {
        try {
            JSONArray resultsArray = (new JSONObject(result)).getJSONArray("results");
            JSONObject results = resultsArray.getJSONObject(0);
            JSONArray myResults = results.getJSONArray("address_components");
            String route = myResults.getJSONObject(1).getString("short_name");
            String locality = myResults.getJSONObject(2).getString("short_name");
            if (route == null || locality == null)
            	return null;
            else {
            	String[] res = new String[2];
            	res[0] = route;
            	res[1] = locality;            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }	
}
