# json-formatter-validator

Library used to format an invalid json: a string in json-like format, but no quotation marks on keys and values. The tool works with two input types: string and json file. 

## About json-formatter-validator

The method `checkValidityAndFormatObject()` checks if json is valid or not. If json is invalid, the string is formatted via regex to add the quotation marks. If json is already valid, nothing is done and json itself is returned. This functionality can be called as follows:

**Input**

```
String invalidJSON = "{id: 267107086801, productCode: 02-671070868, lastUpdate: 2018-07-15, lastUpdateTimestamp: 2018-07-15 01:49:58, payment: [{sequential: 1, id: CREDIT_CARD, value: 188, installments: 9}]}";
CustomJSONFormatter formatter = new CustomJSONFormatter();
formatter.checkValidityAndFormatObject(invalidJSON);  
```

**Output**

```
19/02/14 00:08:57 INFO jsonformattervalidator.JsonFormatterValidatorApplication: Started Json Formatter Validator Aplication
19/02/14 00:08:57 INFO formatter.CustomJSONFormatter: Invalid json: {id: 267107086801, productCode: 02-671070868, lastUpdate: 2018-07-15, lastUpdateTimestamp: 2018-07-15 01:49:58, payment: [{sequential: 1, id: CREDIT_CARD, value: 188, installments: 9}]}
19/02/14 00:08:57 INFO formatter.CustomJSONFormatter: Valid json: {"id":"267107086801","productCode":"02-671070868","lastUpdate":"2018-07-15","lastUpdateTimestamp":"2018-07-15 01:49:58","payment":[{"sequential":"1","id":"CREDIT_CARD","value":"188","installments":"9"}]}
```

To access the valid json, the `getValidJson()` method should be used. Example:

**Input**

```
JsonObject validJson = formatter.getValidJson();
JsonElement id = validJson.get("id");
logger.info(id.getAsLong());
```

**Output**

```
19/02/14 00:08:57 INFO jsonformattervalidator.JsonFormatterValidatorApplication: 267107086801
```

## Dependencies

- Java 8
- [Gson](https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.5)
- [log4j](https://mvnrepository.com/artifact/log4j/log4j/1.2.17)

## How to use

You must import .jar into the classpath of your project. If your project is a maven project, just set it as dependency in `pom.xml`, as follows:

```
<dependency>
   	<groupId>io.github.mariazevedo88</groupId>
   	<artifactId>json-formatter-validator</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<scope>system</scope>
	<systemPath>${project.basedir}/src/main/resources/json-formatter-validator-jar-with-dependencies.jar</systemPath>
</dependency>
```
