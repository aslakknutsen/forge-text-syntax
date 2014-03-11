package org.forge.text.syntax.scanner;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.StringScanner;
import org.forge.text.syntax.TokenType;
import org.forge.text.syntax.WordList;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/java.rb
 * Last update sha: 1cdf0e17af6c280dc12130a9200d8196b056bbe9
 */
public class JavaScanner implements Scanner {

   private enum Patterns {
      SPACE("\\s+|\\n"),
      COMMENT("[^\\n\\\\]* (?: \\\\. [^\\n\\\\]* )* | /\\* (?: .*? \\*/ | .* ) !mx"),
      IDENT("[a-zA-Z_][A-Za-z_0-9]*"),
      OPERATORS("\\.(?!\\d)|[,?:()\\[\\]}]|--|\\+\\+|&&|\\|\\||\\*\\*=?|[-+*\\/%^~&|<>=!]=?|<<<?=?|>>>?=?"),
      STRING_CONTENT_PATTERN_SINGLE("[^\\\\']+"),
      STRING_CONTENT_PATTERN_DOUBLE("[^\\\\\"]+"),
      STRING_CONTENT_PATTERN_MULTI_LINE("[^\\\\\\/]+"),
      ESCAPE("[bfnrtv\\n\\\\'\"]|x[a-fA-F0-9]{1,2}|[0-7]{1,3}"),
      UNICODE_ESCAPE("u[a-fA-F0-9]{4}|U[a-fA-F0-9]{8}");
      
      private Pattern pattern;
      Patterns(String pattern) {
         this.pattern = Pattern.compile(pattern);
      }
      Patterns(String pattern, int options) {
         this.pattern = Pattern.compile(pattern, options);
      }
   }
   
   public enum State {
      initial,
      string
   }
   
   public static final String[] KEYWORDS = new String[] {
                                             "assert", "break", "case", "catch", "continue", "default", "do", "else",
                                             "finally", "for", "if", "instanceof", "import", "new", "package",
                                             "return", "switch", "throw", "try", "typeof", "while", "debugger", "export"};
   public static final String[] RESERVED = new String[] {
                                             "const", "goto"};
   public static final String[] CONSTANTS = new String[] {
                                             "false", "null", "true"};
   public static final String[] MAGIC_VARIABLES = new String[] {
                                             "this", "super"};
   public static final String[] TYPES = new String[] {
                                             "boolean", "byte", "char", "class", "double", "enum", "float", "int",
                                             "interface", "long", "short", "void"}; // missing int[]
   public static final String[] DIRECTIVES = new String[] {
                                             "abstract", "extends", "final", "implements", "native", "private",
                                             "protected", "public", "static", "strictfp", "synchronized", "throws",
                                             "transient", "volatile"};
   
   public static final WordList<TokenType> IDENT_KIND = new WordList<TokenType>(TokenType.ident)
                                             .add(KEYWORDS, TokenType.keyword)
                                             .add(RESERVED, TokenType.reserved)
                                             .add(CONSTANTS, TokenType.predefined_constant)
                                             .add(MAGIC_VARIABLES, TokenType.local_variable)
                                             .add(TYPES, TokenType.type)
                                             //add(BuiltinTypes::List, :predefined_type).
                                             //add(BuiltinTypes::List.select { |builtin| builtin[/(Error|Exception)$/] }, :exception).
                                             .add(DIRECTIVES, TokenType.directive);

   public static final Map<String, Pattern> STRING_CONTENT_PATTERN = new HashMap<String, Pattern>(); 
   {
      STRING_CONTENT_PATTERN.put("'", Patterns.STRING_CONTENT_PATTERN_SINGLE.pattern);
      STRING_CONTENT_PATTERN.put("\"", Patterns.STRING_CONTENT_PATTERN_DOUBLE.pattern);
      STRING_CONTENT_PATTERN.put("/", Patterns.STRING_CONTENT_PATTERN_MULTI_LINE.pattern);
   }       
   
   @Override
   public void scan(StringScanner source, Encoder encoder) {
      State state = State.initial;
      String string_delimiter = null;
      TokenType package_name_expected = null;
      boolean class_name_follows = false;
      boolean last_token_dot = false;
      
      while(source.hasMore()) {
         Matcher m = null;
         
         switch (state) {
         case initial:
            if( (m = source.scan(Patterns.SPACE.pattern)) != null) {
               encoder.textToken(m.group(), TokenType.space);
               continue;
            }
            else if( (m = source.scan(Patterns.COMMENT.pattern)) != null) {
               encoder.textToken(m.group(), TokenType.comment);
               continue;
            }
            else if( package_name_expected != null && (m = source.scan(
                  Pattern.compile(Patterns.IDENT.pattern.pattern() + "(?:\\." + Patterns.IDENT.pattern.pattern()+ "})*"))) != null) {
               encoder.textToken(m.group(), package_name_expected);
            }
            else if( (m = source.scan(Pattern.compile(Patterns.IDENT.pattern.pattern() + "|\\[\\]"))) != null) {
               String match = m.group();
               TokenType kind = IDENT_KIND.lookup(match);
               if(last_token_dot) {
                  kind = TokenType.ident;
               } else if(class_name_follows) {
                  kind = TokenType.class_;
                  class_name_follows = false;
               } else {
                  if("import".equals(match)) {
                     package_name_expected = TokenType.include;
                  } else if("package".equals(match)) {
                     package_name_expected = TokenType.namespace;
                  } else if("class".equals(match) || "interface".equals(match)) {
                     class_name_follows = true;
                  }
               }
               encoder.textToken(match, kind);
            }
            else if( (m = source.scan(Patterns.OPERATORS.pattern)) != null ) {
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if( (m = source.scan(";")) != null) {
               package_name_expected = null;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if( (m = source.scan("\\{")) != null) {
               class_name_follows = false;
               encoder.textToken(m.group(), TokenType.operator);
            }
            else if( (m = source.check("[\\d.]")) != null ) {
               if( (m = source.scan("0[xX][0-9A-Fa-f]+")) != null) {
                  encoder.textToken(m.group(), TokenType.hex);
               }
               else if( (m = source.scan("(?>0[0-7]+)(?![89.eEfF])")) != null) {
                  encoder.textToken(m.group(), TokenType.octal);
               }
               else if( (m = source.scan("\\d+[fFdD]|\\d*\\.\\d+(?:[eE][+-]?\\d+)?[fFdD]?|\\d+[eE][+-]?\\d+[fFdD]?")) != null) {
                  encoder.textToken(m.group(), TokenType.float_);
               }
               else if( (m = source.scan("\\d+[lL]?")) != null) {
                  encoder.textToken(m.group(), TokenType.integer);
               }
            }
            else if( (m = source.scan("[\"']")) != null) {
               state = State.string;
               encoder.beginGroup(TokenType.string);
               string_delimiter = m.group();
               encoder.textToken(m.group(), TokenType.delimiter);
            }
            else if( (m = source.scan("@" + Patterns.IDENT.pattern)) != null ) {
               encoder.textToken(m.group(), TokenType.annotation);
            }
            else {
               encoder.textToken(source.next(), TokenType.error);
            }
            break;
         case string:
            if( (m = source.scan(STRING_CONTENT_PATTERN.get(string_delimiter))) != null ) {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if( (m = source.scan("[\"'\\/]")) != null ) {
               encoder.textToken(m.group(), TokenType.delimiter);
               encoder.endGroup(TokenType.string);
               state = State.initial;
               string_delimiter = null;
            }
            else if( state == State.string && (m = source.scan(
                  "/\\\\(?:" + Patterns.ESCAPE.pattern.pattern() +"|" + Patterns.UNICODE_ESCAPE.pattern.pattern() + ")/m")) != null) {
               if("'".equals(string_delimiter) && !("\\\"".equals(m.group()) || "\\'".equals(m.group())) ) {
                  encoder.textToken(m.group(), TokenType.content);
               } else {
                  encoder.textToken(m.group(), TokenType.char_);
               }
            }
            else if( (m = source.scan("\\\\./m")) != null ) {
               encoder.textToken(m.group(), TokenType.content);
            }
            else if( (m = source.scan("\\\\|$")) != null ) {
               encoder.endGroup(TokenType.string);
               state = State.initial;
               if(!m.group().isEmpty()) {
                  encoder.textToken(m.group(), TokenType.error);
               }
            }
            else {
               throw new RuntimeException("else case \" reached; " + source.peek(1) + " in " + getClass());
            }
            break;
         default:
            throw new RuntimeException("unknown state " + state);
         }
         if(m != null) {
            last_token_dot = (".".equals(m.group()));
         }
      }
      if(state == State.string) {
         encoder.endGroup(TokenType.string);
      }
   }
}
