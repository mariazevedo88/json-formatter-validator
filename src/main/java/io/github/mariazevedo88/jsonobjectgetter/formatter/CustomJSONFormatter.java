package io.github.mariazevedo88.jsonobjectgetter.formatter;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CustomJSONFormatter {
	
	private static final Logger logger = Logger.getLogger(CustomJSONFormatter.class.getName());
	private JSONObject validJson;
	
	private boolean isValidJson(Object json){
		
		if(json instanceof JSONObject || json instanceof JSONArray) {
			logger.info("Valid json: " + json);
			return true;
		}
			
	    logger.info("Invalid json: " + json);
        return false;
	}
	
	private void parseJSONObject(Object json) throws ParseException {
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json.toString());
		validJson = (JSONObject) obj;
		
	}
	
	public JSONObject getValidJson() {
		return validJson;
	}

	public static String getInvalidJsonToFormat(String invalidJson) {
		return invalidJson.replaceAll(
	            "(?<=\\{|, ?)([a-zA-Z]+?): ?(?![ \\{\\[])(.+?)(?=,|})", "\"$1\": \"$2\"");
	}
	
	public JSONObject checkValidityAndFormatObject(Object json) throws ParseException {
		
		String jsonToTest = json.toString();
		
		if(!isValidJson(json)) {
			jsonToTest = getInvalidJsonToFormat(json.toString());
		}
		
		parseJSONObject(jsonToTest);
		
		return validJson;
	}

}
