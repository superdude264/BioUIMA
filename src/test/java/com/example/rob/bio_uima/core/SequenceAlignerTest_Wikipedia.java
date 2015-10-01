package com.example.rob.bio_uima.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * Running example from <a href="http://en.wikipedia.org/w/index.php?title=Hirschberg%27s_algorithm&oldid=621812465">the
 * Wikipedia page on Hirschberg's algorithm (pulled 8/26/2014)</a>.
 * 
 */
public class SequenceAlignerTest_Wikipedia {

   /**
    * Object under test. Should not be effected by multiple runs.
    */
   public static SequenceAligner _sa = new SequenceAligner(-2, -2, 2, -1);

   @Test
   public final void testComputeAlignments() {
      String[] res = _sa.computeAlignments("AGTACGCA", "TATGC");
      assertEquals("AGTACGCA", res[0]);
      assertEquals("--TATGC-", res[1]);
   }

   @Test
   public final void testNeedlemanWunsch() {
      final StringBuilder seq1 = new StringBuilder("AGTACGCA");
      final StringBuilder seq2 = new StringBuilder("TATGC");
      StringBuilder[] result = _sa.needlemanWunsch(seq1, seq2);
      assertEquals("AGTACGCA", result[0].toString());
      assertEquals("--TATGC-", result[1].toString());
   }

   @Test
   public final void testComputeCostMatrix() {
      final String exp = "[[0, -2, -4, -6, -8, -10], [-2, -1, 0, -2, -4, -6], [-4, -3, -2, -1, 0, -2], [-6, -2, -4, 0, -2, -1], [-8, -4, 0, -2, -1, -3], [-10, -6, -2, -1, -3, 1], [-12, -8, -4, -3, 1, -1], [-14, -10, -6, -5, -1, 3], [-16, -12, -8, -7, -3, 1]]";
      final StringBuilder seq1 = new StringBuilder("AGTACGCA");
      final StringBuilder seq2 = new StringBuilder("TATGC");
      String actual = Arrays.deepToString(_sa.computeCostMatrix(seq1, seq2));
      assertEquals(exp, actual);
   }

}
