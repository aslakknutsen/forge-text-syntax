package org.forge.text.syntax.scanner;

import static org.forge.text.syntax.encoder.AssertEncoder.assertTextToken;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.junit.Test;

public class CSSScannerTestCase extends AbstractScannerTestCase {
   
   @Test
   public void should() throws Exception {
      
      String source =
            "@import url(\"resource:///org/gnome/adwaita/gtk-main-dark.css\");\n" +
            ".effeckt-button,\n" + 
            ".effeckt-button .spinner,\n" + 
            ".effeckt-button .label {\n" + 
            "  -webkit-transition: 500ms cubic-bezier(0.175, 0.885, 0.32, 1.275) all;\n" + 
            "  -o-transition: 500ms cubic-bezier(0.175, 0.885, 0.32, 1.275) all;\n" + 
            "  transition: 500ms cubic-bezier(0.175, 0.885, 0.32, 1.275) all;\n" + 
            "}\n" + 
            "\n" + 
            ".effeckt-button[data-effeckt-type=\"expand-right\"] .spinner {\n" + 
            "  right: 16px;\n" + 
            "}\n";
      
      Syntax.scan(source, Scanner.Type.CSS.name(), ASSERT_ENCODER, System.out);
 
      assertTextToken(TokenType.directive, "@import");
      assertTextToken(TokenType.content, "\"resource:///org/gnome/adwaita/gtk-main-dark.css\"");
      assertTextToken(TokenType.class_, ".effeckt-button", ".spinner", ".label");
      assertTextToken(TokenType.float_, "500", "0.175", "0.885", "0.32");
      assertTextToken(TokenType.value, "ms", "all", "px");
      assertTextToken(TokenType.key, "-o-transition", "transition", "right");
      assertTextToken(TokenType.operator, ";", "{", "}", ",");
   }
}
