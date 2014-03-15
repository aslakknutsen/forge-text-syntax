package org.forge.text.syntax.scanner;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.StringScanner;
import org.forge.text.syntax.Syntax;
import org.forge.text.syntax.TokenType;
import org.forge.text.syntax.WordList;

/*
 * Based on https://github.com/rubychan/coderay/blob/master/lib/coderay/scanners/html.rb
 * Last update sha: 8c3c0c49a98eb8daceb69d0b233d054fbbccc49e
 */
public class HTMLScanner implements Scanner {

   public static final String[] EVENT_ATTRIBUTES = new String[] {
                             "onabort", "onafterprint", "onbeforeprint", "onbeforeunload", "onblur", "oncanplay",
                             "oncanplaythrough", "onchange", "onclick", "oncontextmenu", "oncuechange", "ondblclick",
                             "ondrag", "ondragdrop", "ondragend", "ondragenter", "ondragleave", "ondragover",
                             "ondragstart", "ondrop", "ondurationchange", "onemptied", "onended", "onerror", "onfocus",
                             "onformchange", "onforminput", "onhashchange", "oninput", "oninvalid", "onkeydown",
                             "onkeypress", "onkeyup", "onload", "onloadeddata", "onloadedmetadata", "onloadstart",
                             "onmessage", "onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup",
                             "onmousewheel", "onmove", "onoffline", "ononline", "onpagehide", "onpageshow", "onpause",
                             "onplay", "onplaying", "onpopstate", "onprogress", "onratechange", "onreadystatechange",
                             "onredo", "onreset", "onresize", "onscroll", "onseeked", "onseeking", "onselect", "onshow",
                             "onstalled", "onstorage", "onsubmit", "onsuspend", "ontimeupdate", "onundo", "onunload", 
                             "onvolumechange", "onwaiting"};
   
   public static final Pattern ATTR_NAME = Pattern.compile("[\\w.:-]+");
   public static final Pattern TAG_END = Pattern.compile("\\/?>");
   public static final Pattern HEX = Pattern.compile("[0-9a-fA-F]");
   public static final Pattern ENTITY = Pattern.compile("&(?:\\w+|\\#(?:\\d+|x" + HEX.pattern() + "+));");
   
   
   public static final Map<String, Pattern> PLAIN_STRING_CONTENT = new HashMap<String, Pattern>(); 
   {
      PLAIN_STRING_CONTENT.put("'", Pattern.compile("[^&'>\\n]+"));
      PLAIN_STRING_CONTENT.put("\"", Pattern.compile("[^&\">\\n]+"));
   }       

   public enum EmbeddedType {
      script,
      style
   }
   
   public enum State {
      innitial,
      in_special_tag,
      attribute,
      attribute_equal,
      attribute_value,
      attribute_value_string
   }
   
   public static final WordList<EmbeddedType> IN_ATTRIBUTE = new WordList<EmbeddedType>(null)
                                                .add(EVENT_ATTRIBUTES, EmbeddedType.script)
                                                .add(new String[] {"style"}, EmbeddedType.style);
   
   @Override
   public void scan(StringScanner source, Encoder encoder) {
      State state = State.innitial;
      EmbeddedType in_attribute = null;
      String in_tag = null;
      Pattern plain_string_content = null;
      
      while(source.hasMore()) {
         MatchResult m = null;
      
         if( state != State.in_special_tag && (m = source.scan(Pattern.compile("\\s+", Pattern.DOTALL))) != null) {
            encoder.textToken(m.group(), TokenType.space);
         }
         else {
            
            switch (state) {
            case innitial:
               
               if( (m = source.scan("<!\\[CDATA\\[")) != null ) {
                  encoder.textToken(m.group(), TokenType.inline_delimiter);
                  if( (m = source.scan(Pattern.compile(".*?\\]\\]>", Pattern.DOTALL))) != null) {
                     encoder.textToken(m.group().substring(0, m.group().length()-4), TokenType.plain);
                     encoder.textToken("]]>", TokenType.inline_delimiter);
                  }
                  else if( (m = source.scan(".+")) != null ) {
                     encoder.textToken(m.group(), TokenType.error);
                  }
               }
               else if( (m = source.scan(Pattern.compile("<!--(?:.*?-->|.*)", Pattern.DOTALL))) != null ) {
                  encoder.textToken(m.group(), TokenType.comment);
               }
               else if( (m = source.scan(Pattern.compile("<!(\\w+)(?:.*?>|.*)|\\]>", Pattern.DOTALL))) != null ) {
                  encoder.textToken(m.group(), TokenType.doctype);
               }
               else if( (m = source.scan("<\\?xml(?:.*?\\?>|.*)")) != null ) {
                  encoder.textToken(m.group(), TokenType.preprocessor);
               }
               else if( (m = source.scan(Pattern.compile("<\\?(?:.*?\\?>|.*)", Pattern.DOTALL))) != null ) {
                  encoder.textToken(m.group(), TokenType.comment);
               }
               else if( (m = source.scan(Pattern.compile("<\\/[-\\w.:]*>?", Pattern.DOTALL))) != null ) {
                  in_tag = null;
                  encoder.textToken(m.group(), TokenType.tag);
               }
               else if( (m = source.scan(Pattern.compile("<(?:(script|style)|[-\\w.:]+)(>)?", Pattern.DOTALL))) != null ) {
                  encoder.textToken(m.group(), TokenType.tag);
                  in_tag = m.group(1);
                  if(m.group(2) != null) {
                     if(in_tag != null) {
                        state = State.in_special_tag;
                     }
                  }
                  else {
                     state = State.attribute;
                  }
               }
               else if( (m = source.scan("[^<>&]+")) != null ) {
                  encoder.textToken(m.group(), TokenType.plain);
               }
               else if( (m = source.scan(ENTITY)) != null ) {
                  encoder.textToken(m.group(), TokenType.entity);
               }
               else if( (m = source.scan("[<>&]")) != null ) {
                  in_tag = null;
                  encoder.textToken(m.group(), TokenType.error);
               }
               else {
                  throw new RuntimeException("[BUG] else-case reached with state " + state + " in " + getClass());
               }
               
               break;
            case attribute:
               
               if( (m = source.scan(TAG_END)) != null ) {
                  encoder.textToken(m.group(), TokenType.tag);
                  in_attribute = null;
                  if( in_tag != null) {
                     state = State.in_special_tag;
                  } else {
                     state = State.innitial;
                  }
               }
               else if( (m = source.scan(ATTR_NAME)) != null ) {
                  in_attribute = IN_ATTRIBUTE.lookup(m.group());
                  encoder.textToken(m.group(), TokenType.attribute_name);
                  state = State.attribute_equal;
               }
               else {
                  in_tag = null;
                  encoder.textToken(source.next(), TokenType.error);
               }
               
               break;
            case attribute_equal:
               
               if( (m = source.scan("=")) != null ) {
                  encoder.textToken(m.group(), TokenType.operator);
                  state = State.attribute_value;
               }
               else {
                  state = State.attribute;
               }
               break;

            case attribute_value:
               if( (m = source.scan(ATTR_NAME)) != null ) {
                 encoder.textToken(m.group(), TokenType.attribute_value);
                 state = State.attribute;
               } else if( (m = source.scan("[\"']")) != null ) {
                  if(EmbeddedType.script == in_attribute || EmbeddedType.style == in_attribute) {
                     encoder.beginGroup(TokenType.string);
                     encoder.textToken(m.group(), TokenType.delimiter);
                     String groupStart = m.group();

                     if( (m = source.scan("javascript:[ \\t]*")) != null ) {
                        encoder.textToken(m.group(), TokenType.comment);
                     }
                     // unsupported
                     String code = source.scanUntil(Pattern.compile("(?=" + groupStart + "|\\z)")).group();
                     if(EmbeddedType.style == in_attribute) {
                        Syntax.scan(code, Scanner.Type.CSS, encoder);
                     }

                     //if in_attribute == :script
                     // scan_java_script encoder, code
                     // else
                     // scan_css encoder, code, [:block]
                     //end
                     m = source.scan("[\"']");
                     if(m != null) {
                        encoder.textToken(m.group(), TokenType.delimiter);
                     }
                     encoder.endGroup(TokenType.string);
                     state = State.attribute;
                     in_attribute = null;
                  }
                  else {
                     encoder.beginGroup(TokenType.string);
                     state = State.attribute_value_string;
                     plain_string_content = PLAIN_STRING_CONTENT.get(m.group());
                     encoder.textToken(m.group(), TokenType.delimiter);
                  }
               }
               else if( (m = source.scan(TAG_END)) != null ) {
                  encoder.textToken(m.group(), TokenType.tag);
                  state = State.innitial;
               }
               else {
                  encoder.textToken(source.next(), TokenType.error);
               }
               break;
            case attribute_value_string:
               
               if( (m = source.scan(plain_string_content)) != null ) {
                  encoder.textToken(m.group(), TokenType.content);
               }
               else if( (m = source.scan("['\"]")) != null ) {
                  encoder.textToken(m.group(), TokenType.delimiter);
                  encoder.endGroup(TokenType.string);
                  state = State.attribute;
               }
               else if( (m = source.scan(ENTITY)) != null ) {
                  encoder.textToken(m.group(), TokenType.entity);
               }
               else if( (m = source.scan("&")) != null ) {
                  encoder.textToken(m.group(), TokenType.content);
               }
               else if( (m = source.scan("[\\n>]")) != null ) {
                  encoder.endGroup(TokenType.string);
                  state = State.innitial;
                  encoder.textToken(m.group(), TokenType.error);
               }
               break;
            case in_special_tag:
               
               if("script".equalsIgnoreCase(in_tag) || "style".equalsIgnoreCase(in_tag)) {
                  String code = null;
                  String closing = null;
                  if( (m = source.scan("[ \\t]*\\n")) != null ) {
                     encoder.textToken(m.group(), TokenType.space);
                  }
                  if( (m = source.scan(Pattern.compile("(\\s*<!--)(?:(.*?)(-->)|(.*))", Pattern.DOTALL))) != null ) {
                     code = m.group(2);
                     if(code == null) {
                        code = m.group(4);
                     }
                     closing = m.group(3);
                     encoder.textToken(m.group(1), TokenType.comment);
                  }
                  else {
                     // code = scan_until(/(?=(?:\n\s*)?<\/#{in_tag}>)|\z/)
                     closing = null;
                  }
                  if(code != null && !code.isEmpty()) {
                     encoder.beginGroup(TokenType.inline);
                     if("script".equalsIgnoreCase(in_tag)) {
                        //scan_java_script encoder, code
                     }
                     else {
                        //scan_css encoder, code
                     }
                     encoder.endGroup(TokenType.inline);
                  }
                  if(closing != null) {
                     encoder.textToken(closing, TokenType.comment);
                  }
                  state = State.innitial;
               }
               else {
                  throw new RuntimeException("unknown special tag " + in_tag);
               }
               break;
            default:
               throw new RuntimeException("Unknown state " + state);
            }
         }
      }
   }

}
