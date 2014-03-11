package org.forge.text.syntax.scanner;

import org.forge.text.syntax.Encoder;
import org.forge.text.syntax.encoder.AssertEncoder;

public abstract class AbstractScannerTestCase {

   public static final String ASSERT_ENCODER = "TEST";
   {
      Encoder.Factory.registrer(ASSERT_ENCODER, AssertEncoder.class);
   }

}
