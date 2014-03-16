package org.forge.text.syntax.encoder;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
   public static final NullOutputStream INSTANCE = new NullOutputStream();

   private NullOutputStream() {
   }

   public void write(int b) throws IOException {
   }
}
