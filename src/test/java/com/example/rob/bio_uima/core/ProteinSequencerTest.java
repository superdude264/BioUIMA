package com.example.rob.bio_uima.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Rob
 * 
 */
public class ProteinSequencerTest {

   /**
    * Object under test. Should not be effected by multiple runs.
    */
   public static ProteinSequencer _seq = ProteinSequencer.getInstance();

   @Test(expected = IllegalArgumentException.class)
   public final void testSequence_WhenSequenceIsNull() {
      _seq.findLongestProteinOrf(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testSequence_WhenSequenceIsEmpty() {
      _seq.findLongestProteinOrf("");
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testSequence_WhenSequenceContainsInvalidCharacters() {
      // Only 'A', 'T', 'G', & 'C' are valid (case insensitive).
      _seq.findLongestProteinOrf("atz");
   }

   @Test
   public final void testSequence_WhenSequenceCharactersLowercase() {
      _seq.findLongestProteinOrf("att");
   }

   @Test
   public final void testSequence_WhenSequenceCharactersUppercase() {
      _seq.findLongestProteinOrf("ATT");
   }

   @Test
   public final void testSequence_WhenSequenceIsScxaButeu() {
      String protein = _seq
            .findLongestProteinOrf("GTTCGTGATGGTTATATTGCTGATGATAAAGATTGTGCTTATTTTTGTGGTCGTAATGCTTATTGTGATGAAGAATGTAAAAAAGGTGCTGAATCTGGTAAATGTTGGTATGCTGGTCAATATGGTAATGCTTGTTGGTGTTATAAACTTCCTGATTGGGTTCCTATTAAACAAAAAGTTTCTGGTAAATGTAAT");
      assertEquals("MVILLMIKIVLIFVVVMLIVMKNVKKVLNLVNVGMLVNMVMLVGVINFLIGFLLNKKFLVNV", protein);
   }

   @Test
   public final void testSequence_WhenSequenceIsScx1Titse() {
      String protein = _seq
            .findLongestProteinOrf("AAAGATGGTTATCCTGTTGAATATGATAATTGTGCTTATATTTGTTGGAATTATGATAATGCTTATTGTGATAAACTTTGTAAAGATAAAAAAGCTGATTCTGGTTATTGTTATTGGGTTCATATTCTTTGTTATTGTTATGGTCTTCCTGATTCTGAACCTACTAAAACTAATGGTAAATGTAAATCTGGTAAAAAA");
      assertEquals("MVILLNMIIVLIFVGIMIMLIVINFVKIKKLILVIVIGFIFFVIVMVFLILNLLKLMVNVNLVK", protein);
   }

   @Test
   public final void testSequence_WhenSequenceIsScx6Titse() {
      String protein = _seq
            .findLongestProteinOrf("CGTGAAGGTTATCCTGCTGATTCTAAAGGTTGTAAAATTACTTGTTTTCTTACTGCTGCTGGTTATTGTAATACTGAATGTACTCTTAAAAAAGGTTCTTCTGGTTATTGTGCTTGGCCTGCTTGTTATTGTTATGGTCTTCCTGAATCTGTTAAAATTTGGACTTCTGAAACTAATAAATGT");
      assertEquals("MVFLNLLKFGLLKLIN", protein);
   }

   @Test
   public final void testSequence_WhenSequenceIsScx1Cenno() {
      String protein = _seq
            .findLongestProteinOrf("AAAGATGGTTATCTTGTTGATGCTAAAGGTTGTAAAAAAAATTGTTATAAACTTGGTAAAAATGATTATTGTAATCGTGAATGTCGTATGAAACATCGTGGTGGTTCTTATGGTTATTGTTATGGTTTTGGTTGTTATTGTGAAGGTCTTTCTGATTCTACTCCTACTTGGCCTCTTCCTAATAAAACTTGTTCTGGTAAA");
      assertEquals("MKHRGGSYGYCYGFGCYCEGLSDSTPTWPLPNKTCSGK", protein);

   }

   @Test
   public final void testSequence_WhenSequenceIsSix2Leiqu() {
      String protein = _seq
            .findLongestProteinOrf("GATGGTTATATTCGTAAACGTGATGGTTGTAAACTTTCTTGTCTTTTTGGTAATGAAGGTTGTAATAAAGAATGTAAATCTTATGGTGGTTCTTATGGTTATTGTTGGACTTGGGGTCTTGCTTGTTGGTGTGAAGGTCTTCCTGATGAAAAAACTTGGAAATCTGAAACTAATACTTGTGGT");
      assertEquals("MVIFVNVMVVNFLVFLVMKVVIKNVNLMVVLMVIVGLGVLLVGVKVFLMKKLGNLKLILV", protein);
   }

}
