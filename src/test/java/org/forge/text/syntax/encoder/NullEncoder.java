package org.forge.text.syntax.encoder;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.TokenType;

public class NullEncoder implements Encoder {

   @Override
   public void textToken(String text, TokenType type) {
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
}
