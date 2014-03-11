package org.forge.text.syntax.encoder;

import java.awt.Color;
import java.io.OutputStream;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Theme;
import org.forge.text.syntax.TokenType;

public class TerminalEncoder extends Encoder.AbstractEncoder implements Encoder {

   public TerminalEncoder(OutputStream out, Theme theme) {
      super(out, theme);
      write(TerminalString.RESET); // reset terminal colors
   }
   
   @Override
   public void textToken(String text, TokenType type) {
      Color color = color(type);
      if(color != null) {
         write(new TerminalString(new TerminalColor(color), text).toString());
      }
      else {
         write(text);
      }
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

   public static class TerminalString {

      public static final String START_COLOR = "\u001B[38;5;";
      public static final String END = "m";
      public static final String RESET = "\u001B[0" + END;

      private TerminalColor color;
      private String content;

      public TerminalString(TerminalColor color, String content) {
         this.color = color;
         this.content = content;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(START_COLOR)
            .append(color.toString())
            .append(END);
         sb.append(content);
         sb.append(RESET);
         return sb.toString();
      }
   }

   public static class TerminalColor {
      public Color color;

      public TerminalColor(Color color) {
         this.color = color;
      }

      @Override
      public String toString() {
         return String.valueOf(
               rgbToAnsi(
                     color.getRed(),
                     color.getGreen(),
                     color.getBlue()));
      }

      private int rgbToAnsi(int red, int green, int blue) {
         return 16 + (getAnsiScale(red) * 36) + (getAnsiScale(green) * 6) + getAnsiScale(blue);
      }

      public int getAnsiScale(int color) {
         int space = 256/5;
         if(color == 0) {
            return 0;
         }
         if(color < space*1) {
            return 1;
         }
         if( color > space*1 && color < space*2) {
            return 2;
         }
         if( color > space*2 && color < space*3) {
            return 3;
         }
         if( color > space*3 && color < space*4) {
            return 4;
         }
         if( color > space*4) {
            return 5;
         }
         return 0;
      }
   }
}