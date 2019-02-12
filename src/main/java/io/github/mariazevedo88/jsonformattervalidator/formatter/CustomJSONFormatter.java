package io.github.mariazevedo88.jsonformattervalidator.formatter;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class that verify a json and format in cases of invalid json
 * 
 * @author Mariana Azevedo
 * @since 10/02/2019
 *
 */
public class CustomJSONFormatter {
	
	private static final Logger logger = Logger.getLogger(CustomJSONFormatter.class.getName());
	private JsonObject validJson;
	
	/**
	 * Method that verify in a object is a valid or invalid json
	 * @param json
	 * @return
	 */
	private boolean isValidJson(Object json){
		
		if(json instanceof BufferedReader){
			JsonElement res = new JsonParser().parse((BufferedReader)json);
			if(res != null && res.isJsonObject()){
				this.validJson = (JsonObject) res;
				return true;
			}
		}
		
		if(json instanceof JsonObject || json instanceof JsonArray) {
			this.validJson = (JsonObject) json;
			return true;
		}
			
	    logger.info("Invalid json: " + json.toString());
        return false;
	}
	
	/**
	 * Method that parses a json object
	 * @param json
	 */
	private void parseJSONObject(Object json) {
		
		JsonElement res = null;
		
		if(json instanceof String){
			res = new JsonParser().parse((String)json);
		}
		
		if(json instanceof BufferedReader){
			res = new JsonParser().parse((BufferedReader)json);
		}
		
		if (res != null && res.isJsonObject()) {
			this.validJson = res.getAsJsonObject();
        }
	}
	
	public JsonObject getValidJson() {
		return validJson;
	}

	/**
	 * Method to convert a invalid json, add double quotes where is needed. Based on the answers of this question:
	 * https://stackoverflow.com/questions/54584696/how-add-quotes-in-a-json-string-using-java-when-the-value-is-a-date
	 * 
	 * (?<=: ?): there’s a colon an optionally a blank before the value (lookbehind)
     * (?![ \\{\\[]) the value does not start with a blank, curly brace or square bracket (negative lookahead; 
     *  blank because we don’t want a blank between the colon and the value to be taken as part of the value)
     * (.+?): the value consists of at least one character, as few as possible (reluctant quantifier; or regex would try to take the rest of the string)
     * (?=,|}): after the value comes either a comma or a right curly brace (positive lookahead).
     * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * @param invalidJson
	 * @return
	 */
	public static String getInvalidJsonToFormat(String invalidJson) {
		invalidJson = invalidJson.replaceAll("(?<=\\{|, ?)([a-zA-Z]+?): ?(?![ \\{\\[])(.+?)(?=,|})", "\"$1\": \"$2\"");
		StringBuilder builderModified = new StringBuilder(invalidJson);
		return builderModified.toString();
	}
	
	/**
	 * Method that checks json validity and format if needed
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public JsonObject checkValidityAndFormatObject(Object json) throws IOException {
		
		String jsonToTest = null;
		BufferedReader reader = null;
		
		if(json instanceof BufferedReader){
			reader = (BufferedReader) json;
		}
		
		if(!isValidJson(json)) {
			jsonToTest = getInvalidJsonToFormat(json.toString());
			if(reader == null){
				parseJSONObject(jsonToTest);
			}else{
				parseJSONObject(reader);
				reader.close();
			}
		}
		
		logger.info("Valid json: " + this.validJson);
		
		return validJson;
	}

}
