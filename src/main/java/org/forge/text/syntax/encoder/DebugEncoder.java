package org.forge.text.syntax.encoder;

import java.io.OutputStream;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Theme;
import org.forge.text.syntax.TokenType;

public class DebugEncoder extends Encoder.AbstractEncoder implements Encoder {

   public DebugEncoder(OutputStream out, Theme theme) {
      super(out, theme);
   }

   @Override
   public void textToken(String text, TokenType type) {
      if(type == TokenType.space) {
         write(text);
      }
      else {
         String output = text;
         if(output.indexOf("\\") != -1) {
            output = output.replaceAll("\\\\", "\\\\\\\\");
         }
         if(output.indexOf(")") != -1) {
            output = output.replaceAll("\\)", "\\\\)");
         }
         write(fixTokeName(type) + "(" + output + ")");
      }
   }

   @Override
   public void beginGroup(TokenType type) {
      write(fixTokeName(type) + "<");
   }

   @Override
   public void endGroup(TokenType type) {
      write(">");
   }

   @Override
   public void beginLine(TokenType type) {
      write(fixTokeName(type) + "[");
   }

   @Override
   public void endLine(TokenType type) {
      write("]");
   }

   public String fixTokeName(TokenType type) {
      String name = type.name();
      if(name.endsWith("_")) {
         name = name.substring(0, name.length()-1);
      }
      return name;
   }
}
