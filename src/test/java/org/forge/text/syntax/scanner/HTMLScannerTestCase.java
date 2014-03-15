package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.junit.Test;

public class HTMLScannerTestCase extends AbstractScannerTestCase {
   
   @Test
   public void should() throws Exception {
      
      String source = "<p style=\"float:right;\">#{q.answers.size.to_i} answers</p>";
      
      Syntax.Builder.create().scannerType(Scanner.Type.HTML).encoderType(ASSERT_ENCODER).execute(source);
 
      assertTextToken(TokenType.tag, "<p");
      assertTextToken(TokenType.attribute_name, "style");
      assertTextToken(TokenType.key, "float");
      //assertTextToken(TokenType.tag, "<html>", "<head>", "<meta", "<title>", "<body>", "<link", "<style>", "<script", "<div", "<hr>", "<footer>");
      //assertTextToken(TokenType.attribute_name, "charset", "content", "src", "class");
      //assertTextToken(TokenType.content, "utf-8", "navbar-inner", "text/javascript", "width=device-width, initial-scale=1.0");
   }
}
