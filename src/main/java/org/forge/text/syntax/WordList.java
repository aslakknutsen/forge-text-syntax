package org.forge.text.syntax;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordList<T> {

   private T defaultValue;
   private Map<T, List<String>> lists;
   
   public WordList(T defaultValue) {
      this.defaultValue = defaultValue;
      this.lists = new HashMap<T, List<String>>();
   }
   
   public WordList<T> add(String[] list, T type) {
      this.lists.put(type, Arrays.asList(list));
      return this;
   }
   
   public T lookup(String value) {
      for(Map.Entry<T, List<String>> entry : lists.entrySet()) {
         if(entry.getValue().contains(value)) {
            return entry.getKey();
         }
      }
      return defaultValue;
   }
}
