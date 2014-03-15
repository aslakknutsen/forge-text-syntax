package org.forge.text.syntax;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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
   
   public static final class Builder {

      private String scannerType;
      private Scanner scanner;
      private Map<String, Object> scannerOptions;

      private String encoderType;
      private Encoder encoder;
      private Map<String, Object> encoderOptions;

      private OutputStream output = System.out;

      private Theme theme = defaultTheme();

      private Builder() {}

      public static Builder create() {
         return new Builder();
      }

      public Builder scannerType(Scanner.Type scannerType) {
         return scannerType(scannerType.name());
      }

      public Builder scannerType(String scannerType) {
         this.scannerType = scannerType;
         return this;
      }

      public Builder scanner(Scanner scanner) {
         this.scanner = scanner;
         return this;
      }

      public Builder scannerOptions(Map<String, Object> options) {
         this.scannerOptions = options;
         return this;
      }

      public Builder encoderType(Encoder.Type encoderType) {
         return encoderType(encoderType.name());
      }

      public Builder encoderType(String encoderType) {
         this.encoderType = encoderType;
         return this;
      }

      public Builder encoder(Encoder encoder) {
         this.encoder = encoder;
         return this;
      }

      public Builder encoderOptions(Map<String, Object> options) {
         this.encoderOptions = options;
         return this;
      }

      public Builder output(OutputStream output) {
         this.output = output;
         return this;
      }

      public Builder theme(Theme theme) {
         this.theme = theme;
         return this;
      }

      public void execute(String source) {
         execute(new StringScanner(source));
      }

      public void execute(StringScanner source) {
         if(output == null && encoder == null) {
            throw new IllegalArgumentException("Either output or encoder must be defined");
         }

         Scanner in = scanner;
         if(scanner == null) {
            if(scannerType == null) {
               throw new IllegalArgumentException("Either input or inputType must be defined");
            }
            in = Scanner.Factory.create(scannerType);
         }
         Encoder out = encoder;
         if(encoder == null) {
            if(encoderType == null) {
               throw new IllegalArgumentException("Either output or outputType must be defined");
            }
            out = Encoder.Factory.create(encoderType, output, theme, encoderOptions == null ? Options.create():encoderOptions);
         }
         in.scan(source, out, scannerOptions == null ? Options.create():scannerOptions);
      }
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
      Builder.create()
         .scannerType(type)
         .encoderType(encoder)
         .output(out)
         .execute(content);

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
