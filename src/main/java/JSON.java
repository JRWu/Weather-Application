

import java.io.*;
import java.net.*;
import java.sql.Time;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.swing.ImageIcon;

import java.util.GregorianCalendar;

import org.apache.commons.io.*;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

/*unfortunately I needed to add some referenced libraries. 
To get the JSON jar go to this link: http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.json%22%20AND%20a%3A%22json%22
I'm using version 20131018 as for some reason the 2014 wouldn't work for my system. 
org.apache.commons.io.* can be found here: http://commons.apache.org/proper/commons-io/
Version 2.4 is the one I'm using, older versions *should* work as well, haven't been tested though. 
Best way to handle this would be to make a MAVEN pom file, but I haven't figured that out yet so until then
this is the way to go. 	

*/

public class JSON {
//	private String url = "http://api.openweathermap.org/data/2.5/forecast?q=london,ca&cnt=7";
	private final String HOST = "http://api.openweathermap.org"; //host and protocol 
	private final String PATH_CURRENT = "/data/2.5/weather";	//where we'll be getting the current weather from
	private final String PATH_FORECAST = "/data/2.5/forecast";	//where we'll be getting short term and long term forecasts from
	private final String PATH_ICON = "/img/w";
	private final String MAIN_JSON = "main";
	private final String WEATHER_JSON = "weather";
	private final String WIND_JSON = "wind";
	private final String ARRAY_JSON = "list";
	private final String SUN_JSON = "sys";
	
	private final int TIMEOUT_TIME = 5000; //the time it'll take for connection to timeout
	
	private String query =  "?q="; 	//variable query, dependent on location and whether it will be current, short term, or long term
	
	private String location = ""; //String to contain location
	private URL weatherURL,iconURL;	//URL Object, used to create connection
	private double temp,temp_max,temp_min,windSpeed; //all the variables for CURRENT weather (forecast still needs to be figured out)
	private int pressure, humidity, windDirectionDegree;
	GregorianCalendar time,sunrise,sunset; 
	private long sunrise1;
	private String weatherDescription, skyState, windDirection; //This may change, need to test out how the currentWeatherSetVariables function is for a while
	private JSONObject allWeatherData, shortTermData, longTermData; 
	private ImageIcon icon;
	
	/**
	 * Returns a JSON object to use for obtaining weather data. 
	 * 
	 * @param l the string for the location JSON object will be grabbing. 
	 */
	public JSON (String l){ //When creating the JSON object, initially set the location (eg, London, CA for our city)
		
		location = l;
		query += location;//first part of the query is always the location. 
		
		
	}
	
	
	
	/**
	 * Method to obtain current weather data; in the future it will throw an error if it cannot connect. 
	 * 
	 * @return a Current object with all fields filled for the current weather data. 
	 */
//	public Current updateCurrentWeatherData() throws NoConnectionException {
	public Current updateCurrentWeatherData(){ 
		try{
			weatherURL = new URL(HOST + PATH_CURRENT + query);
			HttpURLConnection connect = (HttpURLConnection) weatherURL.openConnection();
			connect.setConnectTimeout(TIMEOUT_TIME);
//			if (HttpURLConnection.HTTP_OK != connect.getResponseCode()) throw new NoConnectionException(connect.getResponseMessage());
			if (HttpURLConnection.HTTP_OK != connect.getResponseCode()) 
				System.out.println("problem");
			InputStream in = connect.getInputStream();
			String jsonString = IOUtils.toString(in);
			JSONObject currentWeatherData = new JSONObject(jsonString);
			allWeatherData = new JSONObject(jsonString);
			time = (GregorianCalendar) GregorianCalendar.getInstance();
			time.setTimeInMillis(1000 * currentWeatherData.getLong("dt"));
			
			JSONObject main = currentWeatherData.getJSONObject(MAIN_JSON);
			JSONArray weather = currentWeatherData.getJSONArray(WEATHER_JSON);
			JSONObject wind = currentWeatherData.getJSONObject(WIND_JSON);
			JSONObject sun = currentWeatherData.getJSONObject(SUN_JSON);
			currentMainSetVariables(main);
			currentWeatherSetVariables(weather);
			currentWindSetVariables(wind);
			currentSunTime(sun);
			//return ADO_Object

			return new Current(time,sunrise,sunset,pressure,windSpeed,temp,temp_min,temp_max,humidity,windDirection,skyState,icon);
	
		}catch (SocketTimeoutException e)
		{
			//throw NoConnectionException with timeout.
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
		
		return null;
	}

	
	/**
	 * Private method to set all temperature and humidity and pressure fields
	 * 
	 * @param main JSONObject containing these fields.
	 */
	private void currentMainSetVariables(JSONObject main){
		humidity = main.getInt("humidity");
		pressure = main.getInt("pressure");
		temp_max = main.getDouble("temp_max");
		temp_min = main.getDouble("temp_min");
		temp = main.getDouble("temp");
		}
	
	private void shortTermMainSetVariables(JSONObject main){
		humidity = main.getInt("humidity");
		pressure = main.getInt("pressure");
		temp_max = main.getDouble("temp_max");
		temp_min = main.getDouble("temp_min");
		temp = main.getDouble("temp");
		}
	
	
	/**
	 * Private method for weather variables. Gets sky state and weather description as well as ImageIcon for skystate
	 * 
	 * @param weather JSONArray containing all weather fields
	 */
	private void currentWeatherSetVariables(JSONArray weather){
		JSONObject weatherData= weather.getJSONObject(0);//easier to represent the current weather as a JSONObject than array
		weatherDescription = weatherData.getString("description");
		skyState = weatherData.getString("main");
		
		//Uses a URL to grab an ImageIcon, to later be implemented in the data
		try {
			iconURL = new URL(HOST + PATH_ICON + weatherData.getString("icon") + ".png");
			icon = new ImageIcon(iconURL);
		}catch (Exception e){}
		
	}
	
	private void shortTermWeatherSetVariables(JSONArray weather){
		
		JSONObject weatherData= weather.getJSONObject(0);//easier to represent the current weather as a JSONObject than array
		weatherDescription = weatherData.getString("description");
		skyState = weatherData.getString("main");
		
		//Uses a URL to grab an ImageIcon, to later be implemented in the data
		try {
			iconURL = new URL(HOST + PATH_ICON + weatherData.getString("icon") + ".png");
			icon = new ImageIcon(iconURL);
		}catch (Exception e){}
		
	}
	
	/**
	 * Private method that gets wind data and converts the degree to a rough direction
	 * 
	 * @param wind JSONObject containing all wind data
	 */
	private void currentWindSetVariables(JSONObject wind){
		windDirectionDegree = wind.getInt("deg");
		windSpeed = wind.getDouble("speed");
		String [] direction = {"North","North-Northeast","Northeast","East-Northeast","East","East-Southeast", "Southeast", "South-Southeast","South","South-Southwest","Southwest","West-Southwest","West","West-Northwest","Northwest","North-Northwest"};
		
		windDirection = direction[(int) (( windDirectionDegree/22.5)%16)];
	}
	
	/**
	 * method to set the variables for shortTerm wind
	 * 
	 * @param wind JSONObject that conatins all wind variables required. 
	 */
	private void shortTermWindSetVariables(JSONObject wind){
		windDirectionDegree = wind.getInt("deg");
		windSpeed = wind.getDouble("speed");
		String [] direction = {"North","North-Northeast","Northeast","East-Northeast","East","East-Southeast", "Southeast", "South-Southeast","South","South-Southwest","Southwest","West-Southwest","West","West-Northwest","Northwest","North-Northwest"};
		
		windDirection = direction[(int) (( windDirectionDegree/22.5)%16)];
	}
	
	/**
	 * Private method that gets data for sunrise and sunset and sets it
	 * 
	 * @param sun JSONObject for sunrise and sunset data
	 */
	private void currentSunTime(JSONObject sun){
		//TODO get Billy to change time, sunrise, and sunset fields in current to a calendar instead of int
		sunrise= (GregorianCalendar) GregorianCalendar.getInstance();
		sunset = (GregorianCalendar) GregorianCalendar.getInstance();
		
		
		sunrise.setTimeInMillis(1000 *sun.getLong("sunrise"));
		sunset.setTimeInMillis(1000 * sun.getLong("sunset"));
		
	}
	
	public static void main (String [] args){
		JSON asdf = new JSON("London,ca");
		asdf.updateCurrentWeatherData();
	}

	
	
	/**
	 * Method to return short term data, comes as 3 hour increments spanning 24 hours
	 * @return a ShortTerm object 
	 */
//	public hourly [] updateShortTermData() throws NoConnectionException {
	public ShortTerm updateShortTermData(){
		Hourly [] shortTermHourlies = new Hourly[8];
		try{
		//creates the weather URL 
		weatherURL = new URL(HOST + PATH_FORECAST + query);
		
		HttpURLConnection connect = (HttpURLConnection) weatherURL.openConnection();
		connect.setConnectTimeout(TIMEOUT_TIME);//sets timeout
//		if (HttpURLConnection.HTTP_OK != connect.getResponseCode()) throw new NoConnectionException(connect.getResponseMessage());
		
		//Gets input stream and converts it to string to be handled by JSONObject
		InputStream in = connect.getInputStream();
		String jsonString = IOUtils.toString(in);
		shortTermData = new JSONObject(jsonString); 
		JSONArray arrayData = shortTermData.getJSONArray(ARRAY_JSON);//creates a JSON array with all the tri-hourly seperation
		
		//loop to get the 8 hourly objects
		for (int i =0; i <8; i++){
			JSONObject hour = arrayData.getJSONObject(i);
			
			shortTermMainSetVariables(hour.getJSONObject(MAIN_JSON));
			shortTermWeatherSetVariables(hour.getJSONArray(WEATHER_JSON));
			shortTermWindSetVariables(hour.getJSONObject(WIND_JSON));
			time = (GregorianCalendar) GregorianCalendar.getInstance();
			time.setTimeInMillis(1000 * hour.getLong("dt"));
			
			shortTermHourlies[i] = new Hourly(time.get(GregorianCalendar.HOUR_OF_DAY),pressure,windSpeed,temp,temp_min,temp_max,humidity,windDirection,skyState,icon);
		}
		
		
		
		}catch(MalformedURLException e){
			System.out.println("BAD URL");
		}catch(SocketTimeoutException e){
			//throw e;
		}catch(IOException e){
			//throw new NoConnectionException ("No connection established");
		}
		return new ShortTerm(shortTermHourlies);
	}
	
	
}
