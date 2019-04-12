package io.github.mariazevedo88.jfv;

import java.io.IOException;

import org.apache.log4j.Logger;
import com.google.gson.JsonObject;

import io.github.mariazevedo88.jfv.formatter.CustomJSONFormatter;

/**
 * Application's main class 
 * 
 * @author Mariana Azevedo
 * @since 10/02/2019
 *
 */
public class JsonFormatterValidatorApplication {
	
	private static final Logger logger = Logger.getLogger(JsonFormatterValidatorApplication.class.getName());
	
	private static JsonObject json;

	public static void main(String[] args) throws IOException{
		
		logger.info("Started Json Formatter Validator Aplication");
		
		CustomJSONFormatter formatter = new CustomJSONFormatter();
		
		for(String arg : args) {
			json = formatter.checkValidityAndFormatObject(arg, false, false);
		}
	}

	public static JsonObject getJson() {
		return json;
	}

}

