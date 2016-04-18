package com.prodyna;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.helper.StringUtil;

/**
 * 
 * Calculate Travel time between point x and point y using Google Distance API
 * 
 * @author Nebojsa Kojic
 *
 */
public class TravelTimeService {
	
	private static String URL_PREF = "https://maps.googleapis.com/maps/api/directions/json?origin=";

	private static String DEPARTURE_TIME = "&departure_time=";
	private static String URL_SUF = "&mode=transit&alternatives=true&key=";
	
	private static String KEY = PropertiesUtil.getPropFromFile(4);
	
	//JSON node Names
	private static final String ROUTES = "routes";
	private static final String LEGS = "legs";
    private static final String DURATION = "duration";
    private static final String VALUE = "value";
	
	public static String getTravelTime(String start, String destination){
		
		if(start == null || StringUtil.isBlank(start)){
			return "";
		}
		
		int minDuration = Integer.MAX_VALUE;
		try {
			URL url = new URL(URL_PREF + start.replaceAll(" ", "") + "&destination=" + destination + DEPARTURE_TIME + getLastMonday7_45AM() + URL_SUF + KEY);

			JSONTokener tokener = new JSONTokener(IOUtils.toString(url.openStream()));

			JSONObject respons = (JSONObject) tokener.nextValue();
			
			JSONArray routes = respons.getJSONArray(ROUTES);
			
			Iterator<Object> routesIter = routes.iterator();
			for(int i = 0; i < routes.length(); i++){
				JSONObject route = (JSONObject) routesIter.next();
				JSONArray legs = route.getJSONArray(LEGS);
				JSONObject duration = ((JSONObject)legs.iterator().next()).getJSONObject(DURATION);
				int durationInS = duration.getInt(VALUE);
				if (durationInS < minDuration){
					minDuration = durationInS;
				}
			}		
	
	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
		
		return convertToHours(minDuration);
	}

	private static long getLastMonday7_45AM() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		c.set(Calendar.AM_PM, 0);
		c.set(Calendar.HOUR, 7);
		c.set(Calendar.MINUTE, 45);
		c.set(Calendar.SECOND, 0);
		return c.getTimeInMillis()/1000;
	}

	private static String convertToHours(int totalSecs) {
		int hours = totalSecs / 3600;
		int minutes = (totalSecs % 3600) / 60;

		return String.format("%02d:%02d", hours, minutes) + " hour";
	}
	
}
