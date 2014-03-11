package org.forge.text.syntax;

import java.util.HashMap;
import java.util.Map;

public interface Scanner {
   
   public enum Type { JAVA, HTML, CSS, JSON }
   
   void scan(StringScanner source, Encoder encoder);
   
   public static class Factory {
      private static Factory factory;
      
      private Map<String, Class<? extends Scanner>> registry;
      
      private Factory() {
         this.registry = new HashMap<String, Class<? extends Scanner>>();
      }
      
      private static Factory instance() {
         if(factory == null) {
            factory = new Factory();
         }
         return factory;
      }

      public static void registrer(String type, Class<? extends Scanner> scanner) {
         instance().registry.put(type, scanner); 
      }
      
      public static Scanner create(String type) {
         Class<? extends Scanner> encoder = instance().registry.get(type); 
         if(encoder != null) {
            try {
               return encoder.newInstance();
            } catch(Exception e) {
               throw new RuntimeException("Could not create new instance of " + encoder);
            }
         }
         throw new RuntimeException("No scanner found for type " + type);
      }
   }
}
