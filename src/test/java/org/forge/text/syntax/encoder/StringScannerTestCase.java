package org.forge.text.syntax.encoder;

import java.util.regex.Pattern;

import org.forge.text.syntax.StringScanner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class StringScannerTestCase {

   @Test
   public void shouldTrackIndex() throws Exception {
      String test = "abcde";

      StringScanner scan = new StringScanner(test);
      Assert.assertTrue(scan.hasMore());

      // peek at the future, don't advance
      Assert.assertEquals("ab", scan.peek(2));

      // get and advance
      Assert.assertEquals("a", scan.next());
      Assert.assertEquals("b", scan.next());

      // twice to make sure we did not advance
      Assert.assertTrue(scan.check("c") != null);
      Assert.assertTrue(scan.check("c") != null);

      Assert.assertEquals("cd", scan.scan("cd").group());

      Assert.assertEquals("e", scan.next());
      Assert.assertFalse(scan.hasMore());
   }

   @Test
   public void shouldScanUntil() throws Exception {
      String test = "aaaabc";

      StringScanner scan = new StringScanner(test);
      Assert.assertTrue(scan.hasMore());

      Assert.assertEquals("aaaab", scan.scanUntil("b").group());

      Assert.assertTrue(scan.hasMore());

      Assert.assertEquals("c", scan.next());
      Assert.assertFalse(scan.hasMore());
   }

   @Test @Ignore
   public void should2() throws Exception {

      String test = "package pl.silvermedia.ws";
      StringScanner scan = new StringScanner(test);

      Pattern p = Pattern.compile("[a-zA-Z_][A-Za-z_0-9]*|\\[\\]");

      System.out.println(scan.scan(p).group());
   }
}
