package io.github.mariazevedo88.jfv.formatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.github.mariazevedo88.jfv.enumeration.DelimitersEnum;

/**
 * Class that verify a JSON and format in cases of invalid JSON
 * 
 * @author Mariana Azevedo
 * @since 10/02/2019
 *
 */
public class CustomJSONFormatter {
	
	private static final Logger logger = Logger.getLogger(CustomJSONFormatter.class.getName());
	private JsonObject validJson;
	
	/**
	 * Method that verify in a object is a valid or invalid JSON
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * 
	 * @param json
	 * @param muteLog
	 * @return
	 */
	private boolean isValidJson(Object json, boolean muteLog){
		
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
	private void parseJSONObject(Object json, boolean muteException) {
		
		JsonElement res = null;
		
		if(json instanceof String){
			try {
				res = new JsonParser().parse((String)json);
			}catch(JsonSyntaxException e) {
				if(!muteException) {
					throw new JsonSyntaxException("Error: JSON with more invalid characters than commas and quotes on keys and values.");
				}else {
					this.validJson = null;
				}
			}
		}
		
		if(json instanceof BufferedReader){
			res = new JsonParser().parse((BufferedReader)json);
		}
		
		if (res != null && res.isJsonObject()) {
			this.validJson = res.getAsJsonObject();
        }
	}
	
	/**
	 * Method to convert a invalid JSON, add double quotes where is needed. Based on the answers of this question:
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
	 * 
	 * @param invalidJson
	 * @return
	 */
	private static String getInvalidJsonToFormat(String invalidJson, boolean muteException) {
		
		invalidJson = fixMalformatedFields(invalidJson); //format malformated fields before apply the main regex
		invalidJson = fixEmptyFields(invalidJson); //format empty fields before apply the main regex
		
		invalidJson = invalidJson.replaceAll("(?<=\\{|, ?)([a-zA-Z]+?): ?(?![\\{\\[])(.+?)(?=,|})", "\"$1\": \"$2\"");
		invalidJson = fixFieldsWithSimpleQuotes(invalidJson);
		
		StringBuilder builderModified = new StringBuilder(invalidJson);
		
		builderModified = fixFieldsWithCommasWronglyModified(builderModified, muteException);
		invalidJson = replaceControlDelimiters(builderModified);
		
		return invalidJson;
	}

	/**
	 * Method to clean a string with single quotes
	 * 
	 * @author Mariana Azevedo
	 * @since 28/02/2019
	 * 
	 * @param invalidJson
	 * @return
	 */
	private static String fixFieldsWithSimpleQuotes(String invalidJson) {
		return invalidJson.replaceAll(DelimitersEnum.QUOTES.getValue(), DelimitersEnum.EMPTY_STRING.getValue());
	}
	
	/**
	 * Method to fix malformated values before apply the main regex
	 * 
	 * @author Mariana Azevedo
	 * @since 02/04/2019
	 * 
	 * @param invalidJson
	 * @return
	 */
	private static String fixMalformatedFields(String invalidJson) {
		
		invalidJson = invalidJson.replaceAll("\\s+,,", ","); //correcting double commas with space
		invalidJson = invalidJson.replaceAll("(\\d+)\\,(\\d+)", "$1.$2"); //correcting decimal numbers with comma
		invalidJson = invalidJson.replaceAll("(\\d+)\\:(\\d+)", "$1;;$2"); //correcting hours in the HH:mm format
		invalidJson = invalidJson.replaceAll("(\\()", ";"); //correcting left parentheses wrongly placed
		invalidJson = invalidJson.replaceAll("(\\))", ";"); //correcting right parentheses wrongly placed
		invalidJson = invalidJson.replaceAll("[A-Z]+:", ""); //removing colon wrongly placed
		
		return invalidJson;
	}

	/**
	 * Method to fix json keys with empty values before apply the main regex
	 * 
	 * @author Mariana Azevedo
	 * @since 28/02/2019
	 * 
	 * @param invalidJson
	 * @return
	 */
	private static String fixEmptyFields(String invalidJson) {
		
		invalidJson = invalidJson.replaceAll("(:,)", ":\'\',");
		invalidJson = invalidJson.replaceAll("(:})", ": \'\'}");
		invalidJson = invalidJson.replaceAll("(,,)", "\'\',");
		invalidJson = invalidJson.replaceAll("(,})", "}");
		
		return invalidJson;
	}

	/**
	 * Method that replaces some control delimiters in the fix routine on fields with wrong commas
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param builderModified
	 * @return
	 */
	private static String replaceControlDelimiters(StringBuilder builderModified) {
		
		String finalString = builderModified.toString().replaceAll(DelimitersEnum.DOUBLE_SEMICOLON.getValue(), DelimitersEnum.COLON.getValue());
		finalString = finalString.replaceAll(DelimitersEnum.SEMICOLON.getValue(), DelimitersEnum.COMMA.getValue());
		
		return finalString;
	}
	
	/**
	 * Method that fix invalid fields wrongly converted by the regex of getInvalidJsonToFormat() method
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param builderModified
	 * @return
	 */
	private static StringBuilder fixFieldsWithCommasWronglyModified(StringBuilder builderModified, boolean muteException){
		
		String [] invalidJsonValues = builderModified.toString().split(DelimitersEnum.COMMA.getValue());
		boolean hasInvalidValues = true;
		
		while(hasInvalidValues) {
			builderModified = cleanInvalidJsonValues(invalidJsonValues, builderModified, muteException);
			
			if(!muteException && builderModified.length() == 0) {
				throw new JsonParseException("Error: JSON with more invalid characters than commas and quotes on keys and values.");
			}

			invalidJsonValues = builderModified.toString().split(DelimitersEnum.COMMA.getValue());
			
			if(!isStringHasInvalidJsonValues(invalidJsonValues)) {
				hasInvalidValues = false;
			}
		}
		
		return builderModified;
	}
	
	/**
	 * Method that verifies with string still has a invalid values or keys.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param invalidJsonValues
	 * @return
	 */
	private static boolean isStringHasInvalidJsonValues(String [] invalidJsonValues) {
		Set<String> collection = Arrays.stream(invalidJsonValues).collect(Collectors.toSet());
		return collection.stream().anyMatch(str -> !str.contains(DelimitersEnum.COLON.getValue()));
	}
	
	/**
	 * Method that clean fields wrongly separated with commas and append these strings.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param invalidJsonValues
	 * @param builder
	 * @return
	 */
	private static StringBuilder cleanInvalidJsonValues(String[] invalidJsonValues, StringBuilder builder, boolean muteException) {
		
		StringBuilder builderModified = new StringBuilder(builder);
		String previousField = DelimitersEnum.EMPTY_STRING.getValue();
		
		List<String> collection = Arrays.stream(invalidJsonValues).collect(Collectors.toList());
		for(String str : collection) {
			if(str.contains(DelimitersEnum.COLON.getValue())) {
				previousField = str;
			}else{
				if(!str.isEmpty()) {
					cleanWrongQuotesOnFields(builderModified, previousField, str, muteException);
					break;
				}
			}
		}
		
		return builderModified;
	}

	/**
	 * Method that traverses a string array and identifies whether the string is a valid key and value set. 
	 * If it is not and is part of a whole word broken by commas, it applies treatment to clean and reassemble 
	 * the string by concatenating with the section previously treated.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param builderModified
	 * @param previousField
	 * @param str
	 */
	private static void cleanWrongQuotesOnFields(StringBuilder builderModified, String previousField, String str, boolean muteException) {
		
		StringBuilder sbReplace = new StringBuilder(previousField);
		int lastIndexOf = previousField.length();
		
		try {
			if(sbReplace.lastIndexOf(DelimitersEnum.RIGHT_DOUBLE_QUOTE_WITH_ESCAPE.getValue()) == lastIndexOf-1) {
				sbReplace = sbReplace.deleteCharAt(lastIndexOf-1);
				sbReplace.insert(lastIndexOf-1, DelimitersEnum.SEMICOLON.getValue());
			}
		}catch(StringIndexOutOfBoundsException exception){
			if(!muteException) {
				throw new StringIndexOutOfBoundsException("String is an empty object or has an invalid structure (key without value or vice-versa): " + str);
			}else {
				return;
			}
		}
		
		if(str.contains(DelimitersEnum.RIGHT_KEY.getValue())){
			//If the field that has commas in the middle, but is at the end of the object, 
			//treat so that the quotes are in the right place
			int lastIndexOfStr = str.length();
			String strModified = new StringBuilder(str).deleteCharAt(lastIndexOfStr-1).toString(); 
			sbReplace.append(strModified).append(DelimitersEnum.RIGHT_KEY_WITH_ESCAPE.getValue());
		}else{
			sbReplace.append(str).append(DelimitersEnum.RIGHT_DOUBLE_QUOTE_WITH_ESCAPE.getValue());
		}
		
		Pattern pattern = Pattern.compile(str, Pattern.LITERAL);
		replaceStringBasedOnAPattern(builderModified, pattern, DelimitersEnum.EMPTY_STRING.getValue());
		
		try {
			pattern = Pattern.compile(previousField);
			replaceStringBasedOnAPattern(builderModified, pattern, sbReplace.toString());
		}catch (PatternSyntaxException e){
			splitPatternToNearowTheSearch(builderModified, previousField, sbReplace);
		}
		
		pattern = Pattern.compile(DelimitersEnum.DOUBLE_COMMA.getValue());
		replaceStringBasedOnAPattern(builderModified, pattern, DelimitersEnum.COMMA.getValue());
	}

	/**
	 * Method to cut a string to approximate the search and avoid cases of PatternSyntaxException
	 * 
	 * @author Mariana Azevedo
	 * @since 01/03/2019
	 * 
	 * @param builderModified
	 * @param previousField
	 * @param sbReplace
	 */
	private static void splitPatternToNearowTheSearch(StringBuilder builderModified, String previousField,
			StringBuilder sbReplace) {
		
		String[] patternSplit = previousField.split(DelimitersEnum.COLON.getValue());
		String[] sbReplaceToSplit = sbReplace.toString().split(DelimitersEnum.COLON.getValue());
		
		Pattern pattern = Pattern.compile(patternSplit[patternSplit.length-1]);
		replaceStringBasedOnAPattern(builderModified, pattern, sbReplaceToSplit[sbReplaceToSplit.length-1]);
	}
	

	/**
	 * Method that replaces a string based on a pattern.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param builderModified
	 * @param pattern
	 * @param replacement
	 */
	private static void replaceStringBasedOnAPattern(StringBuilder builderModified, Pattern pattern, String replacement) {
		
		Matcher matcher = pattern.matcher(builderModified);
		int start = 0;
		while (matcher.find(start)) {
			builderModified.replace(matcher.start(), matcher.end(), replacement);
		}
	}
	
	/**
	 * Method that checks JSON validity and format if needed.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param json
	 * @param muteLog
	 * @return
	 * @throws IOException
	 */
	public JsonObject checkValidityAndFormatObject(Object json, boolean muteLog, boolean muteException) throws IOException {
		
		String jsonToTest = null;
		BufferedReader reader = null;
		
		if(json instanceof BufferedReader){
			reader = (BufferedReader) json;
		}
		
		if(json == null) {
			if(!muteException) {
				throw new NullPointerException("Object to validated is null.");
			}else {
				return validJson;
			}
		}
		
		if(!isValidJson(json, muteLog)) {
			jsonToTest = getInvalidJsonToFormat(json.toString(), muteException);
			if(reader == null){
				parseJSONObject(jsonToTest, muteException);
			}else{
				parseJSONObject(reader, muteException);
				reader.close();
			}
		}
		
		if(!muteLog && this.validJson != null) {
			logger.info("Valid json: " + this.validJson);
		}else {
			if(!muteLog) 
				logger.warn("JsonParseException: JSON with more invalid characters than commas and quotes on keys and values.");
		}
		
		return validJson;
	}
	
	/**
	 * Method that remove a json object/json array pattern from the string
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param invalidJson
	 * @param jsonObjectPattern
	 * @return
	 */
	private String removeJSONObjectFromString(String invalidJson, String jsonObjectPattern) {
		
		StringBuilder builderModified = new StringBuilder(invalidJson);
		int lastIndexOf = builderModified.indexOf(jsonObjectPattern) + jsonObjectPattern.length();
		
		int numberLeftKeys = 0;
		int numberRightKeys = 0;
		int numberLeftBrackets = 0;
		int numberRightBrackets = 0;
		
		for (int i = lastIndexOf; i < builderModified.length(); i++) {
			String next = builderModified.substring(i,i+1);
			if(next.equals(DelimitersEnum.LEFT_KEY.getValue())) numberLeftKeys++;
			
			if(next.equals(DelimitersEnum.RIGHT_KEY.getValue())) numberRightKeys++;
			
			if(next.equals(DelimitersEnum.LEFT_BRACKETS.getValue())) numberLeftBrackets++;
			
			if(next.equals(DelimitersEnum.RIGHT_BRACKETS.getValue())) numberRightBrackets++;
			
			if((next.equals(DelimitersEnum.COMMA.getValue()) && hasEqualNumberOfKeysOrBrackets(numberLeftKeys, numberRightKeys) && hasEqualNumberOfKeysOrBrackets(numberLeftBrackets, numberRightBrackets))
					|| (next.equals(DelimitersEnum.RIGHT_KEY.getValue()) && hasMoreRightKeys(numberLeftKeys, numberRightKeys))) {
				if (next.equals(DelimitersEnum.COMMA.getValue())) jsonObjectPattern = jsonObjectPattern.concat(next);
				break;
			}
			
			jsonObjectPattern = jsonObjectPattern.concat(next);
			
		}

		return builderModified.toString().replace(jsonObjectPattern, "");
	}
	
	/**
	 * Method that filter a json object/json array pattern from the string
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param invalidJson
	 * @param jsonObjectPattern
	 * @return
	 */
	private String filterJSONObjectFromString(String invalidJson, String jsonObjectPattern) {
		
		StringBuilder builderModified = new StringBuilder(invalidJson);
		int lastIndexOf = builderModified.indexOf(jsonObjectPattern) + jsonObjectPattern.length();
		
		int numberLeftKeys = 0;
		int numberRightKeys = 0;
		int numberLeftBrackets = 0;
		int numberRightBrackets = 0;
		
		for (int i = lastIndexOf; i < builderModified.length(); i++) {
			
			String next = builderModified.substring(i,i+1);

			if(next.equals(DelimitersEnum.LEFT_KEY.getValue())) numberLeftKeys++;
			
			if(next.equals(DelimitersEnum.RIGHT_KEY.getValue())) numberRightKeys++;
			
			if(next.equals(DelimitersEnum.LEFT_BRACKETS.getValue())) numberLeftBrackets++;
			
			if(next.equals(DelimitersEnum.RIGHT_BRACKETS.getValue())) numberRightBrackets++;
			
			if((next.equals(DelimitersEnum.COMMA.getValue()) && hasEqualNumberOfKeysOrBrackets(numberLeftKeys, numberRightKeys) && hasEqualNumberOfKeysOrBrackets(numberLeftBrackets, numberRightBrackets))
					|| (next.equals(DelimitersEnum.RIGHT_KEY.getValue()) && hasMoreRightKeys(numberLeftKeys, numberRightKeys))) {
				if (next.equals(DelimitersEnum.COMMA.getValue())) jsonObjectPattern = jsonObjectPattern.concat(next);
				break;
			}
			
			jsonObjectPattern = jsonObjectPattern.concat(next);
			
		}

		return new StringBuilder(jsonObjectPattern).toString();
	}
	
	/**
	 * Method that remove a list of json object/json array patterns from the string
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param invalidJson
	 * @param jsonObjectPattern
	 * @return
	 */
	public String removeJSONObjectsFromString(String invalidJson, String[] jsonObjectPattern) {
		
		String jsonModified = invalidJson;
		
		for(String attribute : jsonObjectPattern) {
			jsonModified = removeJSONObjectFromString(jsonModified, attribute);
		}
		
		return jsonModified;
	}
	
	/**
	 * Method that filter a list of json object/json array pattern from the string
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param invalidJson
	 * @param jsonObjectPattern
	 * @return
	 */
	public String filterJSONObjectsFromString(String invalidJson, String[] jsonObjectPattern) {
		
		String jsonModified = "";
		
		for(String attribute : jsonObjectPattern) {
			String filterResult = filterJSONObjectFromString(invalidJson, attribute);
			jsonModified = jsonModified.concat(filterResult).concat(DelimitersEnum.COMMA.getValue());
		}
		
		if(!jsonModified.startsWith(DelimitersEnum.LEFT_KEY.getValue())) jsonModified = DelimitersEnum.LEFT_KEY.getValue().concat(jsonModified);
		if(!jsonModified.endsWith(DelimitersEnum.RIGHT_KEY.getValue())) jsonModified = jsonModified.concat(DelimitersEnum.RIGHT_KEY.getValue());
		
		return jsonModified;
	}

	/**
	 * Method that verifies with string has more right keys
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param numberLeftKeys
	 * @param numberRightKeys
	 * @return
	 */
	private boolean hasMoreRightKeys(int numberLeftKeys, int numberRightKeys) {
		return numberLeftKeys < numberRightKeys;
	}

	/**
	 * Method that verifies with string has an equal number of left and right keys
	 * or left and right brackets
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param numberLeft
	 * @param numberRight
	 * @return
	 */
	private boolean hasEqualNumberOfKeysOrBrackets(int numberLeft, int numberRight) {
		return numberLeft == numberRight;
	}
	
	/**
	 * Method that return a valid JSON
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * 
	 * @return
	 */
	public JsonObject getValidJson() {
		return validJson;
	}

}
