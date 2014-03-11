package org.forge.text.syntax;

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
      writeln("[" + text + "]:" + type);
      flush();
   }

   @Override
   public void beginGroup(TokenType type) {
      writeln("+" + type);
      flush();
   }

   @Override
   public void endGroup(TokenType type) {
      writeln("-" + type);
      flush();
   }

   @Override
   public void beginLine(TokenType type) {
      writeln("+ line");
      flush();
   }

   @Override
   public void endLine(TokenType type) {
      writeln("- line");
      flush();
   }
}
