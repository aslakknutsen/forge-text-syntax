package org.forge.text.syntax.scanner;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.StringScanner;
import org.forge.text.syntax.TokenType;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/css.rb
 * Last update sha: 70c9ba896e1bba5ac727fb6fdfc3ba94510e652d
 *
 */
public class CSSScanner implements Scanner {

   public static final Pattern HEX = Pattern.compile("[0-9a-fA-F]");
   public static final Pattern UNICODE = Pattern.compile("\\\\" + HEX.pattern() + "{1,6}\\b");
   public static final Pattern ESCAPE = Pattern.compile(UNICODE.pattern() + "|\\\\[^\\n0-9a-fA-F]");
   public static final Pattern NMChar = Pattern.compile("[-_a-zA-Z0-9]");
   public static final Pattern NMStart = Pattern.compile("[_a-zA-Z]");

   public static final Pattern String1 = Pattern.compile("\"(?:[^\\n\\\\\"]+|\\\\\\n|" + ESCAPE.pattern() + ")*\"?");
   public static final Pattern String2 = Pattern.compile("'(?:[^\\n\\\\']+|\\\\\\n|" + ESCAPE.pattern() + ")*'?");
   public static final Pattern String = Pattern.compile(String1.pattern() + "|" + String2.pattern()); 

   public static final Pattern HexColor = Pattern.compile("#(?:" + HEX.pattern() + "{6}|" + HEX.pattern() + "{3})");
   public static final Pattern Num = Pattern.compile("-?(?:[0-9]*\\.[0-9]+|[0-9]+)n?");
   public static final Pattern Name = Pattern.compile(NMChar.pattern() + "+");
   public static final Pattern Ident = Pattern.compile("-?" + NMStart.pattern() + NMChar.pattern() + "*");
   public static final Pattern AtKeyword = Pattern.compile("@" + Ident.pattern());
   public static final Pattern Percentage = Pattern.compile(Num.pattern() + "%");

   public static final List<String> reldimensions = Arrays.asList("em", "ex", "px");
   public static final List<String> absdimensions = Arrays.asList("in", "cm", "mm", "pt", "pc");
   public static final List<String> stuff = Arrays.asList("s", "dpi", "dppx", "deg");
   
   @SuppressWarnings("unchecked")
   public static final Pattern Unit = union(reldimensions, absdimensions, stuff);

   @SuppressWarnings("unchecked")
   public static Pattern union(List<String>... strings) {
      StringBuilder p = new StringBuilder();
      for(List<String> string : strings) {
         for(String str : string) {
            p.append(str).append("|");
         }
      }
      return Pattern.compile(p.deleteCharAt(p.length()-1).toString());
   }
   
   public static final Pattern Dimension = Pattern.compile(Num.pattern() + Unit.pattern());
   public static final Pattern Function = Pattern.compile("(?:url|alpha|attr|counters?)\\((?:[^)\\n]|\\\\\\))*\\)?");
   public static final Pattern Id = Pattern.compile("(?!" + HexColor.pattern() + "\\b(?!-))#" + Name.pattern()); 
   public static final Pattern Class = Pattern.compile("\\." + Name.pattern());
   public static final Pattern PseudoClass = Pattern.compile("::?" + Ident.pattern());
   public static final Pattern AttributeSelector = Pattern.compile("\\[[^\\]]*\\]?");

   public enum State {
      initial,
      media,
      media_before_name,
      media_after_name,
      block
   }

   @Override
   public void scan(StringScanner source, Encoder encoder) {
      boolean value_expected = false;
      Stack<State> state = new Stack<State>();
      state.push(State.initial);
      
      while(source.hasMore()) {
         Matcher m = null;
      
         if( (m = source.scan("\\s+")) != null) {
            encoder.textToken(m.group(), TokenType.space);
         }
         else if(media_blocks(source, encoder, value_expected, state)) {

         }
         else if( (m = source.scan(Pattern.compile("\\/\\*(?:.*?\\*\\/|\\z)", Pattern.DOTALL))) != null ){
            encoder.textToken(m.group(), TokenType.comment);
         }
         else if( (m = source.scan("\\{")) != null ){
            value_expected = false;
            encoder.textToken(m.group(), TokenType.operator);
            state.push(State.block);
         }
         else if( (m = source.scan("\\}")) != null ){
            value_expected = false;
            encoder.textToken(m.group(), TokenType.operator);
            if(state.peek() == State.block || state.peek() == State.media) {
               state.pop();
            }
         }
         else if( (m = source.scan(String)) != null ){
            encoder.beginGroup(TokenType.string);
            encoder.textToken(m.group().substring(0,  1), TokenType.delimiter);
            if(m.group().length() > 2) {
               encoder.textToken(m.group().substring(1, m.group().length()-2), TokenType.content);
            }
            if(m.group().length() >= 2) {
               encoder.textToken(m.group().substring(m.group().length()-1), TokenType.delimiter);
            }
            encoder.endGroup(TokenType.string);
         }
         else if( (m = source.scan(Function)) != null ){
            encoder.beginGroup(TokenType.function);
            Matcher functionMatcher = Pattern.compile("^\\w+\\(").matcher(m.group());
            functionMatcher.lookingAt();
            String start = functionMatcher.group();
            encoder.textToken(start, TokenType.delimiter);
            if(m.group().substring(m.group().length()-1).matches(".?\\)")) {
               if(m.group().length() > start.length()+1) {
                  encoder.textToken(m.group().substring(start.length(), m.group().length()-1), TokenType.content);
                  encoder.textToken(")", TokenType.delimiter);
               }
            }
            else if(m.group().length() > start.length()) {
               encoder.textToken(m.group().substring(start.length(), m.group().length()-1), TokenType.content);
            }
            encoder.endGroup(TokenType.function);
         }
         else if( (m = source.scan("(?:" + Dimension.pattern() + "|" + Percentage.pattern() + "|" + Num.pattern() + ")")) != null ){
            encoder.textToken(m.group(), TokenType.float_);
         }
         else if( (m = source.scan(HexColor)) != null ){
            encoder.textToken(m.group(), TokenType.color);
         }
         else if( (m = source.scan("! *important")) != null ){
            encoder.textToken(m.group(), TokenType.important);
         }
         else if( (m = source.scan("(?:rgb|hsl)a?\\([^()\\n]*\\)?")) != null ){
            encoder.textToken(m.group(), TokenType.color);
         }
         else if( (m = source.scan(AtKeyword)) != null ){
            encoder.textToken(m.group(), TokenType.directive);
         }
         else if( (m = source.scan("[+>~:;,.=()\\/]")) != null ){
            if(":".equals(m.group())) {
               value_expected = true;
            } else if(";".equals(m.group())) {
               value_expected = false;
            }
            encoder.textToken(m.group(), TokenType.operator);
         }
         else {
            encoder.textToken(source.next(), TokenType.error);
         }
      }   
   }

   private boolean media_blocks(StringScanner source, Encoder encoder, boolean value_expected, Stack<State> state) {
      Matcher m;
      switch (state.peek()) {
      
      case initial:
      case media:
         if( (m = source.scan("(?>" + Ident.pattern() + ")(?!\\()|\\*")) != null ) {
            encoder.textToken(m.group(), TokenType.tag);
            return true;
         }
         else if( (m = source.scan(Class)) != null ) {
            encoder.textToken(m.group(), TokenType.class_);
            return true;
         }
         else if( (m = source.scan(Id)) != null ) {
            encoder.textToken(m.group(), TokenType.id);
            return true;
         }
         else if( (m = source.scan(PseudoClass)) != null ) {
            encoder.textToken(m.group(), TokenType.pseudo_class);
            return true;
         }
         else if( (m = source.scan(AttributeSelector)) != null ) {
            encoder.textToken(m.group().substring(0, 1), TokenType.operator);
            if(m.group().length() > 2) {
               encoder.textToken(m.group().substring(1, m.group().length()-2), TokenType.attribute_name);
            }
            if(m.group().substring(m.group().length()-1).matches(".?\\]")) {
               encoder.textToken(m.group().substring(m.group().length()-1), TokenType.operator);
            }
            return true;
         }
         else if( (m = source.scan("@media")) != null ) {
            encoder.textToken(m.group(), TokenType.directive);
            state.push(State.media_before_name);
            return true;
         }
         break;
      
      case block:
         if( (m = source.scan("(?>" + Ident.pattern() + ")(?!\\()")) != null ) {
            if (value_expected) {
               encoder.textToken(m.group(), TokenType.value);
            } else {
               encoder.textToken(m.group(), TokenType.key);
            }
            return true;
         }
         break;
      
      case media_before_name:
         if( (m = source.scan(Ident)) != null ) {
            encoder.textToken(m.group(), TokenType.type);
            state.pop();
            state.push(State.media_after_name);
            return true;
         }
         break;
      
      case media_after_name:
         if( (m = source.scan("\\{")) != null ) {
            encoder.textToken(m.group(), TokenType.operator);
            state.pop();
            state.push(State.media);
            return true;
         }
         break;

      default:
         throw new RuntimeException("Unknown state " + state);
      }
      return false;
   }
}
