package org.forge.text.syntax.scanner;

import java.util.HashMap;
import java.util.Map;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.StringScanner;
import org.forge.text.syntax.Syntax.Builder;
import org.forge.text.syntax.encoder.NullEncoder;
import org.junit.Ignore;
import org.junit.Test;

public class JavaScriptScannerTestCase extends AbstractScannerTestCase {

   @Test
   public void shoulMatchJavaScriptEtienneMassipExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "etienne-massip.in.js");
   }

   @Test
   public void shoulMatchJavaScriptGordonExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "gordon.in.js");
   }

   @Test
   public void shoulMatchJavaScriptPrototypeExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "prototype.in.js");
   }

   @Test
   public void shoulMatchJavaScriptReadabilityExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "readability.in.js");
   }

   @Test
   public void shoulMatchJavaScriptScriptAculoUSExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "script.aculo.us.in.js");
   }

   @Test
   public void shoulMatchJavaScriptSunSpiderExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "sun-spider.in.js");
   }

   @Test
   public void shoulMatchJavaScriptTraceTestExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "trace-test.in.js");
   }

   @Test @Ignore // known issue http://redmine.rubychan.de/issues/137 https://github.com/rubychan/coderay-scanner-tests/blob/master/javascript/xml.known-issue.yaml
   public void shoulMatchJavaScriptXMLExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", " xml.in.js");
   }

   /*
    * JDK 1.7.0_51 -> stable around 85-86 ms
    * JDK 1.8.0    -> stable around 93-104 ms
    */
   @Test @Ignore // simple Performance setup
   public void performance() throws Exception {
      String content = fetch("javascript", "sun-spider.in.js");
      Map<String, Object> options = new HashMap<String, Object>();
      //OutputStream out = NullOutputStream.INSTANCE;
      Encoder encoder = new NullEncoder();
      //Encoder encoder = new TerminalEncoder(out, Syntax.defaultTheme(), new HashMap<String, Object>());

      Scanner scanner = new JavaScriptScanner();
      for(int i = 0 ; i < 60; i++) {
         long start = System.currentTimeMillis();
         scanner.scan(new StringScanner(content), encoder, options);
         System.out.println(i + " [" + (System.currentTimeMillis()-start) + "]");
      }
   }
}