package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.junit.Test;

public class JavaScannerTestCase extends AbstractScannerTestCase {
   
   @Test
   public void should() throws Exception {
      
      String source = "package org.forge.coderayj.scanner;\n" + 
            "\n" +
            "import org.forge.coderayj.Encoder;\n" + 
            "import org.forge.coderayj.Scanner;\n" + 
            "\n" +
            "private String source = null;\n" +
            "\n" +
            "public class JavaScanner implements Scanner {\n" + 
            "\n" + 
            "   @Override\n" + 
            "   public void scan(String source, Encoder encoder) {\n" + 
            "     Sring a = \"aaa\"\n" + 
            "   }\n" +  
            "}\n";
      
      Syntax.scan(source, Scanner.Type.JAVA.name(), ASSERT_ENCODER, System.out);

      assertTextToken(TokenType.include, "org", "forge");
      assertTextToken(TokenType.keyword, "import");
      assertTextToken(TokenType.type, "void", "class");
      assertTextToken(TokenType.directive, "public");
      assertTextToken(TokenType.content, "aaa");
   }
}
