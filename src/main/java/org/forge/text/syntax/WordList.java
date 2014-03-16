package org.forge.text.syntax;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class WordList<T> {

   private T defaultValue;
   private Map<T, Set<String>> lists;
   private boolean caseInsensitive;
   
   public WordList(T defaultValue) {
      this(defaultValue, false);
   }

   public WordList(T defaultValue, boolean caseInsensitive) {
      this.defaultValue = defaultValue;
      this.caseInsensitive = caseInsensitive;
      this.lists = new LinkedHashMap<T, Set<String>>();
   }
   
   public WordList<T> add(String[] list, T type) {
      Set<String> words = new HashSet<>();
      if(caseInsensitive) {
         for(String word : list) {
            words.add(word.toLowerCase());
         }
      } else {
         words.addAll(Arrays.asList(list));
      }
      this.lists.put(type, words);
      return this;
   }
   
   public T lookup(String value) {
      String match = value;
      if(caseInsensitive) {
         match = match.toLowerCase();
      }

      for(Map.Entry<T, Set<String>> entry : lists.entrySet()) {
         if(entry.getValue().contains(match)) {
            return entry.getKey();
         }
      }
      return defaultValue;
   }
}
