package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.junit.Test;

public class CSSScannerTestCase extends AbstractScannerTestCase {
   
   @Test
   public void should() throws Exception {
      
      String source = "/* See http://reference.sitepoint.com/css/content. */\n" +
            "@media print {\n" +
            "  a[href]:after {\n" +
            "    content: \"<\" attr(href) \">\";\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "a:link:after, a:visited:after {content:\" (\" attr(href) \")\";font-size:90%;}\n" +
            "ol {\n" +
            "  counter-reset: item;\n" +
            "  margin: 0;\n" +
            "  padding: 0.7px;\n" +
            "}\n" +
            ".some {}" +
            "ol>li {\n" +
            "  counter-increment: item;\n" +
            "  list-style: none inside;\n" +
            "}\n" +
            "ol>li:before {\n" +
            "  content: counters(item, \".\") \" - \";\n" +
            "}\n" +
            "\n" +
            "body {\n" +
            "  counter-reset: chapter;\n" +
            "}\n" +
            "h1 {\n" +
            "  counter-increment: chapter;\n" +
            "  counter-reset: section;\n" +
            "}\n" +
            "h2 {\n" +
            "  counter-increment: section;\n" +
            "}\n" +
            "h2:before {\n" +
            "  content: counter(chapter) \".\" counter(section) \" \";\n" +
            "}\n";
      
      Syntax.scan(source, Scanner.Type.CSS.name(), ASSERT_ENCODER, System.out);
 
      assertTextToken(TokenType.attribute_name, "href");
      assertTextToken(TokenType.directive, "@media");
      assertTextToken(TokenType.comment, "/* See http://reference.sitepoint.com/css/content. */");
      assertTextToken(TokenType.tag, "a", "body", "ol");
      assertTextToken(TokenType.class_, ".some");
      assertTextToken(TokenType.float_, "0", "0.7");
      assertTextToken(TokenType.value, "px");
      assertTextToken(TokenType.key, "list-style", "counter-increment", "margin");
      assertTextToken(TokenType.operator, ";", "{", "}", ",");
   }
}
