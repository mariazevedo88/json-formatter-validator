package io.github.mariazevedo88.jfv.model.enumeration;

/**
 * Enum that lists the items used as delimiters in the application.
 * 
 * @author Mariana Azevedo
 * @since 07/03/2019
 *
 */
public enum DelimitersEnum {
	
	COLON(":"),
	COMMA(","),
	DOUBLE_COMMA(",,"),
	EMPTY_STRING(""),
	RIGHT_DOUBLE_QUOTE_WITH_ESCAPE("\""),
	RIGHT_KEY("}"),
	RIGHT_KEY_WITH_ESCAPE("\"}"),
	SEMICOLON(";"),
	DOUBLE_SEMICOLON(";;"),
	QUOTES("''"),
	LEFT_KEY("{"),
	LEFT_BRACKETS("["),
	RIGHT_BRACKETS("]"),
	COLON_WITH_LEFT_KEY(":{");
	
	private String value;
	
	private DelimitersEnum(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
