package io.github.mariazevedo88.jfv.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import io.github.mariazevedo88.jfv.JsonFormatterValidatorApplication;
import io.github.mariazevedo88.jfv.formatter.CustomJSONFormatter;

/**
 * CustomJSONFormatter test class
 * 
 * @author Mariana Azevedo
 * @since 01/03/2019
 *
 */
@DisplayName("CustomJSONFormatterTest")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CustomJSONFormatterTest{
	
	private static final Logger logger = Logger.getLogger(CustomJSONFormatterTest.class.getName());
	private CustomJSONFormatter formatter;
	
	@BeforeAll
	public void setUp() {
		formatter = new CustomJSONFormatter();
	}
	
	@Test
	@DisplayName("Should throws NullPointerException if parameter in JsonFormatterValidatorApplication is null")
	@Order(1)
	public void shouldThrowsNullPointerExceptionIfHasNullParams() throws IOException {
		 assertThrows(NullPointerException.class,()->{
			JsonFormatterValidatorApplication.main(null);
	     });
	}
	
	@Test
	@DisplayName("Should instantiate an JsonFormatterValidatorApplication with Success")
	@Order(2)
	@SuppressWarnings("static-access")
	public void shouldInstantiateAnApplicationWithSuccess() throws IOException {
		JsonFormatterValidatorApplication app = new JsonFormatterValidatorApplication();
		
		String jsonFromString = "{id:267107086801,productCode:02-671070868,purchaseDate:2018-07-15}";
		String[] args = new String[] {jsonFromString};
		app.main(args);
		
		assertTrue(app.getJson().isJsonObject());
	}
	
	@Test
	@DisplayName("Should throws NullPointerException if parameter in CustomJSONFormatter is null")
	@Order(3)
	public void shouldThrowsNullPointerExceptionIfParamIsANullObject() throws IOException {
		 assertThrows(NullPointerException.class,()->{
			formatter.checkValidityAndFormatObject(null, false, false);
	     });
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a jar execution")
	@Order(4)
	public void shouldGetValidJSONFromStringArgsExecution() throws IOException {
		String jsonFromString = "{id:267107086801,productCode:02-671070868,lastUpdate:2018-07-15,payment:[{sequential:1,id:CREDIT_CARD,value:188,installments:9}]}";
		String[] args = new String[] {jsonFromString};
		
		JsonFormatterValidatorApplication.main(args);
		assertTrue(JsonFormatterValidatorApplication.getJson().isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a simple String")
	@Order(5)
	public void shouldGetValidJSONFromStringAnalyzed() throws IOException {
		String jsonFromString = "{id:267107086801,productCode:02-671070868,lastUpdate:2018-07-15,payment:[{sequential:1,id:CREDIT_CARD,value:188,installments:9}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFromString, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSONObject")
	@Order(6)
	public void shouldGetValidJSON() throws IOException {
		JsonElement json = formatter.getValidJson();
		assertTrue(!json.isJsonNull());
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a String with empty fields")
	@Order(7)
	public void shouldGetValidJSONFromStringWithEmptyFields() throws IOException {
		String jsonWithEmptyFields = "{id:267111784501,productCode:02-671117845,purchaseDate:2018-07-15,status:APPROVED,estimatedDeliveryDate:2018-09-26,deliveryAddress:{street:Rua Wanderlin Vieira,number:216,reference:,neighborhood:Cachoeira,city:Conselheiro Lafaiete,state:MG,zipcode:36408106,additionalInfo:},paymentMethods:[{sequential:1,id:CREDIT_CARD,value:1216.03,installments:10}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithEmptyFields, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON of a String with equal values separeted by commas")
	@Order(8)
	public void shouldGetValidJSONOfStringWithEqualValuesSeparatedByCommas() throws IOException {
		String jsonWithCommasOnAddressField = "{id:267590641902,productCode:02-675906419,purchaseDate:2018-09-17,status:NEW,estimatedDeliveryDate:2018-12-03,deliveryAddress:{street:Rua Baru00e3o do Flamengo,number:35,additionalInfo:311,reference:Entregar na entrada de serviu00e7o, na parte de tru00e1s do pru00e9dio, na rua Senador Vergueiro, num 5.,neighborhood:Flamengo,city:RIO DE JANEIRO,state:RJ,zipcode:22220080},telephones:{main:{ddd:21,number:00026310},secondary:{ddd:21,number:00015462},business:{ddd:21,number:632154789}},billingAddress:{street:Rua Baru00e3o do Flamengo,number:500,additionalInfo:311,reference:Entregar na entrada de serviu00e7o, na parte de tru00e1s do pru00e9dio, na rua Senador Vergueiro, num 5.,neighborhood:Flamengo,city:RIO DE JANEIRO,state:RJ,zipcode:22220080},telephones:{main:{ddd:21,number:00026310},secondary:{ddd:21,number:981405949},business:{ddd:21,number:981405949}},paymentMethods:[{sequential:1,id:VOUCHER,value:70.53,installments:1,idAutorization:null,cardIssuer:null},{sequential:2,id:VOUCHER,value:40.62,installments:1,idAutorization:null,cardIssuer:null}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithCommasOnAddressField, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON of a malformed String with different values separeted by commas")
	@Order(9)
	public void shouldGetValidJSONOfStringWithDifferentValuesSeparatedByCommas() throws IOException {
		String jsonWithCommasOnFieldName = "{id:267034342303,productCode:02-670343423,purchaseDate:2018-07-02,customer:{name:Juliano, Thais Ou Lourdes,deliveryAddress:{street:Rua Landel de Moura,number:1212,additionalInfo:CASA,reference:PRu00d3XIMO DA AV. WENCESLAU ESCOBAR.,neighborhood:Tristeza,city:Porto Alegre,state:RS,zipcode:91920150}},billingAddress:{street:Avenida Alberto Bins,number:9687,additionalInfo:conj. 23651,reference:em frente ao sesc,neighborhood:Centro Histu00f3rico,city:Porto Alegre,state:RS,zipcode:90030140},telephones:{main:{ddd:51,number:00032146},secondary:{ddd:51,number:025412333},business:{ddd:51,number:003214541}},totalAmount:578.79,totalFreight:58.99,totalDiscount:0,totalInterest:0,quantity:2,price:259.9,freight:58.99,discount:0,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:578.79,installments:10}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithCommasOnFieldName, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON of a malformed String with Double commas")
	@Order(10)
	public void shouldGetValidJSONOfStringWithDoubleCommas() throws IOException {
		String jsonWithDoubleComma = "{id:267133121501,productCode:02-671331215,purchaseDate:2018-07-18,estimatedDeliveryDate:2018-09-17,deliveryAddress:{street:Rua Au00e7au00ed,,number:451,additionalInfo:Frente u00e0 Av. Sucupira,,reference:Garagem pequena,,neighborhood:Morada do Sol,city:Presidente Figueiredo,state:AM,zipcode:69735000},totalAmount:169.88,totalFreight:14.99,totalDiscount:0,totalInterest:0,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:169.88,installments:5}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithDoubleComma, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON of a malformed String With Numbers after commas")
	@Order(11)
	public void shouldGetValidJSONOfStringWithNumbersAfterCommas() throws IOException {
		String jsonWithNumbersAfterComma = "{id:267180636401,productCode:02-671806364,purchaseDate:2018-07-26,lastUpdate:2018-07-26,purchaseTimestamp:2018-07-26 18:00:31,lastUpdateTimestamp:2018-07-26 18:09:22,status:NEW,estimatedDeliveryDate:2018-10-31,deliveryAddress:{street:Av. Eugu00eanio Krause, 3034/02,number:3034,reference:zazzazaaa,neighborhood:Armau00e7u00e3o,city:Penha,state:SC,zipcode:88385000},telephones:{main:{ddd:47,number:02020312},secondary:{ddd:47,number:085246321},business:{ddd:47,number:065234187}},billingAddress:{street:Av. Eugu00eanio Krause, 3034/02,number:3034,reference:zazadazaa,neighborhood:Armau00e7u00e3o,city:Penha,state:SC,zipcode:88385000},telephones:{main:{ddd:47,number:001321456},secondary:{ddd:47,number:465413100},business:{ddd:47,number:789798745}},totalAmount:463.89,totalFreight:63.99,totalDiscount:0,totalInterest:0,quantity:1,price:399.9,freight:63.99,discount:0,warehouse:98,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:463.89,installments:10}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithNumbersAfterComma, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON of a malformed String with Dot before commas")
	@Order(12)
	public void shouldGetValidJSONOfStringWithDotBeforeCommas() throws IOException {
		String jsonWithDotBeforeComma = "{id:106946382801,productCode:01-69463828,purchaseDate:2018-07-22,lastUpdate:2018-07-22,deliveryAddress:{street:Rua Pastor Blablabla,number:666,additionalInfo:Apto 14, bloco B, pru00e9dio vermelho,reference:Pru00f3ximo ao shopping aricanduva,neighborhood:Jardim Imperador (Zona Leste),city:Sao Paulo,state:SP,zipcode:00000000},billingAddress:{street:Rua Pastor Blablabla,number:666,additionalInfo:Bloco B, pru00e9dio vermelho.,reference:Pru00f3ximo ao shopping aricanduva,neighborhood:Jardim Imperador (Zona Leste),city:Sao Paulo,state:SP,zipcode:00000000},totalAmount:169.89,totalFreight:0,totalDiscount:0,totalInterest:0,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:169.89,installments:2}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithDotBeforeComma, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should throws NullPointerException if parameter in CustomJSONFormatter is an empty String")
	@Order(13)
	public void shouldThrowsNullPointerExceptionIfParamIsEmptyString() throws IOException {
		assertThrows(JsonParseException.class,()->{
			formatter.checkValidityAndFormatObject("", false, false);
	    });
	}
	
	@Test
	@DisplayName("Should throws StringIndexOutOfBoundsException if parameter in CustomJSONFormatter is an empty JSONObject")
	@Order(14)
	public void shouldThrowsStringIndexOutOfBoundsExceptionFromEmptyObjectAsString() throws IOException {
		assertThrows(StringIndexOutOfBoundsException.class,()->{
			formatter.checkValidityAndFormatObject("{}", false, false);
	    });
	}
	
	@Test
	@DisplayName("Should throws a StringIndexOutOfBoundsException with String is JsonObject with a key without value")
	@Order(15)
	public void shouldThrowsStringIndexOutOfBoundsExceptionFromJsonObjectWithoutValue() throws IOException {
		assertThrows(StringIndexOutOfBoundsException.class,()->{
			formatter.checkValidityAndFormatObject("{blablablabla}", false, false);
	    });
	}
	
	@Test
	@DisplayName("Should get a valid JSON From JsonObject with a comma as value")
	@Order(16)
	public void shouldGetValidJSONFromJsonObjectWithCommaAsValue() throws IOException {
		JsonElement json = formatter.checkValidityAndFormatObject("{id: ,}", false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a valid JSON file")
	@Order(17)
	public void shouldGetValidJSONFromValidJSONFile() throws IOException {
		File file = new File("src/test/resources/mock_valid_json.json");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		JsonElement json = formatter.checkValidityAndFormatObject(reader, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should throws FileNotFoundException from an empty JSON file")
	@Order(18)
	public void shouldThrowsFileNotFoundExceptionFromInvalidJSONFile() throws IOException {
		assertThrows(FileNotFoundException.class,()->{
			File file = new File("");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			formatter.checkValidityAndFormatObject(reader, false, false);
	    });
	}
	
	@Test
	@DisplayName("Should throws StringIndexOutOfBoundsException from empty Array as a String")
	@Order(19)
	public void shouldThrowsStringIndexOutOfBoundsExceptionFromEmptyArrayAsString() throws IOException {
		assertThrows(StringIndexOutOfBoundsException.class,()->{
			formatter.checkValidityAndFormatObject("[]", false, false);
	    });
	}
	
	@Test
	@DisplayName("Should verify if malformed String as a JSON Array is a valid JSONObject")
	@Order(20)
	public void shouldVerifyIfMalformedJSONArrayIsJSONObject() throws IOException {
		String jsonWithDotBeforeComma = "{paymentMethods:[{sequential:1,id:CREDIT_CARD,value:169.89,installments:2}]}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithDotBeforeComma, false, false);
		
		JsonElement jsonElement = ((JsonObject) json).get("paymentMethods");
		assertFalse(jsonElement.isJsonObject());
	}
	
	@Test
	@DisplayName("Should verify if malformed String as a JSON Object has a valid value")
	@Order(21)
	public void shouldVerifyIfMalformedStringJSONObjectHasValidValue() throws IOException {
		String jsonWithDotBeforeComma = "{id:1234567890, productCode:02-671806364}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithDotBeforeComma, false, false);
		
		JsonElement jsonElement = ((JsonObject) json).get("id");
		assertTrue(jsonElement.isJsonPrimitive()); //JsonPrimitive = primitive types and Java types
	}
	
	@Test
	@DisplayName("Should get a valid JSON Object from malformed String with a double comma and space as value")
	@Order(22)
	public void shouldGetValidJSONObjectFromMalformedStringWithDoubleCommaAndSpaceValue() throws IOException {
		String jsonWithDoubleCommaAndSpace = "{id:268852005101,productCode:02-688520051,address:{street:Rua B,number:666,additionalInfo:Apto 666 , Bloco 1 ,,reference:Organizacoes Tabajara,neighborhood:Tabajara,city:São Paulo,state:SP,zipcode:12345678}}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithDoubleCommaAndSpace, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON Object from malformed String with only hours as string")
	@Order(23)
	public void shouldGetValidJSONObjectFromMalformedStringWithOnlyHoursAsString() throws IOException {
		String jsonWithOnlyHoursAsString = "{id:268862679704,productCode:02-688626797,purchaseDate:2019-02-03,address:{street:Rua Cinco,number:240,additionalInfo:Teste,reference:Ao lado lotus, Recebimento 7:15 as 17:00,neighborhood:Centro,city:Lavras,state:MG,zipcode:00000000}}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithOnlyHoursAsString, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON Object from malformed String with parentheses wrongly placed")
	@Order(24)
	public void shouldGetValidJSONObjectFromMalformedStringWithParenthesesWronglyPlaced() throws IOException {
		String jsonWithParenthesesWronglyPlaced = "{id:266861122901,productCode:02-668611229,address:{street:Rua Teste,number:22,additionalInfo:Apto 01,reference:Em frente a padaria ( a casa nao tem porteiro, ou campainha) ligar avisando que chegou,neighborhood:Teste,city:Lavras,state:MG,zipcode:37200000}}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithParenthesesWronglyPlaced, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON Object from malformed String with colon wrongly placed")
	@Order(25)
	public void shouldGetValidJSONObjectFromMalformedStringWithColonWronglyPlaced() throws IOException {
		String jsonWithColonWronglyPlaced = "{id:268856993701,productCode:02-688569937,purchaseDate:2019-02-02,address:{street:Rua Pachecao,number:2019,additionalInfo:casa FRENTE. 21965307587,reference:depois do ponto de onibus 666, no seguno numero. Procurar fulano  TELE: 35 981149567 .,neighborhood:Jardim Floresta,city:Lavras,state:MG,zipcode:37200000}}";
		JsonElement json = formatter.checkValidityAndFormatObject(jsonWithColonWronglyPlaced, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should remove a JSON Attribute from malformed String")
	@Order(26)
	public void shouldRemoveJSONAttributeFromMalformedString() throws IOException {
		String jsonToRemove = "{id:268852005101,productCode:02-688520051,purchaseDate:2019-02-01,lastUpdate:2019-02-01}";
		logger.info("Invalid json with string to remove: " + jsonToRemove);
		String[] removeAll = {"purchaseDate"};
		String jsonFormatted = formatter.removeJSONObjectsFromString(jsonToRemove, removeAll);
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFormatted, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should remove a JSON Object from malformed String")
	@Order(27)
	public void shouldRemoveJSONObjectFromMalformedString() throws IOException {
		String jsonToRemove = "{pf:{cpf:11122233385,name:MARIANA DE AZEVEDO SANTOS}, localDate:2019-02-01}";
		logger.info("Invalid json with string to remove: " + jsonToRemove);
		String[] removeAll = {"pf"};
		String jsonFormatted = formatter.removeJSONObjectsFromString(jsonToRemove, removeAll);
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFormatted, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should remove a JSON Array from malformed String")
	@Order(28)
	public void shouldRemoveJSONArrayFromMalformedString() throws IOException {
		String jsonToRemove = "{totalAmount:326.98,totalFreight:79.99,totalDiscount:0,products:[{link:{id:BLABLABLA-1,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}, {link:{id:BLABLABLA-2,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}]}";
		logger.info("Invalid json with string to remove: " + jsonToRemove);
		String[] removeAll = {"products"};
		String jsonFormatted = formatter.removeJSONObjectsFromString(jsonToRemove, removeAll);
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFormatted, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should remove more than one attribute from malformed String")
	@Order(29)
	public void shouldRemoveMoreThanOneAttributeFromMalformedString() throws IOException {
		String jsonToRemove = "{totalAmount:326.98,totalFreight:79.99,totalDiscount:0,products:[{link:{id:BLABLABLA-1,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}, {link:{id:BLABLABLA-2,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}]}";
		logger.info("Invalid json with string to remove: " + jsonToRemove);
		String[] removeAll = {"products", "totalAmount"};
		String jsonFormatted = formatter.removeJSONObjectsFromString(jsonToRemove, removeAll);
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFormatted, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should filter a JSON Object from malformed String")
	@Order(30)
	public void shouldFilterJSONObjectFromMalformedString() throws IOException {
		String jsonToFilter = "{totalAmount:326.98,totalFreight:79.99,totalDiscount:0,products:[{link:{id:BLABLABLA-1,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}, {link:{id:BLABLABLA-2,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}]}";
		logger.info("Invalid json with string to filter: " + jsonToFilter);
		String[] filter = {"totalDiscount"};
		String jsonFormatted = formatter.filterJSONObjectsFromString(jsonToFilter, filter);
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFormatted, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should filter more than one attribute from malformed String")
	@Order(31)
	public void shouldFilterMoreThanOneAttributeFromMalformedString() throws IOException {
		String jsonToFilter = "{totalAmount:326.98,totalFreight:79.99,totalDiscount:0,products:[{link:{id:BLABLABLA-1,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}, {link:{id:BLABLABLA-2,rel:sku},quantity:1,price:246.99,freight:79.99,discount:0}]}";
		logger.info("Invalid json with string to filter: " + jsonToFilter);
		String[] filter = {"products", "totalAmount"};
		String jsonFormatted = formatter.filterJSONObjectsFromString(jsonToFilter, filter);
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFormatted, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should remove more than one attribute from malformed String, but the final result is null")
	@Order(32)
	public void shouldRemoveAttributesOfAInvalidJSON() throws IOException {
		String jsonToRemove = "{id:265998308001,productCode:02-659983080,purchaseDate:2018-01-17,customer:{pf:{cpf:012345678,name:Mariana},deliveryAddress:{street:Rua Fechada,number:666,additionalInfo:casa,reference:nos fundos do armazem,neighborhood:Lavras,city:Lavras,state:MG,zipcode:01234000},telephones:{main:{ddd:35,number:38222482},secondary:{ddd:35,number:38222482},business:{ddd:35,number:38222482}}},payer:{pf:{cpf:012345678,name:Mariana},birthDate:1988-07-22,billingAddress:{street:Rua Fechada,number:22,additionalInfo:casa da rua (error},reference:em Frente a Praca Agusto Silva,neighborhood:Lavras,city:Lavras,state:MG,zipcode:24415040},business:{ddd:35,number:38222482}}},totalAmount:183.98}";
		logger.info("Invalid json with string to remove: " + jsonToRemove);
		String[] remove = {"customer", "payer"};
		String jsonFormatted = formatter.removeJSONObjectsFromString(jsonToRemove, remove);
		JsonElement json = formatter.checkValidityAndFormatObject(jsonFormatted, false, true);
		assertNull(json);
	}
	
	@Test
	@DisplayName("Should remove more than one attribute from malformed String, but the final result is a JsonSyntaxException")
	@Order(33)
	public void shouldRemoveAttributesWithException() throws IOException {
		assertThrows(JsonSyntaxException.class,()->{
			String jsonToRemove = "{id:265998308001,productCode:02-659983080,purchaseDate:2018-01-17,customer:{pf:{cpf:012345678,name:Mariana},deliveryAddress:{street:Rua Fechada,number:666,additionalInfo:casa,reference:nos fundos do armazem,neighborhood:Lavras,city:Lavras,state:MG,zipcode:01234000},telephones:{main:{ddd:35,number:38222482},secondary:{ddd:35,number:38222482},business:{ddd:35,number:38222482}}},payer:{pf:{cpf:012345678,name:Mariana},birthDate:1988-07-22,billingAddress:{street:Rua Fechada,number:22,additionalInfo:casa da rua (error},reference:em Frente a Praca Agusto Silva,neighborhood:Lavras,city:Lavras,state:MG,zipcode:24415040},business:{ddd:35,number:38222482}}},totalAmount:183.98}";
			logger.info("Invalid json with string to remove: " + jsonToRemove);
			String[] remove = {"customer", "payer"};
			String jsonFormatted = formatter.removeJSONObjectsFromString(jsonToRemove, remove);
			formatter.checkValidityAndFormatObject(jsonFormatted, false, false);
		});
	}
	
	@Test
	@DisplayName("Should verify if a malformed String is a valid JSON")
	@Order(34)
	public void shouldVerifyIfJSONIsValid() throws IOException {
		String jsonFromString = "{id:267107086801,productCode:02-671070868,purchaseDate:2018-07-15}";
		logger.info("Invalid json: " + jsonFromString);
		assertFalse(formatter.isValidJson(jsonFromString, true));
	}
	
	@Test
	@DisplayName("Should verify if String is valid JSON from a invalid JSON file")
	@Order(35)
	public void shouldVerifyIfJSONIsValidFromInvalidJSONFile() throws IOException {
		File file = new File("src/test/resources/mock_invalid_json.json");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		logger.info("Invalid json file: " + file.getName());
		JsonElement json = formatter.checkValidityAndFormatObject(reader, false, false);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a String with empty fields, but muting exception")
	@Order(36)
	public void shouldGetValidJSONFromStringWithEmptyFieldsMutingException() throws IOException {
		JsonElement json = formatter.checkValidityAndFormatObject(null, false, true);
		assertNull(json);
	}
	
	@Test
	@DisplayName("Should not throws StringIndexOutOfBoundsException with mute exception flag")
	@Order(37)
	public void shouldNotThrowsStringIndexOutOfBoundsExceptionWithMuteExceptionFlag() throws IOException {
		JsonElement json = formatter.checkValidityAndFormatObject("[]", false, true);
		assertNull(json);
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a invalid JSON file")
	@Order(38)
	public void shouldGetValidJsonFromInvalidJSONFile() throws IOException {
		File file = new File("src/test/resources/mock_invalid_json.json");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		logger.info("Invalid json file: " + file.getName());
		assertTrue(formatter.isValidJson(reader, true));
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a invalid JsonObject with mute logs flag")
	@Order(39)
	public void shouldGetValidJsonFromJSONObjectMutingLogs() throws IOException {
		
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("name", "Mariana");

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("developer", innerObject);
		
		logger.info("Json object in test: " + jsonObject);
		assertTrue(formatter.isValidJson(jsonObject, true));
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a invalid JsonArray with mute logs flag")
	@Order(40)
	public void shouldGetValidJsonFromJSONArrayMutingLogs() throws IOException {
		
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("name", "Mariana");

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("developer", innerObject);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		logger.info("Json object in test: " + jsonArray);
		assertTrue(formatter.isValidJson(jsonArray, true));
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a invalid JsonObject")
	@Order(41)
	public void shouldGetValidJsonFromJSONObject() throws IOException {
		
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("name", "Mariana");

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("developer", innerObject);
		
		logger.info("Json object in test: " + jsonObject);
		assertNotNull(formatter.checkValidityAndFormatObject(jsonObject, false, false));
	}
	
	@Test
	@DisplayName("Should get a valid JSON from a invalid JsonArray")
	@Order(42)
	public void shouldGetValidJsonFromJSONArray() throws IOException {
		
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("name", "Mariana");

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("developer", innerObject);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		logger.info("Json object in test: " + jsonArray);
		assertNotNull(formatter.checkValidityAndFormatObject(jsonArray, false, false));
	}
	
	@Test
	@DisplayName("Should get a valid JSON on parse a file")
	@Order(43)
	public void shouldGetValidJsonOnParseAFile() throws IOException {
		File file = new File("src/test/resources/mock_invalid_json.json");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		logger.info("Invalid json file: " + file.getName());
		formatter.parseJSONObject(reader, true);
		assertNotNull(formatter.getValidJson());
	}
	
	@Test
	@DisplayName("Should get a valid JSON on parse a JsonArray")
	@Order(44)
	public void shouldGetValidJsonOnParseAJsonArray() throws IOException {
		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("name", "Mariana");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("developer", innerObject);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		logger.info("Json object in test: " + jsonArray);
		formatter.parseJSONObject(jsonArray.toString(), true);
		assertNotNull(formatter.getValidJson());
	}
	
	@AfterAll
	public void tearDown() {
		formatter = null;
	}

}
