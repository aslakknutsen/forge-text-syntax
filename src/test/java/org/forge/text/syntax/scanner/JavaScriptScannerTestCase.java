package org.forge.text.syntax.scanner;

import org.forge.text.syntax.Scanner;
import org.forge.text.syntax.Syntax.Builder;
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

   @Test @Ignore // don't handle xml in javascript
   public void shoulMatchJavaScriptTraceTestExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", "trace-test.in.js");
   }

   @Test @Ignore // don't handle xml in javascript
   public void shoulMatchJavaScriptXMLExample() throws Exception {
      assertMatchExample(
            Builder.create()
            .scannerType(Scanner.Type.JAVA_SCRIPT), "javascript", " xml.in.js");
   }
}