package io.github.mariazevedo88.jsonobjectgetter;

import org.apache.log4j.Logger;

import io.github.mariazevedo88.jsonobjectgetter.formatter.CustomJSONFormatter;

public class JsonObjectGetterApplication {
	
	private static final Logger logger = Logger.getLogger(JsonObjectGetterApplication.class.getName());

	public static void main(String[] args){
		logger.info("Started Json Object Getter Aplication");
		
		CustomJSONFormatter formatter = new CustomJSONFormatter();
		
		for(String arg : args) {
			if(formatter.isValidJson(arg)) {
				logger.info(formatter.getValidJson());
			}else {
				logger.info(CustomJSONFormatter.getInvalidJsonToFormat(arg));
			}
		}
			
	}

}

