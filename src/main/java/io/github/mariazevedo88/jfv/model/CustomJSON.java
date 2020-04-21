package io.github.mariazevedo88.jfv.model;

import java.io.BufferedReader;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Class that represents a CustomJSON
 * 
 * @author Mariana Azevedo
 * @since 17/08/2019
 */
public class CustomJSON {
	
	private static final Logger logger = Logger.getLogger(CustomJSON.class.getName());
	private JsonElement validJson;
	
	/**
	 * Method that return a validJSON element
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * 
	 * @return validJson
	 */
	public JsonElement getValidJson() {
		return validJson;
	}

	/**
	 * Method that set value on validJSON
	 * 
	 * @author Mariana Azevedo
	 * @since 17/08/2019
	 * 
	 * @param validJson
	 */
	public void setValidJson(JsonElement validJson) {
		this.validJson = validJson;
	}

	/**
	 * Method that verify in a object is a valid or invalid JSON
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * 
	 * @param json
	 * @param muteLog
	 * 
	 * @return boolean
	 */
	public boolean isValidJson(Object json, boolean muteLog){
		
		if(json instanceof BufferedReader){
			JsonElement res = JsonParser.parseReader((BufferedReader)json);
			this.validJson = res;
			return true;
		}
		
		if(json instanceof JsonObject) {
			this.validJson = (JsonObject) json;
			return true;
		}
		
		if(json instanceof JsonArray) {
			this.validJson = (JsonArray) json;
			return true;
		}
			
		if(!muteLog) {
			logger.info("Invalid json: " + json.toString());
		}
		
        return false;
	}
	
	/**
	 * Method that parses a JSON object
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * 
	 * @param json
	 * @param muteException
	 */
	public void parseJSONObject(Object json, boolean muteException) {
		
		JsonElement res = null;
		
		if(json instanceof String){
			try {
				res = JsonParser.parseString((String)json);
			}catch(JsonSyntaxException e) {
				if(!muteException) {
					throw new JsonSyntaxException("Error: JSON with more invalid characters than commas and quotes on keys and values.");
				}else {
					this.validJson = null;
				}
			}
		}
		
		if(json instanceof BufferedReader){
			res = JsonParser.parseReader((BufferedReader)json);
		}
		
		if (res != null) {
			if(res.isJsonObject()) this.validJson = res.getAsJsonObject();
			if(res.isJsonArray()) this.validJson = res.getAsJsonArray();
        }
	}

}
