package org.forge.text.syntax;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringScanner {

   StringSequence sequence;
         
   public StringScanner(String source) {
      this.sequence = new StringSequence(source);
   }
   
   public MatchResult scan(String pattern) {
      return scan(Pattern.compile(pattern));
   }
   
   public MatchResult scan(Pattern pattern) {
      Matcher m = pattern.matcher(sequence);
      if(m.lookingAt()) {
         MatchResult result = StaticMatchResult.freeze(m, sequence);
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
         MatchResult result = StaticMatchResult.freezeFrom(m, sequence);
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
         return StaticMatchResult.freeze(m, sequence);
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

   public static class StaticMatchResult implements MatchResult {

      private StringSequence sequence;
      private int previousIndex;
      private int[][] groups;

      private StaticMatchResult(StringSequence sequence, int[][] groups) {
         this.groups = groups;
         this.sequence = sequence;
         this.previousIndex = sequence.index();
      }

      public static MatchResult freeze(Matcher m, StringSequence sequence) {
         int[][] groups = new int[1+m.groupCount()][2];
         groups[0]  = new int[] {m.start(), m.end()};
         for(int i = 0; i < m.groupCount(); i++) {
            groups[i+1] = new int[]{m.start(i), m.end(i)};
         }
         return new StaticMatchResult(sequence, groups);
      }

      public static MatchResult freezeFrom(Matcher m, StringSequence sequence) {
         int[][] groups = new int[1+m.groupCount()][2];
         // we want until, so set start to 0
         groups[0]  = new int[] {0, m.end()};
         for(int i = 0; i < m.groupCount(); i++) {
            groups[i+1] = new int[]{m.start(i), m.end(i)};
         }
         return new StaticMatchResult(sequence, groups);
      }

      @Override
      public int start() {
         return groups[0][0];
      }

      @Override
      public int start(int group) {
         return groups[group][0];
      }

      @Override
      public int end() {
         return groups[0][1];
      }

      @Override
      public int end(int group) {
         return groups[group][1];
      }

      @Override
      public String group() {
         int[] pos = groups[0];
         return sequence.subSequence(previousIndex, pos[0], pos[1]).toString();
      }

      @Override
      public String group(int group) {
         int[] pos = groups[group];
         try {
            return sequence.subSequence(previousIndex, pos[0], pos[1]).toString();
         } catch(StringIndexOutOfBoundsException e) {
            return null; // group was never found
         }
      }

      @Override
      public int groupCount() {
         return groups.length-1;
      }
   }

   public static class StringSequence implements CharSequence {
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