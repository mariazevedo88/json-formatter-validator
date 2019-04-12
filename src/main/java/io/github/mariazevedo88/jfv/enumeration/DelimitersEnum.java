package io.github.mariazevedo88.jfv.enumeration;

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
	RIGHT_BRACKETS("]");
	
	private String value;
	
	private DelimitersEnum(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
