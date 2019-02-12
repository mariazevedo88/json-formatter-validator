package io.github.mariazevedo88.jsonobjectgetter;

import java.io.IOException;

import org.apache.log4j.Logger;

import io.github.mariazevedo88.jsonobjectgetter.formatter.CustomJSONFormatter;

/**
 * @author Mariana Azevedo
 * @since 10/02/2019
 *
 */
public class JsonObjectGetterApplication {
	
	private static final Logger logger = Logger.getLogger(JsonObjectGetterApplication.class.getName());

	public static void main(String[] args) throws IOException{
		logger.info("Started Json Object Getter Aplication");
		
		CustomJSONFormatter formatter = new CustomJSONFormatter();
		
		for(String arg : args) {
			formatter.checkValidityAndFormatObject(arg);
		}
	}

}

