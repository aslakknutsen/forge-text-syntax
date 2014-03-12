package org.forge.text.syntax.encoder;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
      textTokens.add(new TokenPair(text, type));
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

   private static List<TokenPair> textTokens = new ArrayList<TokenPair>();

   private static class TokenPair {
      private String text;
      private TokenType type;

      public TokenPair(String text, TokenType type) {
         this.text = text;
         this.type= type;
      }

      @Override
      public String toString() {
         return "[text=" + text + ", type=" + type + "]";
      }
   }

   public static void assertTextToken(TokenType type, String... texts) {
      for(String text : texts) {
         boolean found = false;
         List<TokenPair> textMatches = new ArrayList<TokenPair>();
         for(TokenPair pair : textTokens) {
            if(pair.text.equals(text)) {
               textMatches.add(pair);
               if(pair.type == type) {
                  found = true;
                  break;
               }
            }
         }
         if(!found) {
            Assert.fail("Expected [" + text + "] of type [" + type + "]: Found matches: " + textMatches);
         }
      }
   }
   
   public static void dump() {
      System.out.println(textTokens);
   }
}
