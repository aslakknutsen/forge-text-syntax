package org.forge.text.syntax;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringScanner {

   private StringSequence sequence;
         
   public StringScanner(String source) {
      this.sequence = new StringSequence(source);
   }
   
   public MatchResult scan(String pattern) {
      return scan(Pattern.compile(pattern));
   }
   
   public MatchResult scan(Pattern pattern) {
      Matcher m = pattern.matcher(sequence);
      if(m.lookingAt()) {
         MatchResult result = new StaticMatchResult(sequence, m);
         sequence.advance(m.end());
         return result;
      }
      return null;
   }
   
   public MatchResult scanUntil(String pattern) {
      return scanUntil(Pattern.compile(pattern));
   }

   public MatchResult scanUntil(Pattern pattern) {
      Matcher m = pattern.matcher(sequence);
      if(m.find()) {
         MatchResult result = new UntilStaticMatchResult(sequence, m);
         sequence.advance(m.end());
         return result;
      }
      return null;
   }

   public MatchResult check(String pattern) {
      return check(Pattern.compile(pattern));
   }

   public MatchResult check(Pattern pattern) {
      Matcher m = pattern.matcher(sequence);
      if(m.lookingAt()) {
         return new StaticMatchResult(sequence, m);
      }
      return null;
   }

   public String next() {
      return sequence.pop();
   }
   
   public boolean hasMore() {
      return sequence.hasMore();
   }
   
   public String peek(int length) {
      return sequence.peek(length);
   }

   private static class StaticMatchResult implements MatchResult {

      protected StringSequence sequence;
      protected int previousIndex;
      protected MatchResult originalMatch;

      public StaticMatchResult(StringSequence sequence, MatchResult result) {
         this.originalMatch = result;
         this.sequence = sequence;
         this.previousIndex = sequence.index();
      }

      @Override
      public int end() {
         return originalMatch.end();
      }

      @Override
      public int end(int group) {
         return originalMatch.end(group);
      }

      @Override
      public int groupCount() {
         return originalMatch.groupCount();
      }

      @Override
      public int start() {
         return originalMatch.start();
      }

      @Override
      public int start(int group) {
         return originalMatch.start(group);
      }

      @Override
      public String group() {
         int start = originalMatch.start();
         if(start == -1) {
            return null;
         }
         int end = originalMatch.end();
         return (String)sequence.subSequence(previousIndex, start, end);
      }

      @Override
      public String group(int group) {
         int start = originalMatch.start(group);
         if(start == -1) {
            return null;
         }
         int end = originalMatch.end(group);
         return (String)sequence.subSequence(previousIndex, start, end);
      }
   }

   /*
    * group(0) start is always 0. We want from previous up til next, not from next.
    *
    */
   private static class UntilStaticMatchResult extends StaticMatchResult {

      public UntilStaticMatchResult(StringSequence sequence, MatchResult result) {
         super(sequence, result);
      }

      @Override
      public int start() {
         return 0;
      }

      @Override
      public int start(int group) {
         if(group == 0) {
            return 0;
         }
         return originalMatch.start(group);
      }

      @Override
      public String group() {
         int start = 0;
         int end = originalMatch.end();
         return (String)sequence.subSequence(previousIndex, start, end);
      }

      @Override
      public String group(int group) {
         int start = originalMatch.start(group);
         if(start == -1) {
            return null;
         }
         if(group == 0) {
            start = 0;
         }
         int end = originalMatch.end(group);
         return (String)sequence.subSequence(previousIndex, start, end);
      }
   }

   private static class StringSequence implements CharSequence {
      private String source;

      private int index;

      public StringSequence(String source) {
         this.source = source;
         this.index = 0;
      }

      @Override
      public int length() {
         return source.length()-index;
      }

      @Override
      public char charAt(int index) {
         return source.charAt(this.index + index);
      }

      @Override
      public CharSequence subSequence(int start, int end) {
         return source.subSequence(this.index + start, this.index + end);
      }

      public CharSequence subSequence(int index, int start, int end) {
         return source.subSequence(index + start, index + end);
      }

      public String peek(int length) {
         return source.substring(this.index, this.index + length);
      }

      public String pop() {
         return String.valueOf(source.charAt(this.index++));
      }

      public int index() {
         return this.index;
      }

      public void advance(int length) {
         this.index = this.index+length;
      }

      public boolean hasMore() {
         return this.index < source.length();
      }
   }
}