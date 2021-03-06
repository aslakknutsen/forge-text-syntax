package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.Syntax.Builder;
import org.forge.text.syntax.TokenType;
import org.junit.Ignore;
import org.junit.Test;

public class JavaScannerTestCase extends AbstractScannerTestCase {
   
   @Test @Ignore // simple developer test
   public void should() throws Exception {
      
      String source = "/***** BEGIN LICENSE BLOCK ***** */\n" +
            "package pl.silvermedia.ws;\n" +
            "import java.util.List;\n" +
            "\n" +
            "import javax.jws.WebParam;\n" +
            "import javax.jws.WebService;\n" +
            "\n" +
            "@WebService\n" +
            "public interface ContactUsService {\n" +
            "  List<Message> getMessages();\n" +
            "  Message[] getFirstMessage();\n" +
            "    void postMessage(@WebParam(name = \"message\") Message message) throws UnsupportedOperationException {\n" +
            "        if (File.separatorChar == '\\\\') {" +
            "            bannerText = \"  \" + bannerText + \"  \\n\\n\";\n" +
            "        }\n" +
            "    }" +
            "}\n" +
            "";

      Syntax.Builder.create().scannerType(Scanner.Type.JAVA).encoderType(ASSERT_ENCODER).execute(source);

      assertTextToken(TokenType.comment, "/***** BEGIN LICENSE BLOCK ***** */");
      assertTextToken(TokenType.namespace, "pl.silvermedia.ws");
      assertTextToken(TokenType.predefined_type, "List");
      assertTextToken(TokenType.exception, "UnsupportedOperationException");
      assertTextToken(TokenType.keyword, "import");
      assertTextToken(TokenType.type, "void", "interface", "[]");
      assertTextToken(TokenType.directive, "public");
      assertTextToken(TokenType.content, "message");
      assertTextToken(TokenType.char_, "\\n", "\\\\");
   }

   @Test
   public void shoulMatchJavaExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA), "java", "example.in.java");
   }

   @Test
   public void shoulMatchJavaJRubyExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA), "java", "jruby.in.java");
   }
}
