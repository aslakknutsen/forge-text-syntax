package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.forge.text.syntax.Syntax.Builder;
import org.junit.Ignore;
import org.junit.Test;

public class JSONScannerTestCase extends AbstractScannerTestCase {

   @Test @Ignore // simple developer test
   public void should() throws Exception {
      
      String source = "[\n" +
            "   {\n" +
            "      \"precision\": \"zip\",\n" +
            "      \"Latitude\":  37,\n" +
            "      \"Longitude\": -122.3959,\n" +
            "      \"Address\":   \"\",\n" +
            "      \"City\":      \"SAN FRANCISCO\",\n" +
            "      \"State\":     \"CA\",\n" +
            "      \"Zip\":       \"94107\",\n" +
            "      \"Country\":   \"US\"\n" +
            "   },\n" +
            "   {\n" +
            "      \"precision\": \"zip\",\n" +
            "      \"Latitude\":  37.371991,\n" +
            "      \"Longitude\": -122.026020,\n" +
            "      \"Address\":   \"\",\n" +
            "      \"City\":      \"SUNNYVALE\",\n" +
            "      \"State\":     \"CA\",\n" +
            "      \"Zip\":       \"94085\",\n" +
            "      \"Country\":   \"US\"\n" +
            "   }\n" +
            "]\n";

      Syntax.Builder.create().scannerType(Scanner.Type.JSON).encoderType(ASSERT_ENCODER).execute(source);

      assertTextToken(TokenType.content, "Zip", "precision");
      assertTextToken(TokenType.content, "zip", "CA", "US");
      assertTextToken(TokenType.integer, "37");
      assertTextToken(TokenType.float_, "37.371991", "-122.3959");
   }

   @Test
   public void shoulMatchJSONExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JSON), "json", "example.in.json");
   }

   @Test
   public void shoulMatchJSONLibExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JSON), "json", "json-lib.in.json");
   }

   @Test
   public void shoulMatchJSONBigExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JSON), "json", "big.in.json");
   }

   @Test
   public void shoulMatchJSONBig2Example() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JSON), "json", "big2.in.json");
   }
}
