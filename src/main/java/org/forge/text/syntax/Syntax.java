package org.forge.text.syntax;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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
      Encoder encoder = Encoder.Factory.create(encoderType, out, new Theme(Color.RED));
      scanner.scan(new StringScanner(source), encoder);
   }
}
