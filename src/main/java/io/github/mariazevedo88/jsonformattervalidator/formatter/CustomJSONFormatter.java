package io.github.mariazevedo88.jsonformattervalidator.formatter;

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
	 * Method that verify in a object is a valid or invalid json.
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
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
	 * Method that parses a json object.
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
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
	private static String getInvalidJsonToFormat(String invalidJson) {
		invalidJson = fixEmptyFields(invalidJson); //format empty fields before apply the main regex
		invalidJson = invalidJson.replaceAll("(?<=\\{|, ?)([a-zA-Z]+?): ?(?![\\{\\[])(.+?)(?=,|})", "\"$1\": \"$2\"");
		invalidJson = fixFieldsWithSimpleQuotes(invalidJson);
		
		StringBuilder builderModified = new StringBuilder(invalidJson);
		
		builderModified = fixFieldsWithCommasWronglyModified(builderModified);
		invalidJson = replaceControlDelimiters(builderModified);
		
		return invalidJson;
	}

	/**
	 * Method to clean a string with single quotes
	 * 
	 * @author Mariana Azevedo
	 * @since 28/02/2019
	 * @param invalidJson
	 * @return
	 */
	private static String fixFieldsWithSimpleQuotes(String invalidJson) {
		return invalidJson.replaceAll("''", "");
	}

	/**
	 * Method to fix json keys with empty values before apply the main regex
	 * 
	 * @author Mariana Azevedo
	 * @since 28/02/2019
	 * @param invalidJson
	 * @return
	 */
	private static String fixEmptyFields(String invalidJson) {
		invalidJson = invalidJson.replaceAll("(:,)", ":\'\',");
		invalidJson = invalidJson.replaceAll("(:})", ": \'\'}");
		invalidJson = invalidJson.replaceAll("(,,)", ":\'\',");
		return invalidJson;
	}

	/**
	 * Method that replaces some control delimiters in the fix routine on fields with wrong commas.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * @param builderModified
	 * @return
	 */
	private static String replaceControlDelimiters(StringBuilder builderModified) {
		return builderModified.toString().replaceAll(";", ",");
	}

	/**
	 * Method that fix invalid fields wrongly converted by the regex of getInvalidJsonToFormat() method.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * @param builderModified
	 * @return
	 */
	private static StringBuilder fixFieldsWithCommasWronglyModified(StringBuilder builderModified) {
		
		String [] invalidJsonValues = builderModified.toString().split(",");
		boolean hasInvalidValues = true;
		
		while(hasInvalidValues) {
			builderModified = cleanInvalidJsonValues(invalidJsonValues, builderModified);
			invalidJsonValues = builderModified.toString().split(",");
			
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
	 * @param invalidJsonValues
	 * @return
	 */
	private static boolean isStringHasInvalidJsonValues(String [] invalidJsonValues) {
		Set<String> collection = Arrays.stream(invalidJsonValues).collect(Collectors.toSet());
		return collection.stream().anyMatch(str -> !str.contains(":"));
	}
	
	/**
	 * Method that clean fields wrongly separated with commas and append these strings.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * @param invalidJsonValues
	 * @param builder
	 * @return
	 */
	private static StringBuilder cleanInvalidJsonValues(String[] invalidJsonValues, StringBuilder builder) {
		
		StringBuilder builderModified = new StringBuilder(builder);
		String previousField = "";
		
		List<String> collection = Arrays.stream(invalidJsonValues).collect(Collectors.toList());
		for(String str : collection) {
			if(str.contains(":")) {
				previousField = str;
			}else{
				if(!str.isEmpty()) {
					cleanWrongQuotesOnFields(builderModified, previousField, str);
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
	 * @param builderModified
	 * @param previousField
	 * @param str
	 */
	private static void cleanWrongQuotesOnFields(StringBuilder builderModified, String previousField, String str) {
		
		StringBuilder sbReplace = new StringBuilder(previousField);
		int lastIndexOf = previousField.length();
		
		if(sbReplace.lastIndexOf("\"") == lastIndexOf-1) {
			sbReplace = sbReplace.deleteCharAt(lastIndexOf-1);
			sbReplace.insert(lastIndexOf-1, ";");
		}
		
		if(str.contains("}")){
			//If the field that has commas in the middle, but is at the end of the object, 
			//treat so that the quotes are in the right place
			int lastIndexOfStr = str.length();
			String strModified = new StringBuilder(str).deleteCharAt(lastIndexOfStr-1).toString(); 
			sbReplace.append(strModified).append("\"}");
		}else{
			sbReplace.append(str).append("\"");
		}
		
		Pattern pattern = Pattern.compile(str);
		replaceStringBasedOnAPattern(builderModified, pattern, "");
		
		try {
			pattern = Pattern.compile(previousField);
			replaceStringBasedOnAPattern(builderModified, pattern, sbReplace.toString());
		}catch (PatternSyntaxException e){
			splitPatternToNearowTheSearch(builderModified, previousField, sbReplace);
		}
		
		pattern = Pattern.compile(",,");
		replaceStringBasedOnAPattern(builderModified, pattern, ",");
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
		
		String[] patternSplit = previousField.split(":");
		String[] sbReplaceToSplit = sbReplace.toString().split(":");
		
		Pattern pattern = Pattern.compile(patternSplit[patternSplit.length-1]);
		replaceStringBasedOnAPattern(builderModified, pattern, sbReplaceToSplit[sbReplaceToSplit.length-1]);
	}
	

	/**
	 * Method that replaces a string based on a pattern.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
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
	 * Method that checks json validity and format if needed.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
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
	
	/**
	 * Method that return a valid json
	 * 
	 * @author Mariana Azevedo
	 * @since 10/02/2019
	 * @return
	 */
	public JsonObject getValidJson() {
		return validJson;
	}

}
