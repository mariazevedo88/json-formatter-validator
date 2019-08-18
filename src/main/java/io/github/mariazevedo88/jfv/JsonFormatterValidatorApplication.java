package io.github.mariazevedo88.jfv;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;

import io.github.mariazevedo88.jfv.service.CustomJSONFormatter;

/**
 * Application's main class 
 * 
 * @author Mariana Azevedo
 * @since 10/02/2019
 *
 */
public class JsonFormatterValidatorApplication {
	
	private static final Logger logger = Logger.getLogger(JsonFormatterValidatorApplication.class.getName());
	
	private static JsonElement json;

	/**
	 * Method that executes the formattter/validator application
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		
		logger.info("Started Json Formatter Validator Application");
		
		CustomJSONFormatter formatter = new CustomJSONFormatter();
		
		for(String arg : args) {
			json = formatter.checkValidityAndFormatObject(arg, false, false);
		}
	}

	/**
	 * Method that returns a JsonObject
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * 
	 * @return JsonObject
	 */
	public static JsonElement getJson() {
		return json;
	}

}

