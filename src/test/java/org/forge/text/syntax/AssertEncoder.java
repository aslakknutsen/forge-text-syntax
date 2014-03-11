package org.forge.text.syntax;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Theme;
import org.forge.text.syntax.TokenType;
import org.junit.Assert;

public class AssertEncoder extends Encoder.AbstractEncoder {

   public AssertEncoder(OutputStream out, Theme theme) {
      super(out, theme);
      textTokens.clear();
   }

   @Override
   public void textToken(String text, TokenType type) {
      textTokens.put(text, type);
   }

   @Override
   public void beginGroup(TokenType type) {
   }

   @Override
   public void endGroup(TokenType type) {
   }

   @Override
   public void beginLine(TokenType type) {
   }

   @Override
   public void endLine(TokenType type) {
   }

   private static Map<String, TokenType> textTokens = new HashMap<String, TokenType>();

   public static void assertTextToken(TokenType type, String... texts) {
      for(String text : texts) {
         Assert.assertEquals("Verify " + text, type, textTokens.get(text));
      }
   }
   
   public static void dump() {
      System.out.println(textTokens);
   }
}
