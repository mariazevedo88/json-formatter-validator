package io.github.mariazevedo88.jsonformattervalidator.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import io.github.mariazevedo88.jsonformattervalidator.JsonFormatterValidatorApplication;
import io.github.mariazevedo88.jsonformattervalidator.formatter.CustomJSONFormatter;

/**
 * CustomJSONFormatter test class
 * 
 * @author Mariana Azevedo
 * @since 01/03/2019
 *
 */
@DisplayName("CustomJSONFormatter")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CustomJSONFormatterTest{
	
	private CustomJSONFormatter formatter;
	
	@BeforeAll
	public void setUp() {
		formatter = new CustomJSONFormatter();
	}
	
	@Test
	@DisplayName("Verify if parameter in JsonFormatterValidatorApplication is null")
	@Order(1)
	public void verifyNullParams() throws IOException {
		 assertThrows(NullPointerException.class,()->{
			JsonFormatterValidatorApplication.main(null);
	     });
	}
	
	@Test
	@DisplayName("Verify if parameter in CustomJSONFormatter is null")
	@Order(2)
	public void verifyANullObject() throws IOException {
		 assertThrows(NullPointerException.class,()->{
			formatter.checkValidityAndFormatObject(null);
	     });
	}
	
	@Test
	@DisplayName("Get a Valid JSON from a jar execution")
	@Order(3)
	public void getJSONFromStringArgsExecution() throws IOException {
		String jsonFromString = "{id:267107086801,productCode:02-671070868,lastUpdate:2018-07-15,payment:[{sequential:1,id:CREDIT_CARD,value:188,installments:9}]}";
		String[] args = new String[] {jsonFromString};
		
		JsonFormatterValidatorApplication.main(args);
		assertTrue(JsonFormatterValidatorApplication.getJson().isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Valid JSON from a Simple String")
	@Order(4)
	public void getJSONFromString() throws IOException {
		String jsonFromString = "{id:267107086801,productCode:02-671070868,lastUpdate:2018-07-15,payment:[{sequential:1,id:CREDIT_CARD,value:188,installments:9}]}";
		JsonObject json = formatter.checkValidityAndFormatObject(jsonFromString);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Valid JSONObject")
	@Order(5)
	public void getValidJson() throws IOException {
		JsonObject json = formatter.getValidJson();
		assertTrue(!json.isJsonNull());
	}
	
	@Test
	@DisplayName("Get a Valid JSON from a String with Empty Fields")
	@Order(6)
	public void getJSONWithEmptyFields() throws IOException {
		String jsonWithEmptyFields = "{id:267111784501,productCode:02-671117845,purchaseDate:2018-07-15,status:APPROVED,estimatedDeliveryDate:2018-09-26,deliveryAddress:{street:Rua Wanderlin Vieira,number:216,reference:,neighborhood:Cachoeira,city:Conselheiro Lafaiete,state:MG,zipcode:36408106,additionalInfo:},paymentMethods:[{sequential:1,id:CREDIT_CARD,value:1216.03,installments:10}]}";
		JsonObject json = formatter.checkValidityAndFormatObject(jsonWithEmptyFields);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Valid JSON where the Field has equal values separeted by commas")
	@Order(7)
	public void getJSONWithCommasOnFieldsWithEqualValues() throws IOException {
		String jsonWithCommasOnAddressField = "{id:267590641902,productCode:02-675906419,purchaseDate:2018-09-17,status:NEW,estimatedDeliveryDate:2018-12-03,deliveryAddress:{street:Rua Baru00e3o do Flamengo,number:35,additionalInfo:311,reference:Entregar na entrada de serviu00e7o, na parte de tru00e1s do pru00e9dio, na rua Senador Vergueiro, num 5.,neighborhood:Flamengo,city:RIO DE JANEIRO,state:RJ,zipcode:22220080},telephones:{main:{ddd:21,number:00026310},secondary:{ddd:21,number:00015462},business:{ddd:21,number:632154789}},billingAddress:{street:Rua Baru00e3o do Flamengo,number:500,additionalInfo:311,reference:Entregar na entrada de serviu00e7o, na parte de tru00e1s do pru00e9dio, na rua Senador Vergueiro, num 5.,neighborhood:Flamengo,city:RIO DE JANEIRO,state:RJ,zipcode:22220080},telephones:{main:{ddd:21,number:00026310},secondary:{ddd:21,number:981405949},business:{ddd:21,number:981405949}},paymentMethods:[{sequential:1,id:VOUCHER,value:70.53,installments:1,idAutorization:null,cardIssuer:null},{sequential:2,id:VOUCHER,value:40.62,installments:1,idAutorization:null,cardIssuer:null}]}";
		JsonObject json = formatter.checkValidityAndFormatObject(jsonWithCommasOnAddressField);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Valid JSON where the Field has different values separeted by commas")
	@Order(8)
	public void getJSONWithCommasOnFieldsWithDifferentValues() throws IOException {
		String jsonWithCommasOnFieldName = "{id:267034342303,productCode:02-670343423,purchaseDate:2018-07-02,customer:{name:Juliano, Thais Ou Lourdes,deliveryAddress:{street:Rua Landel de Moura,number:1212,additionalInfo:CASA,reference:PRu00d3XIMO DA AV. WENCESLAU ESCOBAR.,neighborhood:Tristeza,city:Porto Alegre,state:RS,zipcode:91920150}},billingAddress:{street:Avenida Alberto Bins,number:9687,additionalInfo:conj. 23651,reference:em frente ao sesc,neighborhood:Centro Histu00f3rico,city:Porto Alegre,state:RS,zipcode:90030140},telephones:{main:{ddd:51,number:00032146},secondary:{ddd:51,number:025412333},business:{ddd:51,number:003214541}},totalAmount:578.79,totalFreight:58.99,totalDiscount:0,totalInterest:0,quantity:2,price:259.9,freight:58.99,discount:0,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:578.79,installments:10}]}";
		JsonObject json = formatter.checkValidityAndFormatObject(jsonWithCommasOnFieldName);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Valid JSON  where the Field has Double commas")
	@Order(9)
	public void getJSONWithDoubleComma() throws IOException {
		String jsonWithDoubleComma = "{id:267133121501,productCode:02-671331215,purchaseDate:2018-07-18,estimatedDeliveryDate:2018-09-17,deliveryAddress:{street:Rua Au00e7au00ed,,number:451,additionalInfo:Frente u00e0 Av. Sucupira,,reference:Garagem pequena,,neighborhood:Morada do Sol,city:Presidente Figueiredo,state:AM,zipcode:69735000},totalAmount:169.88,totalFreight:14.99,totalDiscount:0,totalInterest:0,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:169.88,installments:5}]}";
		JsonObject json = formatter.checkValidityAndFormatObject(jsonWithDoubleComma);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Valid JSON With Numbers After Commas")
	@Order(10)
	public void getJSONWithNumbersAfterComma() throws IOException {
		String jsonWithNumbersAfterComma = "{id:267180636401,productCode:02-671806364,purchaseDate:2018-07-26,lastUpdate:2018-07-26,purchaseTimestamp:2018-07-26 18:00:31,lastUpdateTimestamp:2018-07-26 18:09:22,status:NEW,estimatedDeliveryDate:2018-10-31,deliveryAddress:{street:Av. Eugu00eanio Krause, 3034/02,number:3034,reference:zazzazaaa,neighborhood:Armau00e7u00e3o,city:Penha,state:SC,zipcode:88385000},telephones:{main:{ddd:47,number:02020312},secondary:{ddd:47,number:085246321},business:{ddd:47,number:065234187}},billingAddress:{street:Av. Eugu00eanio Krause, 3034/02,number:3034,reference:zazadazaa,neighborhood:Armau00e7u00e3o,city:Penha,state:SC,zipcode:88385000},telephones:{main:{ddd:47,number:001321456},secondary:{ddd:47,number:465413100},business:{ddd:47,number:789798745}},totalAmount:463.89,totalFreight:63.99,totalDiscount:0,totalInterest:0,quantity:1,price:399.9,freight:63.99,discount:0,warehouse:98,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:463.89,installments:10}]}";
		JsonObject json = formatter.checkValidityAndFormatObject(jsonWithNumbersAfterComma);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Valid JSON With Dot Before Commas")
	@Order(11)
	public void getJSONWithDotBeforeComma() throws IOException {
		String jsonWithDotBeforeComma = "{id:106946382801,productCode:01-69463828,purchaseDate:2018-07-22,lastUpdate:2018-07-22,deliveryAddress:{street:Rua Pastor Blablabla,number:666,additionalInfo:Apto 14, bloco B, pru00e9dio vermelho,reference:Pru00f3ximo ao shopping aricanduva,neighborhood:Jardim Imperador (Zona Leste),city:Sao Paulo,state:SP,zipcode:00000000},billingAddress:{street:Rua Pastor Blablabla,number:666,additionalInfo:Bloco B, pru00e9dio vermelho.,reference:Pru00f3ximo ao shopping aricanduva,neighborhood:Jardim Imperador (Zona Leste),city:Sao Paulo,state:SP,zipcode:00000000},totalAmount:169.89,totalFreight:0,totalDiscount:0,totalInterest:0,paymentMethods:[{sequential:1,id:CREDIT_CARD,value:169.89,installments:2}]}";
		JsonObject json = formatter.checkValidityAndFormatObject(jsonWithDotBeforeComma);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a JSON From Empty String")
	@Order(12)
	public void getJSONFromEmptyString() throws IOException {
		assertThrows(JsonParseException.class,()->{
			formatter.checkValidityAndFormatObject("");
	    });
	}
	
	@Test
	@DisplayName("Get a JSON From Empty Object as a String")
	@Order(13)
	public void getJSONFromEmptyObjectAsString() throws IOException {
		assertThrows(StringIndexOutOfBoundsException.class,()->{
			formatter.checkValidityAndFormatObject("{}");
	    });
	}
	
	@Test
	@DisplayName("Get a JSON From Json Object without a value")
	@Order(14)
	public void getJSONFromJsonObjectWithoutValue() throws IOException {
		assertThrows(StringIndexOutOfBoundsException.class,()->{
			formatter.checkValidityAndFormatObject("{blablablabla}");
	    });
	}
	
	@Test
	@DisplayName("Get a JSON From Json Object with a comma as value")
	@Order(15)
	public void getJSONFromJsonObjectWithCommaAsValue() throws IOException {
		assertThrows(JsonSyntaxException.class,()->{
			formatter.checkValidityAndFormatObject("{id: ,}");
	    });
	}
	
	@Test
	@DisplayName("Get a Valid JSON from a JSON file")
	@Order(16)
	public void getJSONFromJsonFile() throws IOException {
		File file = new File("src/test/resources/mock.json");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		JsonObject json = formatter.checkValidityAndFormatObject(reader);
		assertTrue(json.isJsonObject());
	}
	
	@Test
	@DisplayName("Get a Invalid JSON from a JSON file")
	@Order(17)
	public void getInvalidJSONFromJsonFile() throws IOException {
		assertThrows(FileNotFoundException.class,()->{
			File file = new File("");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			formatter.checkValidityAndFormatObject(reader);
	    });
	}
	
	@AfterAll
	public void tearDown() {
		formatter = null;
	}

}
