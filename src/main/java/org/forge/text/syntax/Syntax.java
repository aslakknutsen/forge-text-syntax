package org.forge.text.syntax;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.forge.text.syntax.encoder.DebugEncoder;
import org.forge.text.syntax.encoder.TerminalEncoder;
import org.forge.text.syntax.scanner.CSSScanner;
import org.forge.text.syntax.scanner.HTMLScanner;
import org.forge.text.syntax.scanner.JSONScanner;
import org.forge.text.syntax.scanner.JavaScanner;

public class Syntax {

   static {
      Scanner.Factory.registrer(Scanner.Type.JAVA.name(), JavaScanner.class);
      Scanner.Factory.registrer(Scanner.Type.HTML.name(), HTMLScanner.class);
      Scanner.Factory.registrer(Scanner.Type.CSS.name(), CSSScanner.class);
      Scanner.Factory.registrer(Scanner.Type.JSON.name(), JSONScanner.class);
      
      Encoder.Factory.registrer(Encoder.Type.TERMINAL.name(), TerminalEncoder.class);
      Encoder.Factory.registrer(Encoder.Type.DEBUG.name(), DebugEncoder.class);
   }
   
   public static String scan(String source, Scanner.Type scannerType, Encoder.Type encoderType) {
      return scan(source, scannerType.name(), encoderType.name());
   }

   public static String scan(String source, String scannerType, String encoderType) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      scan(source, scannerType, encoderType, out);
      return out.toString();
   }

   public static void scan(String source, Scanner.Type scannerType, Encoder.Type encoderType, OutputStream out) {
      scan(source, scannerType.name(), encoderType.name(), out);
   }

   public static void scan(String source, String scannerType, String encoderType, OutputStream out) {
      Scanner scanner = Scanner.Factory.create(scannerType);
      Encoder encoder = Encoder.Factory.create(encoderType, out, defaultTheme());
      scanner.scan(new StringScanner(source), encoder);
   }

   public static void scan(String source, Scanner.Type scannerType, Encoder encoder) {
      scan(new StringScanner(source), scannerType, encoder);
   }

   public static void scan(StringScanner source, Scanner.Type scannerType, Encoder encoder) {
      Scanner scanner = Scanner.Factory.create(scannerType.name());
      scanner.scan(source, encoder);
   }

   public static Theme defaultTheme() {
      return new Theme(Color.WHITE)
         .set(Color.RED, TokenType.predefined_constant, TokenType.content, TokenType.delimiter, TokenType.color, TokenType.value, TokenType.integer, TokenType.float_)
         .set(Color.CYAN, TokenType.tag, TokenType.class_, TokenType.function)
         .set(Color.MAGENTA, TokenType.keyword)
         .set(Color.GREEN, TokenType.type, TokenType.directive, TokenType.string, TokenType.attribute_value, TokenType.attribute_name, TokenType.key);
   }

   public static void main(String[] args) {
      if(args.length < 1) {
         System.out.println("Usage: java -jar forge-text-syntax.jar file-name");
      }

      Encoder.Type encoder = Encoder.Type.TERMINAL;
      String fileName = args[0];
      if(args.length == 2) {
         encoder = Encoder.Type.DEBUG;
      }
      Scanner.Type type = determineType(fileName);
      if(type == null) {
         throw new RuntimeException("Could not determine scanner type based on filename " + fileName);
      }

      String content = null;
      try {
         content = new String(Files.readAllBytes(Paths.get(fileName)));
      } catch(IOException e) {
         throw new RuntimeException("Could not read given file " + fileName, e);
      }

      BufferedOutputStream out = new BufferedOutputStream(System.out);
      scan(content, type, encoder, out);
      try {
         out.flush();
      } catch (IOException e) { }
   }

   private static Scanner.Type determineType(String fileName) {
      if(fileName.matches(".*\\.(html|xhtml|xml)$")) {
         return Scanner.Type.HTML;
      }
      else if(fileName.matches(".*\\.(css)$")) {
         return Scanner.Type.CSS;
      }
      else if(fileName.matches(".*\\.(java)$")) {
         return Scanner.Type.JAVA;
      }
      else if(fileName.matches(".*\\.(json)$")) {
         return Scanner.Type.JSON;
      }
      return null;
   }
}
