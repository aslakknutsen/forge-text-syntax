package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.junit.Test;

public class HTMLScannerTestCase extends AbstractScannerTestCase {
   
   @Test
   public void should() throws Exception {
      
      String source = 
            "<html>\n" + 
            "  <head>\n" + 
            "    <meta charset=\"utf-8\">\n" + 
            "    <title>Forge |</title>\n" + 
            "    <meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\">\n" + 
            "    <style>\n" + 
            "      body {\n" + 
            "        padding-top: 60px;\n" + 
            "      }\n" + 
            "    </style>\n" + 
            "    <script src=\"//cdnjs.cloudflare.com/ajax/libs/jquery/1.7.1/jquery.min.js\" type=\"text/javascript\"></script>\n" + 
            "    <link href=\"/stylesheets/prettify.css\" rel=\"stylesheet\" type=\"text/css\">\n" + 
            "    <!--[if lt IE 9]>\n" + 
            "      <script src=\"//html5shim.googlecode.com/svn/trunk/html5.js\" type=\"text/javascript\"></script>\n" + 
            "    <![endif]-->\n" + 
            "    <script src=\"/javascripts/bootstrap.min.js\" type=\"text/javascript\"></script>\n" + 
            "    <script>\n" + 
            "      $(document).ready(function() {\n" + 
            "      });\n" + 
            "    </script>\n" + 
            "  </head>\n" + 
            "  <body>\n" + 
            "    <div class=\"navbar navbar-fixed-top\">\n" + 
            "      <div class=\"navbar-inner\">\n" + 
            "        <div class=\"container\">\n" + 
            "        </div>\n" + 
            "      </div>\n" + 
            "      <hr>\n" + 
            "      <footer>\n" + 
            "        <p>&copy; JBoss 2014</p>\n" + 
            "      </footer>\n" + 
            "    </div>\n" + 
            "  </body>\n" + 
            "</html>\n";
      
      Syntax.scan(source, Scanner.Type.HTML.name(), ASSERT_ENCODER, System.out);
 
      assertTextToken(TokenType.tag, "<html>", "<head>", "<meta", "<title>", "<body>", "<link", "<style>", "<script", "<div", "<hr>", "<footer>");
      assertTextToken(TokenType.attribute_name, "charset", "content", "src", "class");
      assertTextToken(TokenType.content, "utf-8", "navbar-inner", "text/javascript", "width=device-width, initial-scale=1.0");
   }
}
