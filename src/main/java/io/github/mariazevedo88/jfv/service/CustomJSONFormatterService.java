package io.github.mariazevedo88.jfv.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.github.mariazevedo88.jfv.enumeration.DelimitersEnum;
import io.github.mariazevedo88.jfv.model.CustomJSON;

/**
 * Class that verify a JSON and format in cases of invalid JSON
 * 
 * @author Mariana Azevedo
 * @since 10/02/2019
 *
 */
public class CustomJSONFormatterService {
	
	private static final Logger logger = Logger.getLogger(CustomJSONFormatterService.class.getName());
	private CustomJSON customJson;
	
	public CustomJSONFormatterService() {
		customJson = new CustomJSON();
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
	 * @param muteException
	 * 
	 * @return String
	 */
	private static String getInvalidJsonToFormat(String invalidJson, boolean muteException) {
		
		invalidJson = fixMalformedFields(invalidJson); //format malformated fields before apply the main regex
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
	 * @return String
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
	 * @return String
	 */
	private static String fixMalformedFields(String invalidJson) {
		
		invalidJson = invalidJson.replaceAll("\\s+,,", ","); //correcting double commas with space
		invalidJson = invalidJson.replaceAll("(\\d+)\\,(\\d+)", "$1.$2"); //correcting decimal numbers with comma
		invalidJson = invalidJson.replaceAll("(\\d+)\\:(\\d+)\\:(\\d+)", "$1;;$2;;$3"); //correcting hours in the HH:mm:SS format
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
	 * @return String
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
	 * @return String
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
	 * @param muteException
	 * @return StringBuilder
	 */
	private static StringBuilder fixFieldsWithCommasWronglyModified(StringBuilder builderModified, boolean muteException){
		
		String [] invalidJsonValues = builderModified.toString().split(DelimitersEnum.COMMA.getValue());
		boolean hasInvalidValues = true;
		
		while(hasInvalidValues) {
			builderModified = cleanInvalidJsonValues(invalidJsonValues, builderModified, muteException);
			
			if(builderModified.length() == 0) {
				if(muteException) {
					hasInvalidValues = false;
				}else {
					throw new JsonParseException("Error: JSON with more invalid characters than commas and quotes on keys and values.");
				}
			}else {
				invalidJsonValues = builderModified.toString().split(DelimitersEnum.COMMA.getValue());
				if(!CustomJSONValidatorFiltersService.isStringHasInvalidJsonValues(invalidJsonValues)) {
					hasInvalidValues = false;
				}
			}
		}
		
		return builderModified;
	}
	
	/**
	 * Method that clean fields wrongly separated with commas and append these strings.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param invalidJsonValues
	 * @param builder
	 * @param muteException
	 * @return StringBuilder
	 */
	private static StringBuilder cleanInvalidJsonValues(String[] invalidJsonValues, StringBuilder builder, boolean muteException) {
		
		StringBuilder builderModified = new StringBuilder(builder);
		String previousField = DelimitersEnum.EMPTY_STRING.getValue();
		boolean isClean = false;
		
		List<String> collection = Arrays.stream(invalidJsonValues).collect(Collectors.toList());
		for(String stringAnalyzed : collection) {
			if(stringAnalyzed.contains(DelimitersEnum.COLON.getValue())) {
				previousField = stringAnalyzed;
			}else{
				if(!stringAnalyzed.isEmpty()) {
					isClean = cleanWrongQuotesOnFields(builderModified, previousField, stringAnalyzed, muteException);
					if(isClean) builderModified = new StringBuilder(DelimitersEnum.EMPTY_STRING.getValue());
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
	 * @param stringToBeAnalyzed
	 * @param muteException
	 * 
	 * @return boolean
	 */
	private static boolean cleanWrongQuotesOnFields(StringBuilder builderModified, String previousField, String stringToBeAnalyzed, 
			boolean muteException) {
		
		StringBuilder stringBuilderToBeReplaced = new StringBuilder(previousField);
		int lastIndexOf = previousField.length();
		boolean isClean = false;
		
		try {
			if(stringBuilderToBeReplaced.lastIndexOf(DelimitersEnum.RIGHT_DOUBLE_QUOTE_WITH_ESCAPE.getValue()) == lastIndexOf-1) {
				stringBuilderToBeReplaced = stringBuilderToBeReplaced.deleteCharAt(lastIndexOf-1);
				stringBuilderToBeReplaced.insert(lastIndexOf-1, DelimitersEnum.SEMICOLON.getValue());
			}
		}catch(StringIndexOutOfBoundsException exception){
			if(!muteException) {
				throw new StringIndexOutOfBoundsException("String is an empty object or has an invalid structure (key without value or vice-versa): " + stringToBeAnalyzed);
			}else {
				isClean = true;
				logger.error("String is an empty object or has an invalid structure (key without value or vice-versa): " + stringToBeAnalyzed);
			}
		}
		
		if(!isClean) {
			if(stringToBeAnalyzed.contains(DelimitersEnum.RIGHT_KEY.getValue())){
				//If the field that has commas in the middle, but is at the end of the object, 
				//treat so that the quotes are in the right place
				int lastIndexOfString = stringToBeAnalyzed.length();
				String stringModified = new StringBuilder(stringToBeAnalyzed).deleteCharAt(lastIndexOfString-1).toString(); 
				stringBuilderToBeReplaced.append(stringModified).append(DelimitersEnum.RIGHT_KEY_WITH_ESCAPE.getValue());
			}else{
				stringBuilderToBeReplaced.append(stringToBeAnalyzed).append(DelimitersEnum.RIGHT_DOUBLE_QUOTE_WITH_ESCAPE.getValue());
			}
			
			Pattern pattern = Pattern.compile(stringToBeAnalyzed, Pattern.LITERAL);
			replaceStringBasedOnAPattern(builderModified, pattern, DelimitersEnum.EMPTY_STRING.getValue());
			
			try {
				pattern = Pattern.compile(previousField);
				replaceStringBasedOnAPattern(builderModified, pattern, stringBuilderToBeReplaced.toString());
			}catch (PatternSyntaxException e){
				splitPatternToNearowTheSearch(builderModified, previousField, stringBuilderToBeReplaced);
			}
			
			pattern = Pattern.compile(DelimitersEnum.DOUBLE_COMMA.getValue());
			replaceStringBasedOnAPattern(builderModified, pattern, DelimitersEnum.COMMA.getValue());
		}
		
		return isClean;
	}

	/**
	 * Method to cut a string to approximate the search and avoid cases of PatternSyntaxException
	 * 
	 * @author Mariana Azevedo
	 * @since 01/03/2019
	 * 
	 * @param builderModified
	 * @param previousField
	 * @param stringBuilderToBeReplaced
	 */
	private static void splitPatternToNearowTheSearch(StringBuilder builderModified, String previousField,
			StringBuilder stringBuilderToBeReplaced) {
		
		String[] patternSplit = previousField.split(DelimitersEnum.COLON.getValue());
		String[] stringBuilderReplaceToSplit = stringBuilderToBeReplaced.toString().split(DelimitersEnum.COLON.getValue());
		
		Pattern pattern = Pattern.compile(patternSplit[patternSplit.length-1]);
		replaceStringBasedOnAPattern(builderModified, pattern, stringBuilderReplaceToSplit[stringBuilderReplaceToSplit.length-1]);
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
	 * @param muteException
	 * 
	 * @return JsonElement
	 * 
	 * @throws IOException
	 */
	public JsonElement checkValidityAndFormatObject(Object json, boolean muteLog, boolean muteException) throws IOException {
		
		String jsonToTest = null;
		BufferedReader reader = null;
		
		if(json instanceof BufferedReader){
			reader = (BufferedReader) json;
			json = reader.readLine();
		}
		
		if(json == null) {
			if(!muteException) {
				throw new NullPointerException("Object to validated is null.");
			}else {
				this.customJson.setValidJson(null);
				return customJson.getValidJson();
			}
		}
		
		if(!customJson.isValidJson(json, muteLog)) {
			
			jsonToTest = getInvalidJsonToFormat(json.toString(), muteException);
			customJson.parseJSONObject(jsonToTest, muteException);
			
			if(reader != null){
				reader.close();
			}
		}
		
		if(this.customJson.getValidJson() != null) {
			if(!muteLog) logger.info("Valid json: " + this.customJson);
		}else {
			if(!muteLog) 
				logger.warn("JsonParseException: JSON with more invalid characters than commas and quotes on keys and values.");
		}
		
		return customJson.getValidJson();
	}
	
	/**
	 * Method that return a customJson object
	 * 
	 * @author Mariana Azevedo
	 * @since 18/08/2019
	 * 
	 * @return customJson
	 */
	public CustomJSON getCustomJson() {
		return customJson;
	}
}
