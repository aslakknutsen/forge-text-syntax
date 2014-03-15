package org.forge.text.syntax.scanner;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.StringScanner;
import org.forge.text.syntax.TokenType;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/json.rb
 * Last update sha: b89caf96d1cfc304c2114d8734ebe8b91337c799
 */
public class JSONScanner implements Scanner {

   public static final Pattern ESCAPE = Pattern.compile("[bfnrt\\\\\"\\/]");
   public static final Pattern UNICODE_ESCAPE = Pattern.compile("u[a-fA-F0-9]{4}");
   public static final Pattern KEY = Pattern.compile("(?>(?:[^\\\\\"]+|\\\\.)*)\"\\s*:");
   
   public enum State {
      initial,
      key,
      string
   }
   
   @Override
   public void scan(StringScanner source, Encoder encoder, Map<String, Object> options) {
      State state = State.initial;
      
      while(source.hasMore()) {
         MatchResult m = null;
      
         switch (state) {
         
         case initial:
            if( (m = source.scan("\\s+")) != null ) {
               encoder.textToken(m.group(), TokenType.space);
            }
            else if( (m = source.scan("\"")) != null ) {
               state = source.check(KEY) != null ? State.key:State.string;
               encoder.beginGroup(TokenType.valueOf(state.name()));
               encoder.textToken(m.group(), TokenType.delimiter);
             }
            else if( (m = source.scan("[:,\\[{\\]}]")) != null ) {
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if( (m = source.scan("true|false|null")) != null ) {
               encoder.textToken(m.group(), TokenType.value);
            }
            else if( (m = source.scan("-?(?:0|[1-9]\\d*)")) != null ) {
               String match = m.group();
               if( (m = source.scan("\\.\\d+(?:[eE][-+]?\\d+)?|[eE][-+]?\\d+")) != null ) {
                  match = match + m.group();
                  encoder.textToken(match, TokenType.float_);
               }
               else {
                  encoder.textToken(match, TokenType.integer);
               }
            } else {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;
         case key:
         case string:
            
            if( (m = source.scan("[^\\\\\"]+")) != null ) {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if( (m = source.scan("\"")) != null ) {
               encoder.textToken(m.group(), TokenType.delimiter);
               encoder.endGroup(TokenType.valueOf(state.name()));
               state = State.initial;
            }
            else if( (m = source.scan(Pattern.compile("\\\\(?:" + ESCAPE.pattern()+ "|" + UNICODE_ESCAPE.pattern() + ")", Pattern.DOTALL))) != null ) {
               encoder.textToken(m.group(), TokenType.char_);
            }
            else if( (m = source.scan(Pattern.compile("\\\\.", Pattern.DOTALL))) != null ) {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if( (m = source.scan("\\\\|$")) != null ) {
               encoder.endGroup(TokenType.valueOf(state.name()));
               if(!m.group().isEmpty()) {
                  encoder.textToken(m.group(), TokenType.error);
               }
               state = State.initial;
            }
            else {
               throw new RuntimeException("else case \" reached "  + source.peek(1) + " not handled");
            }
            break;
         default:
            throw new RuntimeException("Unknown state " + state);
         }
      }
      if(state == State.key || state == State.string) {
         encoder.endGroup(TokenType.valueOf(state.name()));
      }
   }

}
