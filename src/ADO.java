/**
* This class represents the general instances variables and classes shared between the different objects
* @author Team 6
*/
public class ADO
{
/**
* Instance variables
*/
	private int airPressure;
	private double windSpeed;
	private double temperature;
	private double minTemp;
	private double maxTemp;
	private int humidity;
	private String windDirection;
	private String skyCondition;
	private String userPreferences;
	public ADO(){};
/*
* Constructor for the abstract data object
*/
	public ADO (String user, int air, double wind, double temp, double min, double max,int humid, String windDir, String sky)
	{
		userPreferences = user;
		airPressure = air;
		windSpeed = wind;
		temperature = convertTemp(temp,userPreferences);
		minTemp = convertTemp(min, userPreferences);
		maxTemp = convertTemp(max, userPreferences);
		humidity = humid;
		windDirection = windDir;
		skyCondition = sky;
	}
/**
* Getter and setter methods for the air pressure
*/
	public int getAirPressure()
	{
		return airPressure;
	}
	public void setAirPressure(int pressure)
	{
		airPressure = pressure;
	}
/**
* Getter and seter methods for the wind speed
*/
	public double getWindSpeed()
	{
		return windSpeed;
	}
	public void setWindSpeed(double wind)
	{
		windSpeed = wind;
	}
/**
* Getter and setter methods for the temperature
*/
	public double getTemperature()
	{
		return temperature;
	}
	public void setTemperature (double temp)
	{
		temperature = convertTemp(temp,userPreferences);
	}
/**
* Getter and setter methods for the expected minimum temperature
*/
	public double getMinTemp()
	{
		return minTemp;
	}
	public void setMinTemp(double min)
	{
		minTemp = convertTemp(min,userPreferences);
	}
/**
* Getter and setter methods for the expected maximum temperature
*/
	public double getMaxTemp()
	{
		return maxTemp;
	}
	public void setMaxTemp(double max)
	{
		maxTemp = convertTemp(max,userPreferences);
	}
/**
* Getter and setter methods for the humidity
*/
	public int getHumidity()
	{
		return humidity;
	}
	public void setHumidity(int humid)
	{
		humidity = humid;
	}
/**
* Getter and setter methods for the wind direction
*/
	public String getWindDirection()
	{
		return windDirection;
	}
	public void setWindDirection(String direction)
	{
		windDirection = direction;
	}
/**
* Getter and setter methods for the sky condition
*/
	public String getSkyCondition()
	{
		return skyCondition;
	} 
	public void setSkyCondition(String condition)
	{
		skyCondition = condition;
	}

/**
* Getter and setter methods for user preferences
*/
	public String getPreferences()
	{
		return userPreferences;
	}
	public void setPreferences(String user)
	{
		userPreferences = user;
	}	
	
/**
* Converts the temperature value from kelvin to celsius, kelvin to fahrenheit or leaves it based on user preferences
*/

	private double convertTemp(double temperature, String userPreferences)
	{
		if (userPreferences.equals("fahrenheit"))
		{
			temperature = ((temperature -273.15)*1.8) + 32;
		}
		if (userPreferences.equals("celsius"))
		{
			temperature = temperature - 273.15;
		}
		return temperature;
	}
}
