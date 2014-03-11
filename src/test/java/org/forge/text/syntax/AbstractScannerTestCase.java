package org.forge.text.syntax;

import org.forge.text.syntax.Encoder;

public abstract class AbstractScannerTestCase {

   public static final String DEBUG_ENCODER = "DEBUG";
   public static final String ASSERT_ENCODER = "TEST";
   {
      Encoder.Factory.registrer(DEBUG_ENCODER, DebugEncoder.class);
      Encoder.Factory.registrer(ASSERT_ENCODER, AssertEncoder.class);
   }

}
