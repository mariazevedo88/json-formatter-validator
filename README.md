# json-formatter-validator 
[![Build Status](https://travis-ci.org/mariazevedo88/json-formatter-validator.svg?branch=master)](https://travis-ci.org/mariazevedo88/json-formatter-validator) [![Coveralls github](https://img.shields.io/coveralls/github/mariazevedo88/json-formatter-validator.svg)](https://coveralls.io/github/mariazevedo88/json-formatter-validator?branch=master) ![GitHub tag (latest SemVer)](https://img.shields.io/github/tag/mariazevedo88/json-formatter-validator.svg) ![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/https/oss.sonatype.org/io.github.mariazevedo88/json-formatter-validator.svg) ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/mariazevedo88/json-formatter-validator.svg) ![GitHub language count](https://img.shields.io/github/languages/count/mariazevedo88/json-formatter-validator.svg) ![GitHub top language](https://img.shields.io/github/languages/top/mariazevedo88/json-formatter-validator.svg) ![GitHub All Releases](https://img.shields.io/github/downloads/mariazevedo88/json-formatter-validator/total.svg) ![GitHub last commit](https://img.shields.io/github/last-commit/mariazevedo88/json-formatter-validator.svg)

## About json-formatter-validator

Library used to format an invalid JSON: a string in a JSON-like format, but no quotation marks on keys and values, or with other errors. The tool works with two input types: string and JSON file. 

The method `checkValidityAndFormatObject()` checks if JSON is valid or not. If JSON is invalid, the string is formatted via regex to add the quotation marks. If JSON is already valid, nothing is done and JSON itself is returned. This functionality can be called as follows:

**Input**

```
String invalidJSON = "{id:267107086801,productCode:02-671070868,lastUpdate:2018-07-15,lastUpdateTimestamp:2018-07-15 01:49:58,payment:[{sequential:1,id:CREDIT_CARD,value:188,installments:9}]}";
CustomJSONFormatter formatter = new CustomJSONFormatter();
formatter.checkValidityAndFormatObject(invalidJSON);  
```

**Output**

```
19/02/14 00:08:57 INFO jsonformattervalidator.JsonFormatterValidatorApplication: Started Json Formatter Validator Aplication
19/02/14 00:08:57 INFO formatter.CustomJSONFormatter: Invalid json: {id: 267107086801,productCode:02-671070868,lastUpdate:2018-07-15,lastUpdateTimestamp:2018-07-15 01:49:58,payment:[{sequential:1,id: CREDIT_CARD,value: 188,installments:9}]}
19/02/14 00:08:57 INFO formatter.CustomJSONFormatter: Valid json: {"id":"267107086801","productCode":"02-671070868","lastUpdate":"2018-07-15","lastUpdateTimestamp":"2018-07-15 01:49:58","payment":[{"sequential":"1","id":"CREDIT_CARD","value":"188","installments":"9"}]}
```

To access the valid JSON, the `getValidJson()` method should be used. Example:

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

- [Java 8](https://www.oracle.com/technetwork/pt/java/javase/downloads/index.html)
- [Maven](https://maven.apache.org/)
- [Gson](https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.5)
- [log4j](https://mvnrepository.com/artifact/log4j/log4j/1.2.17)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)

## How to use

You must import .jar into the classpath of your project. If your project is a maven project, just set it as dependency in `pom.xml`, as follows:

```
<dependency>
  <groupId>io.github.mariazevedo88</groupId>
  <artifactId>json-formatter-validator</artifactId>
  <version>1.1.9</version>
</dependency>
```

## Contributing

[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/0)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/0)[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/1)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/1)[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/2)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/2)[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/3)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/3)[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/4)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/4)[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/5)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/5)[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/6)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/6)[![](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/images/7)](https://sourcerer.io/fame/mariazevedo88/mariazevedo88/json-formatter-validator/links/7)
