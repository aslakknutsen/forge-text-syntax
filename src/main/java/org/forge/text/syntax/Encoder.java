package org.forge.text.syntax;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public interface Encoder {

   public enum Type { TERMINAL, DEBUG }

   void textToken(String text, TokenType type);
   void beginGroup(TokenType type);
   void endGroup(TokenType type);
   void beginLine(TokenType type);
   void endLine(TokenType type);
   
   public static abstract class AbstractEncoder implements Encoder {
      public static final String NEW_LINE = System.getProperty("line.separator");
      
      protected OutputStream out;
      protected Theme theme;
      
      public AbstractEncoder(OutputStream out, Theme theme) {
         this.out = out;
         this.theme = theme;
      }
      
      protected Color color(TokenType type) {
         return this.theme.lookup(type);
      }
      
      protected void write(String str) {
         try {
            out.write(str.getBytes());
         } catch(IOException e) {
            throw new RuntimeException("Could not write to output", e);
         }
      }
      protected void writeln(String str) {
         try {
            out.write(str.getBytes());
            out.write(NEW_LINE.getBytes());
         } catch(IOException e) {
            throw new RuntimeException("Could not write to output", e);
         }
      }
      protected void newLine() {
         try {
            out.write(NEW_LINE.getBytes());
         } catch(IOException e) {
            throw new RuntimeException("Could not write to output", e);
         }
      }
      protected void flush() {
         try {
            out.flush();
         } catch(IOException e) {
            throw new RuntimeException("Could not flush", e);
         }
      }
   }
   
   public static class Factory {
      private static Factory factory;
      
      private Map<String, Class<? extends Encoder>> registry;
      
      private Factory() {
         this.registry = new HashMap<String, Class<? extends Encoder>>();
      }
      
      private static Factory instance() {
         if(factory == null) {
            factory = new Factory();
         }
         return factory;
      }

      public static void registrer(String type, Class<? extends Encoder> encoder) {
         instance().registry.put(type, encoder); 
      }
      
      public static Encoder create(String type, OutputStream out, Theme theme) {
         Class<? extends Encoder> encoder = instance().registry.get(type); 
         if(encoder != null) {
            try {
               Constructor<? extends Encoder> constructor = encoder.getConstructor(OutputStream.class, Theme.class);
               return constructor.newInstance(out, theme);
            } catch(Exception e) {
               throw new RuntimeException("Could not create new instance of " + encoder);
            }
         }
         throw new RuntimeException("No encoder found for type " + type);
      }
   }
}
