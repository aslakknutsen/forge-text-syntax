package org.forge.text.syntax;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordList<T> {

   private T defaultValue;
   private Map<T, Set<String>> lists;
   
   public WordList(T defaultValue) {
      this.defaultValue = defaultValue;
      this.lists = new HashMap<T, Set<String>>();
   }
   
   public WordList<T> add(String[] list, T type) {
      this.lists.put(type, new HashSet<>(Arrays.asList(list)));
      return this;
   }
   
   public T lookup(String value) {
      for(Map.Entry<T, Set<String>> entry : lists.entrySet()) {
         if(entry.getValue().contains(value)) {
            return entry.getKey();
         }
      }
      return defaultValue;
   }
}
