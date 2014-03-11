package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.junit.Test;

public class JSONScannerTestCase extends AbstractScannerTestCase {

   @Test
   public void should() throws Exception {
      
      String source = 
            "{\n" + 
            "    \"firstName\": \"John\",\n" + 
            "    \"lastName\": \"Smith\",\n" + 
            "    \"age\": 25.5,\n" + 
            "    \"address\": {\n" + 
            "        \"streetAddress\": \"21 2nd Street\",\n" + 
            "        \"city\": \"New York\",\n" + 
            "        \"state\": \"NY\",\n" + 
            "        \"postalCode\": 10021\n" + 
            "    },\n" + 
            "    \"phoneNumbers\": [\n" + 
            "        {\n" + 
            "            \"type\": \"home\",\n" + 
            "            \"number\": \"212 555-1234\"\n" + 
            "        },\n" + 
            "        {\n" + 
            "            \"type\": \"fax\",\n" + 
            "            \"number\": \"646 555-4567\"\n" + 
            "        }\n" + 
            "    ]\n" + 
            "}";
      
      Syntax.scan(source, Scanner.Type.JSON.name(), ASSERT_ENCODER, System.out);
      
      assertTextToken(TokenType.content, "John", "Smith", "home");
      assertTextToken(TokenType.integer, "10021");
      assertTextToken(TokenType.float_, "25.5");
   }
}
