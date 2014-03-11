package org.forge.text.syntax;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringScanner {

   private String source;
   private int index = 0;
         
   public StringScanner(String source) {
      this.source = source;
   }
   
   public Matcher scan(String pattern) {
      return scan(Pattern.compile(pattern));
   }
   
   public Matcher scan(Pattern pattern) {
      Matcher m = pattern.matcher(source.substring(index));
      if(m.lookingAt()) {
         index += m.end();
         return m;
      }
      return null;
   }
   
   public Matcher check(String pattern) {
      return check(Pattern.compile(pattern));
   }

   public Matcher check(Pattern pattern) {
      Matcher m = pattern.matcher(source.substring(index));
      if(m.lookingAt()) {
         return m;
      }
      return null;
   }

   public String next() {
      return String.valueOf(source.charAt(index++));
   }
   
   public boolean hasMore() {
      return index < source.length();
   }
   
   public String peek(int length) {
      return source.substring(index, index + length);
   }
   
   public int pos() {
      return index;
   }
}
