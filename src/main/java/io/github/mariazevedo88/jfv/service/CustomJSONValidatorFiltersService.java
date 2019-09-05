package io.github.mariazevedo88.jfv.service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.mariazevedo88.jfv.enumeration.DelimitersEnum;

/**
 * Class with method that verify, validates and filters a JSON
 * 
 * @author Mariana Azevedo
 * @since 18/08/2019
 */
public class CustomJSONValidatorFiltersService {
	
	/**
	 * Method that verifies with string still has a invalid values or keys.
	 * 
	 * @author Mariana Azevedo
	 * @since 17/02/2019
	 * 
	 * @param invalidJsonValues
	 * @return boolean
	 */
	public static boolean isStringHasInvalidJsonValues(String [] invalidJsonValues) {
		Set<String> collection = Arrays.stream(invalidJsonValues).collect(Collectors.toSet());
		return collection.stream().anyMatch(stringAnalyzed -> !stringAnalyzed.contains(DelimitersEnum.COLON.getValue()));
	}
	
	/**
	 * Method that remove a json object/json array pattern from the string
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param invalidJson
	 * @param jsonObjectPattern
	 * @return String
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
			
			numberLeftKeys = checkNumberOfLeftKeys(numberLeftKeys, next);
			numberRightKeys = checkIfExpressionisARightKey(numberRightKeys, next);
			numberLeftBrackets = checkIfExpressionIsALeftBracket(numberLeftBrackets, next);
			numberRightBrackets = checkIfExpressionIsARightBracket(numberRightBrackets, next);
			
			if(checkIfJsonHasEqualNumberExpressionsBeforeComma(numberLeftKeys, numberRightKeys, numberLeftBrackets, 
					numberRightBrackets, next) || checkIfLastKeyIsARightKey(numberLeftKeys, numberRightKeys, next)) {
				
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
	 * @return String
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

			numberLeftKeys = checkNumberOfLeftKeys(numberLeftKeys, next);
			numberRightKeys = checkIfExpressionisARightKey(numberRightKeys, next);
			numberLeftBrackets = checkIfExpressionIsALeftBracket(numberLeftBrackets, next);
			numberRightBrackets = checkIfExpressionIsARightBracket(numberRightBrackets, next);
			
			if(checkIfJsonHasEqualNumberExpressionsBeforeComma(numberLeftKeys, numberRightKeys, numberLeftBrackets,
					numberRightBrackets, next) || checkIfLastKeyIsARightKey(numberLeftKeys, numberRightKeys, next)) {
				
				if (next.equals(DelimitersEnum.COMMA.getValue())) jsonObjectPattern = jsonObjectPattern.concat(next);
				break;
			}
			
			jsonObjectPattern = jsonObjectPattern.concat(next);
			
		}

		return new StringBuilder(jsonObjectPattern).toString();
	}
	
	/**
	 * Method that checks if the last character of json is a right key and if the 
	 * expression has more right keys than left keys.
	 * 
	 * @author Mariana Azevedo
	 * @since 27/05/2019
	 *  
	 * @param numberLeftKeys
	 * @param numberRightKeys
	 * @param next
	 * @return boolean
	 */
	private boolean checkIfLastKeyIsARightKey(int numberLeftKeys, int numberRightKeys, String next) {
		return next.equals(DelimitersEnum.RIGHT_KEY.getValue()) && hasMoreRightKeys(numberLeftKeys, numberRightKeys);
	}

	/**
	 * Method that checks if the json has a equal number of right keys and left keys or left brackets and right brackets,
	 * before a comma. If the test is true, it means that the analysis has reached the end of the original string.
	 * 
	 * @author Mariana Azevedo
	 * @since 27/05/2019
	 * 
	 * @param numberLeftKeys
	 * @param numberRightKeys
	 * @param numberLeftBrackets
	 * @param numberRightBrackets
	 * @param next
	 * @return boolean
	 */
	private boolean checkIfJsonHasEqualNumberExpressionsBeforeComma(int numberLeftKeys, int numberRightKeys, int numberLeftBrackets, 
			int numberRightBrackets, String next) {
		
		return next.equals(DelimitersEnum.COMMA.getValue()) && hasEqualNumberOfKeysOrBrackets(numberLeftKeys, numberRightKeys) 
				&& hasEqualNumberOfKeysOrBrackets(numberLeftBrackets, numberRightBrackets);
	}

	/**
	 * Method that checks if the expression to be read is a right bracket.
	 * 
	 * @author Mariana Azevedo
	 * @since 27/05/2019
	 * 
	 * @param numberRightBrackets
	 * @param next
	 * @return int
	 */
	private int checkIfExpressionIsARightBracket(int numberRightBrackets, String next) {
		if(next.equals(DelimitersEnum.RIGHT_BRACKETS.getValue())) numberRightBrackets++;
		return numberRightBrackets;
	}

	/**
	 * Method that checks if the expression to be read is a left bracket.
	 * 
	 * @author Mariana Azevedo
	 * @since 27/05/2019
	 * 
	 * @param numberLeftBrackets
	 * @param next
	 * @return int
	 */
	private int checkIfExpressionIsALeftBracket(int numberLeftBrackets, String next) {
		if(next.equals(DelimitersEnum.LEFT_BRACKETS.getValue())) numberLeftBrackets++;
		return numberLeftBrackets;
	}

	/**
	 * Method that checks if the expression to be read is a right key.
	 * 
	 * @author Mariana Azevedo
	 * @since 27/05/2019
	 * 
	 * @param numberRightKeys
	 * @param next
	 * @return int
	 */
	private int checkIfExpressionisARightKey(int numberRightKeys, String next) {
		if(next.equals(DelimitersEnum.RIGHT_KEY.getValue())) numberRightKeys++;
		return numberRightKeys;
	}

	/**
	 * Method that checks if the expression to be read is a left key.
	 * 
	 * @author Mariana Azevedo
	 * @since 27/05/2019
	 * 
	 * @param numberLeftKeys
	 * @param next
	 * @return int
	 */
	private int checkNumberOfLeftKeys(int numberLeftKeys, String next) {
		if(next.equals(DelimitersEnum.LEFT_KEY.getValue())) numberLeftKeys++;
		return numberLeftKeys;
	}
	
	/**
	 * Method that remove a list of json object/json array patterns from the string
	 * 
	 * @author Mariana Azevedo
	 * @since 12/04/2019
	 * 
	 * @param invalidJson
	 * @param jsonObjectPattern
	 * @return String
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
	 * @return String
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
	 * @return boolean
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
	 * @return boolean
	 */
	private boolean hasEqualNumberOfKeysOrBrackets(int numberLeft, int numberRight) {
		return numberLeft == numberRight;
	}

}
