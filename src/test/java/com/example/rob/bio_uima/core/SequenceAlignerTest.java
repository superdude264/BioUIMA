package com.example.rob.bio_uima.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SequenceAlignerTest {

   @Test
   public final void testComputeAlignments() {
      final SequenceAligner sa = new SequenceAligner(-2, -2, 2, -1);
      String[] res = sa.computeAlignments("AAGT", "AGT");
      assertEquals("AAGT", res[0]);
      assertEquals("-AGT", res[1]);
   }

   @Test
   public final void testNeedlemanWunsch() {
      final String s1 = "L";
      final String s2 = "NLN";
      final SequenceAligner sa = new SequenceAligner(-2, -2, 2, -1);
      StringBuilder[] res = sa.needlemanWunsch(new StringBuilder(s1), new StringBuilder(s2));
      final String res0 = res[0].toString();
      final String res1 = res[1].toString();
      assertEquals("-L-", res0);
      assertEquals("NLN", res1);
   }

}
